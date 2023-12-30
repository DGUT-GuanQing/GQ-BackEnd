package com.dgut.gq.www.core.service.impl;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.core.config.RabbitmqConfig;
import com.dgut.gq.www.core.mapper.LectureMapper;
import com.dgut.gq.www.core.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.core.mapper.UserMapper;
import com.dgut.gq.www.core.model.dto.LectureDto;
import com.dgut.gq.www.core.model.entity.Lecture;
import com.dgut.gq.www.core.model.entity.UserLectureInfo;
import com.dgut.gq.www.core.model.vo.LectureReviewVo;
import com.dgut.gq.www.core.model.vo.LectureTrailerVo;
import com.dgut.gq.www.core.model.vo.LectureVo;
import com.dgut.gq.www.core.model.vo.UserVo;
import com.dgut.gq.www.core.service.LectureService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 讲座操作
 * @author hyj
 * @version 1.0
 * @since  2022-9-16
 */
@Service
public class LectureServiceImpl  implements LectureService {
    @Autowired
    private LectureMapper lectureMapper;





    @Autowired
    private UserMapper userMapper;


    @Autowired
    private UserLectureInfoMapper userLectureInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedissonClient redissonClient;


    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 加载lua脚本代码
     * @param voucherId
     * @return
     */
    private  static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }


    /**
     * 获取还没开始的讲座信息
     *
     * @return
     */
    @Override
    public SystemJsonResponse findUnStartLecture() {
        LectureVo lectureVo = new LectureVo();
        //在redis查询
        String key = RedisGlobalKey.UNSTART_LECTURE;
        String str = stringRedisTemplate.opsForValue().get(key);
        lectureVo = JSONUtil.toBean(str,LectureVo.class);
        Lecture lecture ;
        //查询失败  就在数据库里查询未结束的讲座
        if(str == null || str.equals("")){
            //查找最新讲座
            LambdaQueryWrapper<Lecture> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.orderByDesc(Lecture::getCreateTime);
            lambdaQueryWrapper.last("LIMIT 1");
            //未被删除
            lambdaQueryWrapper.eq(Lecture::getIsDeleted,0);
            lecture = lectureMapper.selectOne(lambdaQueryWrapper);
            if(lecture.getLectureName() == null)return SystemJsonResponse.fail(999,"还没新的讲座");
            //存入redis
            BeanUtils.copyProperties(lecture,lectureVo);
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(lectureVo));
            stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER,lectureVo.getTicketNumber().toString());
         }
        //获取票的数量
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.TICKET_NUMBER);
        Integer count  = Integer.valueOf(s);
        lectureVo.setTicketNumber(count);
        return SystemJsonResponse.success(lectureVo);
    }

    /**
     * 抢票
     * @param
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse robTicket(String openid,String id) {
        LectureVo lectureVo = new LectureVo();
        //在redis查询
        String key1 = RedisGlobalKey.UNSTART_LECTURE;
        String str1 = stringRedisTemplate.opsForValue().get(key1);
        lectureVo = JSONUtil.toBean(str1,LectureVo.class);
        if(lectureVo.getGrabTicketsStart().isAfter(LocalDateTime.now())){
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"抢票时间未到，已经被拉入黑名单");
        }
        String key = RedisGlobalKey.LOCK_USER + openid;
        //获取锁
        RLock lock = redissonClient.getLock(key);
        boolean b = lock.tryLock();
        if(!b)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"抢票失败");
        try{
            //执行lua脚本
            Long execute = stringRedisTemplate.execute(
                    SECKILL_SCRIPT, Collections.emptyList(), id, openid);

            int  re = execute.intValue();
            //判断是否为0
            //不为0没有抢票资格资格
            if(re != 0){
                return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),re == 1?"票已经抢光":"不能重复抢票");
            }
            //生成讲座用户信息
            UserLectureInfo lectureInfo =new UserLectureInfo();
            lectureInfo.setUpdateTime(LocalDateTime.now());
            lectureInfo.setCreateTime(LocalDateTime.now());
            lectureInfo.setLectureId(id);
            lectureInfo.setOpenid(openid);

            //将信息传入rabbitmq
           String  str = JSONUtil.toJsonStr(lectureInfo);
            rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE,"gqyyds");
        }finally {
            //释放锁
            lock.unlock();

        }

        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"抢票成功");

    }

    /**
     * 获取讲座回顾
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public SystemJsonResponse getLectureReview(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Lecture> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Lecture> lq = new LambdaQueryWrapper<>();
        //添加过滤条件  模糊查询
        if (name != null) {
            lq.and(wrapper ->wrapper.
                    like(Lecture::getGuestName, name)
                    .or()
                    .like(Lecture::getReviewName,name)
            );
        }
        //排序条件
        lq.orderByDesc(Lecture::getCreateTime);
        lq.eq(Lecture::getIsDeleted,0);
        lq.ne(Lecture::getReviewName,"");
        //查询
        lectureMapper.selectPage(pageInfo,lq);
        Integer count = lectureMapper.selectCount(lq);
        //对象转换
        List<Lecture> records = pageInfo.getRecords();
        List<LectureReviewVo>lectureVos = new ArrayList<>();
        //转换为vo
        for (Lecture record : records) {
            if(record.getReviewName() == null)continue;;
            LectureReviewVo lectureVo = new LectureReviewVo();
            BeanUtils.copyProperties(record,lectureVo);
            lectureVos.add(lectureVo);
        }
        //包装对象
        SystemResultList systemResultList  = new SystemResultList(Collections.singletonList(lectureVos),count);
        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 讲座预告
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public SystemJsonResponse getLectureTrailer(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Lecture> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Lecture> lq = new LambdaQueryWrapper<>();
        //添加过滤条件  模糊查询
        if (name != null) {
            lq.and(wrapper ->wrapper.
                    like(Lecture::getGuestName, name)
                    .or()
                    .like(Lecture::getLectureName,name)
            );
        }
        //排序条件
        lq.orderByDesc(Lecture::getCreateTime);
        lq.eq(Lecture::getIsDeleted,0);
        //查询
        lectureMapper.selectPage(pageInfo,lq);
        Integer count = lectureMapper.selectCount(lq);
        //对象转换
        List<Lecture> records = pageInfo.getRecords();
        List<LectureTrailerVo>lectureVos = new ArrayList<>();
        //转换为vo
        for (Lecture record : records) {
            LectureTrailerVo lectureVo = new LectureTrailerVo();
            BeanUtils.copyProperties(record,lectureVo);
            lectureVos.add(lectureVo);
        }
        //包装对象
        SystemResultList systemResultList  = new SystemResultList(lectureVos,count);
        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 扫码签到
     * @param openid
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse ScanCheckin(String openid, String id) {
        //判断用户有没有是否有抢到票
        Boolean member = stringRedisTemplate.opsForSet().isMember(RedisGlobalKey.IS_GRAB_TICKETS, openid);
        if(member == null || !member) return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"没有相关抢票记录，扫码失败");
        String key = RedisGlobalKey.LOCK_USER + openid;
        //获取锁
        RLock lock = redissonClient.getLock(key);
        boolean b = lock.tryLock();
        if(!b)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"扫码失败");
        try{
            UserLectureInfo lectureInfo = new UserLectureInfo();
            lectureInfo.setOpenid(openid);
            lectureInfo.setLectureId(id);
            //将信息传入rabbitmq
            String  str = JSONUtil.toJsonStr(lectureInfo);
            rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_SCAN_CHECKIN_EXCHANGE,"gqyyds",str);
        }catch (Exception e){
            throw  new GlobalSystemException(GlobalResponseCode.OPERATE_FAIL.getCode(), "扫码失败");
        }
        finally {
            //释放锁
            lock.unlock();
        }

        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"扫码成功");
    }


    /**
     * 扫码签退
     * @param openid
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse ScanCheckout(String openid, String id) {
        //判断用户有没有是否有抢到票
        Boolean member = stringRedisTemplate.opsForSet().isMember(RedisGlobalKey.IS_GRAB_TICKETS, openid);
        if(member == null || !member) return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"没有相关抢票记录，扫码失败");
        String key = RedisGlobalKey.LOCK_USER + openid;
        //获取锁
        RLock lock = redissonClient.getLock(key);
        boolean b = lock.tryLock();
        if(!b)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"扫码失败");
        try{
            UserLectureInfo lectureInfo = new UserLectureInfo();
            lectureInfo.setOpenid(openid);
            lectureInfo.setLectureId(id);
            //将信息传入rabbitmq
            String  str = JSONUtil.toJsonStr(lectureInfo);
            rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_SCAN_CHECKOUT_EXCHANGE,"gqyyds",str);
        }catch (Exception e){
            throw  new GlobalSystemException(GlobalResponseCode.OPERATE_FAIL.getCode(), "扫码失败");
        }
        finally {
            //释放锁
            lock.unlock();
        }

        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"扫码成功");
    }

    /**
     * 获取参加讲座的用户信息
     * @param page
     * @param pageSize
     * @param id
     * @param status
     * @return
     */
    @Override
    public SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status) {
        //构造分页构造器
        Page<UserLectureInfo> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<UserLectureInfo> lectureInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getLectureId, id);
        //如果是1查询参加讲座的
        if(status == 1){
            lectureInfoLambdaQueryWrapper.ge(UserLectureInfo::getStatus,1);
        }
        //查询
        userLectureInfoMapper.selectPage(pageInfo,lectureInfoLambdaQueryWrapper);
        Integer count = userLectureInfoMapper.selectCount(lectureInfoLambdaQueryWrapper);
        //实际返回的构造器
        Page<UserVo>userPage=new Page<>();
        //对象转换  忽略records
        BeanUtils.copyProperties(pageInfo,userPage,"records");

        //把信息封装到List并加入到userPage
        List<UserVo>list = new ArrayList<>();
        List<UserLectureInfo> records = pageInfo.getRecords();
        LambdaQueryWrapper<User> lambdaQueryWrapper;

        for(UserLectureInfo k:records){
            String openid = k.getOpenid();
            lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getOpenid,openid);
            User user = userMapper.selectOne(lambdaQueryWrapper);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            list.add(userVo);
        }
        userPage.setRecords(list);
        //封装返回结果
        SystemResultList systemResultList = new SystemResultList(list,count);
        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 新增或者更新讲座
     * @param lectureDto
     */
    @Override
    public SystemJsonResponse updateSaveLecture(LectureDto lectureDto) {
        String key = RedisGlobalKey.UNSTART_LECTURE;
        Lecture lecture = new Lecture();
        BeanUtils.copyProperties(lectureDto, lecture);
        lecture.setUpdateTime(LocalDateTime.now());
        String id = lectureDto.getId();
        String state;
        //新增
        if (id == null) {
            //插入数据库
            lecture.setCreateTime(LocalDateTime.now());
            lectureMapper.insert(lecture);
            //删除原来抢票的人
            stringRedisTemplate.delete(RedisGlobalKey.IS_GRAB_TICKETS);
            //删除讲座
            stringRedisTemplate.delete(RedisGlobalKey.UNSTART_LECTURE);
            state = "新增成功";
        } else {
            lectureMapper.updateById(lecture);
            //看redis的讲座是否要更新
            String s = stringRedisTemplate.opsForValue().get(key);
            Lecture lec = JSONUtil.toBean(s, Lecture.class);
            //如果当前更新的讲座是还没开始的就更新redis
            if (s != null && !s.equals("") && lec != null && lec.getId().equals(lectureDto.getId())) {
                stringRedisTemplate.delete(key);
                //更新票的数量
                stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER, lectureDto.getTicketNumber().toString());
            }
            state = "更新成功";
        }
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),state);
    }


    /**
     * 后台获取讲座信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public SystemJsonResponse getLecture(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Lecture> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Lecture> lq = new LambdaQueryWrapper<>();
        //添加过滤条件  模糊查询
        if (name != null) {
            lq.or();
            lq.like(Lecture::getGuestName, name);
            lq.like(Lecture::getIntroduction,name);
        }
        //排序条件
        lq.eq(Lecture::getIsDeleted,0);
        lq.orderByDesc(Lecture::getCreateTime);
        //查询
        lectureMapper.selectPage(pageInfo,lq);
        Integer count = lectureMapper.selectCount(lq);
        //对象转换
        List<Lecture> records = pageInfo.getRecords();
        List<LectureVo>lectureVos = new ArrayList<>();
        //转换为vo
        for (Lecture record : records) {
            LectureVo lectureVo = new LectureVo();
            BeanUtils.copyProperties(record,lectureVo);
            lectureVos.add(lectureVo);
        }
        //包装对象
        SystemResultList systemResultList  = new SystemResultList(lectureVos,count);
        return SystemJsonResponse.success(systemResultList);
    }


    /**
     * 导出参加讲座的用户信息
     * @param id
     * @param status
     * @return
     */
    @Override
    public SystemJsonResponse exportAttendLectureUser(String id, Integer status) {
        //条件构造器
        LambdaQueryWrapper<UserLectureInfo> lectureInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getLectureId,id);
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getIsDeleted,0);
        //如果是1查询参加讲座的
        if(status == 1){
            lectureInfoLambdaQueryWrapper.ge(UserLectureInfo::getStatus,1);
        }
        List<UserVo>list = new ArrayList<>();
        List<UserLectureInfo> records = userLectureInfoMapper.selectList(lectureInfoLambdaQueryWrapper);
        LambdaQueryWrapper<User> lambdaQueryWrapper;
        for(UserLectureInfo k:records){
            String openid = k.getOpenid();
            lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getOpenid,openid);
            User user = userMapper.selectOne(lambdaQueryWrapper);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            list.add(userVo);
        }
        Integer count = list.size();
        //封装返回结果
        SystemResultList systemResultList = new SystemResultList();
        systemResultList.setList(list);
        systemResultList.setCount(count);
        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 删除讲座
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse deleteLecture(String id) {
        Lecture lecture = new Lecture();
        lecture.setIsDeleted(1);
        lecture.setId(id);
        lectureMapper.updateById(lecture);
        //看redis的讲座是否要更新
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.UNSTART_LECTURE);
        Lecture lec = JSONUtil.toBean(s, Lecture.class);
        //如果当前删除的讲座是还没开始的就删除redis
        if (s != null && !s.equals("") && lec != null && lec.getId().equals(id) ){
            stringRedisTemplate.delete(RedisGlobalKey.IS_GRAB_TICKETS);
            stringRedisTemplate.delete(RedisGlobalKey.UNSTART_LECTURE);
            stringRedisTemplate.delete(RedisGlobalKey.TICKET_NUMBER);
        }
        return SystemJsonResponse.success();
    }


    /**
     * 设置互斥锁
     * @param key
     * @return
     */
    public  boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key
     */
    public  void unlock(String key){
        stringRedisTemplate.delete(key);
    }


}


