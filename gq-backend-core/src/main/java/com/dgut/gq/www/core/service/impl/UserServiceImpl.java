package com.dgut.gq.www.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.model.entity.LoginUser;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.common.util.JwtUtil;
import com.dgut.gq.www.core.mapper.LectureMapper;
import com.dgut.gq.www.core.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.core.mapper.UserMapper;
import com.dgut.gq.www.core.model.dto.UserDto;
import com.dgut.gq.www.core.model.entity.Lecture;
import com.dgut.gq.www.core.model.entity.UserLectureInfo;
import com.dgut.gq.www.core.model.vo.MyLectureVo;
import com.dgut.gq.www.core.model.vo.UserVo;
import com.dgut.gq.www.core.service.UserService;
import com.dgut.gq.www.core.util.HttpUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 用户模块
 * @author hyj
 * @version 1.0
 * @since  2022-9-16
 */
@Service
public class UserServiceImpl implements UserService {



    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LectureMapper lectureMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserLectureInfoMapper userLectureInfoMapper;

    @Autowired
    private HttpUtil httpUtil;


    /**
     * 微信登录
     * @param code
     * @return
     */
    @Override
    public String wxLogin(String code) {
        //获取用户的openid
        String openid = httpUtil.getOpenid(code);
        //String openid = code;
        LambdaQueryWrapper<User>lq=new LambdaQueryWrapper<>();
        lq.eq(User::getOpenid,openid);
        User user = userMapper.selectOne(lq);
        Optional<User> optionalUser=Optional.ofNullable(user);

        //第一次登录
        if(!optionalUser.isPresent()){
            user = new User();
            user.setOpenid(openid);
            user.setUpdateTime(LocalDateTime.now());
            user.setCreateTime(LocalDateTime.now());
            user.setPermission("user");
            userMapper.insert(user);
        }else{
            user.setCreateTime(null);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }

        // 把全部数据封装为LoginUser存入redis  方便后续权限的管理
        List<String> list = new ArrayList<>();
        list.add(user.getPermission());
        user = new User();
        user.setOpenid(openid);
        LoginUser loginUser = new LoginUser(user,list);

        //封装权限
        String key = RedisGlobalKey.PERMISSION+openid;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(loginUser));
        stringRedisTemplate.expire(key,1,TimeUnit.DAYS);
        //加密openid，返回token
        return  JwtUtil.createJWT(openid);
    }



    /**
     * 获取小程序端个人信息
     *
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMe(String openid) {
        //从redis查询
        String key = RedisGlobalKey.USER_MESSAGE+openid;
        String str = stringRedisTemplate.opsForValue().get(key);
        UserVo userVo = JSONUtil.toBean(str,UserVo.class);
        Optional<UserVo> optionalUser=Optional.ofNullable(userVo);

        //查询失败
        if(!optionalUser.isPresent() || userVo.getStudentId() == null){
            userVo = new UserVo();
            LambdaQueryWrapper<User> lq =new LambdaQueryWrapper<>();
            lq.eq(User::getOpenid, openid);

            //查询出对应用户信息
            User user = userMapper.selectOne(lq);
            BeanUtils.copyProperties(user,userVo);
            //存入redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(userVo));
            stringRedisTemplate.expire(key,360,TimeUnit.DAYS);
        }
        return SystemJsonResponse.success(userVo);
    }

    /**
     * 获取自己参加过的讲座信息
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMyLecture(String openid, Integer page, Integer pageSize) {
        //查询数据库
        Page<UserLectureInfo> pageInfo =  new Page<>(page,pageSize);
        LambdaQueryWrapper<UserLectureInfo>lectureInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //条件
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getOpenid,openid);
        //时间排序
        lectureInfoLambdaQueryWrapper.orderByDesc(UserLectureInfo::getCreateTime);
        //未被删除
        lectureInfoLambdaQueryWrapper.eq(UserLectureInfo::getIsDeleted,0);
        //查询数量
        Integer count = userLectureInfoMapper.selectCount(lectureInfoLambdaQueryWrapper);
        //分页查询
        userLectureInfoMapper.selectPage(pageInfo,lectureInfoLambdaQueryWrapper);
        List<UserLectureInfo> records = pageInfo.getRecords();
        //记录当前讲座的观看情况
        HashMap< String,Integer>map = new HashMap<>();
        //获取所有讲座集合
        List<String >list = new ArrayList<>();
        for (UserLectureInfo record : records) {
            list.add(record.getLectureId());
            map.put(record.getLectureId(),record.getStatus());
        }
        //拼接字符串
        String s = StrUtil.join(",",list);
        //返回结果集
        List<MyLectureVo>lectureVos = new ArrayList<>();
        LambdaQueryWrapper<Lecture>lectureLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(list.size() > 0){
            lectureLambdaQueryWrapper.in(Lecture::getId,list);
            lectureLambdaQueryWrapper.eq(Lecture::getIsDeleted,0);
            lectureLambdaQueryWrapper.last("ORDER BY FIELD(id," + s + ")");
            List<Lecture> lectures = lectureMapper.selectList(lectureLambdaQueryWrapper);
            for (Lecture lecture : lectures) {
                MyLectureVo lectureVo = new MyLectureVo();
                BeanUtil.copyProperties(lecture,lectureVo);
                //添加观看状态
                lectureVo.setStatus(map.get(lecture.getId()));
                lectureVos.add(lectureVo);
            }
        }
        //封装返回结果
        SystemResultList systemResultList = new SystemResultList(lectureVos,count);
        return SystemJsonResponse.success(systemResultList);
    }




    /**
     * 是否抢到票
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse isGrabTicket(String openid) {
        String key = RedisGlobalKey.IS_GRAB_TICKETS;
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, openid);
        Boolean flag = true;
        if(member == null || !member){
            flag  = false;
        }
        return SystemJsonResponse.success(flag);
    }


    /**
     * 中央认证登陆
     * @param userDto
     * @return
     */
    @Override
    public SystemJsonResponse dgutLogin(UserDto userDto, String openid) {
        //将中央认证信息更新到数据库
        String key = RedisGlobalKey.USER_MESSAGE + openid;
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenid,openid);
        User user = new User();
        BeanUtils.copyProperties(userDto,user);
        userMapper.update(user,lambdaQueryWrapper);
        //删除redis中的信息
        stringRedisTemplate.delete(key);
        //设置中央认证缓存
        stringRedisTemplate.opsForValue().set(RedisGlobalKey.DGUT_LOGIN + openid,openid);
        return SystemJsonResponse.success();
    }


    /**
     * 签到
     * @param lectureId
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse signIn(String lectureId, String openid) {
        LambdaQueryWrapper<UserLectureInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserLectureInfo::getLectureId,lectureId);
        lambdaQueryWrapper.eq(UserLectureInfo::getOpenid,openid);
        //先查询用户有没有抢到讲座
        Integer count = userLectureInfoMapper.selectCount(lambdaQueryWrapper);
        if(count == null|| count == 0)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"没有相关讲座记录");
        UserLectureInfo lectureInfo = userLectureInfoMapper.selectOne(lambdaQueryWrapper);
        lectureInfo.setStatus(lectureInfo.getStatus() + 1);
        LambdaQueryWrapper<User> userLambdaQueryWrapper =new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getOpenid,openid);
        //更新个人观看讲座记录
        LambdaUpdateWrapper<User> updateWrapper = new UpdateWrapper<User>().lambda()
                .setSql("race_number = race_number + 1")
                .eq(User::getOpenid,openid);
        userMapper.update(null,updateWrapper);
        userLectureInfoMapper.updateById(lectureInfo);
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"签到成功");
    }


    /**
     * 是否在黑名单
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse isBlack(String openid) {
        Boolean flag = false;
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.PERMISSION +openid);
        LoginUser loginUser = JSONUtil.toBean(s, LoginUser.class);
        String permission;
        if(Objects.isNull(loginUser)||Objects.isNull(loginUser.getUser())){
             LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
             lambdaQueryWrapper.eq(User::getOpenid,openid);
             User user = userMapper.selectOne(lambdaQueryWrapper);
             permission = user.getPermission();
        }else {
            permission = loginUser.getPermission().get(0);
        }
        if(permission.equals("black"))flag = true;
        return  SystemJsonResponse.success(flag);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User>lq=new LambdaQueryWrapper<>();
        lq.eq(User::getUserName,username);
        return userMapper.selectOne(lq);
    }


}
