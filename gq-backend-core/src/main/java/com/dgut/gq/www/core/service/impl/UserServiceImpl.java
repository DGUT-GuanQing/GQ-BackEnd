package com.dgut.gq.www.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.entity.LoginUser;
import com.dgut.gq.www.common.db.entity.User;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;
import com.dgut.gq.www.common.db.mapper.LectureMapper;
import com.dgut.gq.www.common.db.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.common.db.mapper.UserMapper;
import com.dgut.gq.www.common.db.service.GqLectureService;
import com.dgut.gq.www.common.db.service.GqUserLectureInfoService;
import com.dgut.gq.www.common.db.service.GqUserService;
import com.dgut.gq.www.common.util.JwtUtil;
import com.dgut.gq.www.core.common.model.dto.UserDto;
import com.dgut.gq.www.core.common.model.vo.MyLectureVo;
import com.dgut.gq.www.core.common.model.vo.UserVo;
import com.dgut.gq.www.core.common.util.HttpUtil;
import com.dgut.gq.www.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户模块
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-16
 */
@Service
@Slf4j
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

    @Autowired
    private GqUserService gqUserService;

    @Autowired
    private GqUserLectureInfoService gqUserLectureInfoService;

    @Autowired
    private GqLectureService gqLectureService;

    /**
     * cas client 默认的session key
     */
    public static final String CAS = "_const_cas_assertion_";

    /**
     * 微信登录
     *
     * @param code
     * @return
     */
    @Override
    public String wxLogin(String code) {
        //获取用户的openid
        String openid = httpUtil.getOpenid(code);
        //String openid = code;
        User user = gqUserService.getByOpenid(openid);
        Optional<User> optionalUser = Optional.ofNullable(user);
        //第一次登录
        if (!optionalUser.isPresent()) {
            user = createUser(openid);
            gqUserService.save(user);
        } else {
            updateUser(user);
        }
        log.info("UserServiceImpl wxLogin user = {}", JSONUtil.toJsonStr(user));
        // 把全部数据封装为LoginUser存入redis  方便后续权限的管理
        LoginUser loginUser = createLoginUser(user);
        //封装权限
        String key = RedisGlobalKey.PERMISSION + openid;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(loginUser));
        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);

        //加密openid，返回token
        return JwtUtil.createJWT(openid);
    }

    private LoginUser createLoginUser(User user) {
        List<String> permissions = new ArrayList<>();
        permissions.add(user.getPermission());
        String openid = user.getOpenid();
        user = new User();
        user.setOpenid(openid);
        return new LoginUser(user, permissions);
    }

    private void updateUser(User user) {
        user.setCreateTime(null);
        user.setUpdateTime(LocalDateTime.now());
        gqUserService.updateById(user);
    }

    private User createUser(String openid) {
        User user = new User();
        user.setOpenid(openid);
        user.setUpdateTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        user.setPermission("user");
        return user;
    }

    /**
     * 获取小程序端个人信息
     *
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMe(String openid) {
        String key = RedisGlobalKey.USER_MESSAGE + openid;
        UserVo userVo = Optional.ofNullable(stringRedisTemplate.opsForValue().get(key))
                .map(user -> JSONUtil.toBean(user, UserVo.class))
                .orElseGet(() -> {
                    User user = gqUserService.getByOpenid(openid);
                    UserVo newUserVo = new UserVo();
                    BeanUtils.copyProperties(user, newUserVo);
                    //存入redis
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newUserVo));
                    stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
                    return newUserVo;
                });
        log.info("UserServiceImpl getMe = {}", JSONUtil.toJsonStr(userVo));
        return SystemJsonResponse.success(userVo);
    }

    /**
     * 获取自己参加过的讲座信息
     *
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMyLecture(String openid, Integer page, Integer pageSize) {
        Page<UserLectureInfo> pageInfo = gqUserLectureInfoService.getByOpenid(openid, page, pageSize);
        List<UserLectureInfo> records = pageInfo.getRecords();
        log.info("UserServiceImpl getMyLecture openid = {}, UserLectureInfoRecords = {}", openid, JSONUtil.toJsonStr(records));
        // 记录当前讲座的观看情况
        HashMap<String, Integer> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (UserLectureInfo record : records) {
            list.add(record.getLectureId());
            map.put(record.getLectureId(), record.getStatus());
        }
        // 查询讲座信息
        List<Lecture> lectures = gqLectureService.getByIds(list);
        log.info("UserServiceImpl getMyLecture openid = {}, lectures = {}", openid, JSONUtil.toJsonStr(lectures));
        List<MyLectureVo> lectureVos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lectures)) {
            lectureVos = lectures.stream().map(lecture -> {
                MyLectureVo lectureVo = new MyLectureVo();
                BeanUtil.copyProperties(lecture, lectureVo);
                lectureVo.setStatus(map.get(lecture.getId()));
                return lectureVo;
            }).collect(Collectors.toList());
        }
        SystemResultList systemResultList = new SystemResultList(lectureVos, (int) pageInfo.getTotal());
        return SystemJsonResponse.success(systemResultList);
    }

    /**
     * 是否抢到票
     *
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse isGrabTicket(String openid) {
        String key = RedisGlobalKey.IS_GRAB_TICKETS;
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, openid);
        boolean flag = member != null && member;
        return SystemJsonResponse.success(flag);
    }

    /**
     * 中央认证登陆
     *
     * @param userDto
     * @return
     */
    @Override
    public SystemJsonResponse dgutLogin(UserDto userDto, String openid) {
        //将中央认证信息更新到数据库
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenid, openid);
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        userMapper.update(user, lambdaQueryWrapper);
        //删除redis中的信息
        String key = RedisGlobalKey.USER_MESSAGE + openid;
        stringRedisTemplate.delete(key);
        //设置中央认证缓存
        stringRedisTemplate.opsForValue().set(RedisGlobalKey.DGUT_LOGIN + openid, openid);
        return SystemJsonResponse.success();
    }

    /**
     * 签到
     *
     * @param lectureId
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse signIn(String lectureId, String openid) {
        LambdaQueryWrapper<UserLectureInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserLectureInfo::getLectureId, lectureId);
        lambdaQueryWrapper.eq(UserLectureInfo::getOpenid, openid);

        //先查询用户有没有抢到讲座
        UserLectureInfo userLectureInfo = gqUserLectureInfoService.getByLectureAndOpenid(lectureId, openid);
        if (userLectureInfo == null) {
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "没有相关讲座记录");
        }
        userLectureInfo.setStatus(userLectureInfo.getStatus() + 1);
        //更新个人观看讲座记录
        gqUserService.updateRaceNumber(openid);
        // 更新签到记录
        gqUserLectureInfoService.updateById(userLectureInfo);
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), "签到成功");
    }

    /**
     * 是否在黑名单
     *
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse isBlack(String openid) {
        boolean flag = false;
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.PERMISSION + openid);
        LoginUser loginUser = JSONUtil.toBean(s, LoginUser.class);
        String permission;

        if (Objects.isNull(loginUser) || Objects.isNull(loginUser.getUser())) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getOpenid, openid);
            User user = gqUserService.getByOpenid(openid);
            permission = user.getPermission();
        } else {
            permission = loginUser.getPermission().get(0);
        }
        if (permission.equals("black")) {
            flag = true;
        }
        return SystemJsonResponse.success(flag);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> lq = new LambdaQueryWrapper<>();
        lq.eq(User::getUserName, username);
        return userMapper.selectOne(lq);
    }

    @Override
    public SystemJsonResponse getLoginUserInfo(HttpServletRequest request, String id) {
        Optional<User> optionalUser = Optional.ofNullable(gqUserService.getById(id));
        if (!optionalUser.isPresent() || optionalUser.get().getOpenid() == null) {
            return SystemJsonResponse.fail();
        }
        String openid = optionalUser.get().getOpenid();
        Object cas = request.getSession().getAttribute(CAS);
        Optional<Object> optionalObject = Optional.ofNullable(cas);

        if (!optionalObject.isPresent()) {
            return SystemJsonResponse.fail("中央认证失败");
        }
        Assertion assertion = (Assertion) cas;
        String userName = assertion.getPrincipal().getName();
        log.info("cas对接登录用户buildUserInfoByCas = {}", userName);
        //获取属性值
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        //更新用户信息
        User user = new User();
        user.setOpenid(openid);
        user.setName((String) attributes.get("cn"));
        user.setCollege((String) attributes.get("eduPersonOrgDN"));
        user.setStudentId((String) attributes.get("bindUserList"));
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getOpenid, openid);
        if (!gqUserService.update(user, lambdaUpdateWrapper)) {
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "该中央认证已经绑定过微信号,请联系管理员解绑");
        }
        //删除redis
        stringRedisTemplate.delete(RedisGlobalKey.USER_MESSAGE + openid);
        //设置中央认证缓存
        stringRedisTemplate.opsForValue().set(RedisGlobalKey.DGUT_LOGIN + openid, openid);
        return SystemJsonResponse.success();
    }

}
