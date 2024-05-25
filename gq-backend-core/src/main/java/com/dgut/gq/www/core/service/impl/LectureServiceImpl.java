package com.dgut.gq.www.core.service.impl;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.core.common.config.RabbitmqConfig;
import com.dgut.gq.www.core.common.model.dto.LectureDto;
import com.dgut.gq.www.core.common.model.entity.Lecture;
import com.dgut.gq.www.core.common.model.entity.UserLectureInfo;
import com.dgut.gq.www.core.common.model.vo.LectureReviewVo;
import com.dgut.gq.www.core.common.model.vo.LectureTrailerVo;
import com.dgut.gq.www.core.common.model.vo.LectureVo;
import com.dgut.gq.www.core.common.model.vo.UserVo;
import com.dgut.gq.www.core.common.mq.CustomCorrelationData;
import com.dgut.gq.www.core.common.util.RecordRobTicketErrorUtil;
import com.dgut.gq.www.core.mapper.LectureMapper;
import com.dgut.gq.www.core.mapper.RecordRobTicketErrorMapper;
import com.dgut.gq.www.core.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.core.mapper.UserMapper;
import com.dgut.gq.www.core.service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 讲座操作
 * @author hyj
 * @version 1.0
 * @since  2022-9-16
 */
@Service
@Slf4j
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

    @Autowired
    private RecordRobTicketErrorMapper recordRobTicketErrorMapper;

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
        // 尝试从Redis获取讲座信息
        String key = RedisGlobalKey.UNSTART_LECTURE;
        String str = stringRedisTemplate.opsForValue().get(key);
        LectureVo lectureVo = Optional.ofNullable(str)
                .map(s -> JSONUtil.toBean(s, LectureVo.class))
                .orElseGet(() -> {
                    // 从数据库中查询最新未结束的讲座
                    Lecture lecture = lectureMapper.selectOne(
                            new LambdaQueryWrapper<Lecture>()
                                    .orderByDesc(Lecture::getCreateTime)
                                    .eq(Lecture::getIsDeleted, 0)
                                    .last("LIMIT 1")
                    );

                    // 检查是否查询到讲座
                    if (lecture == null || lecture.getLectureName() == null) {
                        throw new GlobalSystemException(999, "还没新的讲座");
                    }

                    // 转换为LectureVo并存入Redis
                    LectureVo newLectureVo = new LectureVo();
                    BeanUtils.copyProperties(lecture, newLectureVo);
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newLectureVo));
                    stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER, newLectureVo.getTicketNumber().toString());

                    return newLectureVo;
                });

        // 获取票的数量
        String ticketNumberStr = Optional.ofNullable(stringRedisTemplate.opsForValue()
                        .get(RedisGlobalKey.TICKET_NUMBER))
                        .orElse(
                         String.valueOf(lectureVo.getTicketNumber())
                        );
        lectureVo.setTicketNumber(Integer.parseInt(ticketNumberStr));

        return SystemJsonResponse.success(lectureVo);
    }

    /**
     * 抢票
     * @param
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse robTicket(String openid,String lectureId) {
        String key = RedisGlobalKey.UNSTART_LECTURE ;
        String str = stringRedisTemplate.opsForValue().get(key);
        LocalDateTime grabTicketsStart = JSONUtil.toBean(str, LectureVo.class).getGrabTicketsStart();
        if(grabTicketsStart.isAfter(LocalDateTime.now())){
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"抢票时间未到");
        }

        RLock lock = redissonClient.getLock(key + openid);
        boolean b;
        try {
            b = lock.tryLock(5,10, TimeUnit.SECONDS);
            if(!b)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"抢票失败");
        } catch (InterruptedException e) {
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"抢票失败");
        }

        try{
            //执行lua脚本
            Long execute = stringRedisTemplate.execute(
                    SECKILL_SCRIPT, Collections.emptyList(), lectureId, openid);

            assert execute != null;
            int  re = execute.intValue();
            if(re != 0){
                return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),re == 1?"票已经抢光":"不能重复抢票");
            }
            SendMsg(openid,lectureId);
        }finally {
            lock.unlock();
        }

        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"抢票成功");
    }

    private void SendMsg(String openid, String lectureId) {
        UserLectureInfo userLectureInfo = buildUserLectureInfo(openid, lectureId);
        CustomCorrelationData correlationData = new CustomCorrelationData(openid, lectureId);
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            //springamqp内部处理future的异常（很少出现），不是投递失败
            public void onFailure(Throwable throwable) {
                log.error("发送消息失败！！", throwable);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                //Future接收到回执的处理逻辑，参数中的result就是回执内容
                if (result.isAck()) {
                    log.debug("发送消息成功，收到 ack");
                } else { // result.getReason()，String类型，返回nack时的异常描述
                    log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
                    String openid;
                    String lectureId;
                    openid = correlationData.getOpenid();
                    lectureId = correlationData.getLectureId();
                    String key = RedisGlobalKey.SEND_EXCHANGE_FAIL + openid;
                    String str = stringRedisTemplate.opsForValue().get(key);
                    int value = 0;
                    if(str != null){
                        value = Integer.parseInt(str);
                    }
                    if(value == 1){
                        //记录错误日志
                        RecordRobTicketErrorUtil.recordError(recordRobTicketErrorMapper, openid, lectureId, 0);
                    }else{
                        stringRedisTemplate.opsForValue().set(key, String.valueOf(value + 1));
                        stringRedisTemplate.expire(key, 20, TimeUnit.MINUTES);
                        //重发消息
                        rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE, "gqyyds",
                                JSONUtil.toJsonStr(userLectureInfo), correlationData);
                    }
                }
            }
        });
        // 发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE, "gqyyds",
                JSONUtil.toJsonStr(userLectureInfo), correlationData);
    }

    private UserLectureInfo buildUserLectureInfo(String openid, String lectureId) {
        UserLectureInfo userLectureInfo = new UserLectureInfo();
        userLectureInfo.setUpdateTime(LocalDateTime.now());
        userLectureInfo.setCreateTime(LocalDateTime.now());
        userLectureInfo.setLectureId(lectureId);
        userLectureInfo.setOpenid(openid);
        return  userLectureInfo;
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
        Page<Lecture> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Lecture> lectureLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加过滤条件 - 模糊查询
        Optional.ofNullable(name).ifPresent(
                n -> lectureLambdaQueryWrapper.and(
                        wrapper -> wrapper
                                .like(Lecture::getGuestName, name)
                                .or()
                                .like(Lecture::getReviewName, name)
                )
        );
        lectureLambdaQueryWrapper.orderByDesc(Lecture::getCreateTime)
                                 .eq(Lecture::getIsDeleted, 0)
                                 .ne(Lecture::getReviewName, "");

        lectureMapper.selectPage(pageInfo, lectureLambdaQueryWrapper);
        List<LectureReviewVo> lectureVos = pageInfo.getRecords().stream()
                .filter(record -> record.getReviewName() != null)
                .map(record -> {
                    LectureReviewVo lectureVo = new LectureReviewVo();
                    BeanUtils.copyProperties(record, lectureVo);
                    return lectureVo;
                })
                .collect(Collectors.toList());
        SystemResultList systemResultList = new SystemResultList(Collections.singletonList(lectureVos), (int) pageInfo.getTotal());

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
        Page<Lecture> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<Lecture> lectureLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Optional.ofNullable(name).ifPresent(
                n -> lectureLambdaQueryWrapper.and(
                        wrapper ->wrapper
                                .like(Lecture::getGuestName, name)
                                .or()
                                .like(Lecture::getLectureName,name)
                )
        );

        lectureLambdaQueryWrapper.orderByDesc(Lecture::getCreateTime)
                                 .eq(Lecture::getIsDeleted,0);
        lectureMapper.selectPage(pageInfo,lectureLambdaQueryWrapper);
        Integer count = lectureMapper.selectCount(lectureLambdaQueryWrapper);

        List<LectureTrailerVo> lectureVos = pageInfo.getRecords().stream()
                .filter(record -> record.getLectureName() != null)
                .map(record -> {
                    LectureTrailerVo lectureVo = new LectureTrailerVo();
                    BeanUtils.copyProperties(record, lectureVo);
                    return lectureVo;
                })
                .collect(Collectors.toList());
        SystemResultList systemResultList  = new SystemResultList(lectureVos,count);

        return SystemJsonResponse.success(systemResultList);
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
        Page<UserLectureInfo> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<UserLectureInfo> lectureInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getLectureId, id);

        //如果是1查询参加讲座的
        if(status == 1){
            lectureInfoLambdaQueryWrapper.ge(UserLectureInfo::getStatus,1);
        }
        userLectureInfoMapper.selectPage(pageInfo,lectureInfoLambdaQueryWrapper);
        Integer count = userLectureInfoMapper.selectCount(lectureInfoLambdaQueryWrapper);

        List<UserLectureInfo> records = pageInfo.getRecords();
        List<String> userOpenidList = records.stream()
                .map(UserLectureInfo::getOpenid)
                .collect(Collectors.toList());

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getOpenid,userOpenidList);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);

        List<UserVo> userVoList = users.stream()
                .map(user -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    return userVo;
                })
                .collect(Collectors.toList());
        SystemResultList systemResultList = new SystemResultList(userVoList,count);

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
        if (id == null|| id.equals("")) {
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
            Optional.ofNullable(stringRedisTemplate.opsForValue().get(key))
                    .map(lec -> JSONUtil.toBean(lec, Lecture.class))
                    .ifPresent(lec -> {
                        if(lec.getId() .equals(id)){
                            stringRedisTemplate.delete(key);
                            //更新票的数量
                            stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER, lectureDto.getTicketNumber().toString());
                        }
                    });
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
        Page<Lecture> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<Lecture> lectureLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Optional.ofNullable(name).ifPresent(
            n -> lectureLambdaQueryWrapper.and(
                    wrapper ->wrapper
                            .like(Lecture::getGuestName, name)
                            .or()
                            .like(Lecture::getIntroduction,name)
            )
        );
        lectureLambdaQueryWrapper.eq(Lecture::getIsDeleted,0);
        lectureLambdaQueryWrapper.orderByDesc(Lecture::getCreateTime);
        lectureMapper.selectPage(pageInfo,lectureLambdaQueryWrapper);
        Integer count = lectureMapper.selectCount(lectureLambdaQueryWrapper);

        List<Lecture> records = pageInfo.getRecords();
        List<Object> lectureVos = records.stream()
                .map(lecture -> {
                    LectureVo lectureVo = new LectureVo();
                    BeanUtils.copyProperties(lecture, lectureVo);
                    return lectureVo;
                })
                .collect(Collectors.toList());
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
        List<UserLectureInfo> records = userLectureInfoMapper.selectList(lectureInfoLambdaQueryWrapper);
        List<String> userOpenidList = records.stream()
                .map(UserLectureInfo::getOpenid)
                .collect(Collectors.toList());

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getOpenid,userOpenidList);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);

        List<UserVo> userVoList = users.stream()
                .map(user -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    return userVo;
                })
                .collect(Collectors.toList());
        Integer count = userVoList.size();
        SystemResultList systemResultList = new SystemResultList(userVoList,count);

        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 删除讲座
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse deleteLecture(String id) {
        // 更新讲座记录
        Lecture lecture = new Lecture();
        lecture.setIsDeleted(1);
        lecture.setId(id);
        lectureMapper.updateById(lecture);

        // 检查Redis中的讲座记录是否需要更新
        Optional.ofNullable(stringRedisTemplate.opsForValue().get(RedisGlobalKey.UNSTART_LECTURE))
                .map(lec -> JSONUtil.toBean(lec, Lecture.class))
                .ifPresent(lec -> {
                    if (lec.getId().equals(id)) {
                        stringRedisTemplate.delete(RedisGlobalKey.IS_GRAB_TICKETS);
                        stringRedisTemplate.delete(RedisGlobalKey.UNSTART_LECTURE);
                        stringRedisTemplate.delete(RedisGlobalKey.TICKET_NUMBER);
                    }
                });

        return SystemJsonResponse.success();
    }

}


