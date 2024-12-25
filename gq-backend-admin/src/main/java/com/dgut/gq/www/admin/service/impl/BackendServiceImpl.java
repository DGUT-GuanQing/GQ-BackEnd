package com.dgut.gq.www.admin.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.admin.common.model.dto.DepartmentDto;
import com.dgut.gq.www.admin.common.model.dto.LectureDto;
import com.dgut.gq.www.admin.common.model.dto.PositionDto;
import com.dgut.gq.www.admin.common.model.dto.PosterTweetDto;
import com.dgut.gq.www.admin.common.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.admin.common.model.vo.LectureVo;
import com.dgut.gq.www.admin.common.model.vo.UserVo;
import com.dgut.gq.www.admin.service.BackendService;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.db.entity.*;
import com.dgut.gq.www.common.db.service.*;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 后台管理
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-8
 */
@Service
@Slf4j
public class BackendServiceImpl implements BackendService, UserDetailsService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GqUserLectureInfoService gqUserLectureInfoService;

    @Autowired
    private GqUserService gqUserService;

    @Autowired
    private GqLectureService gqLectureService;

    @Autowired
    private GqPosterTweetService gqPosterTweetService;

    @Autowired
    private GqDepartmentService gqDepartmentService;

    @Autowired
    private GqPositionService gqPositionService;

    @Autowired
    private GqCurriculumVitaeService gqCurriculumVitaeService;

    @Override
    public SystemJsonResponse login(String userName, String password) {
        log.info("BackendServiceImpl login userName = {}, password = {}", userName, password);
        //后台管理密码
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        if (Objects.isNull(authenticate)) {
            throw new GlobalSystemException(
                    GlobalResponseCode.ACCOUNT_NOT_EXIST.getCode(),
                    GlobalResponseCode.ACCOUNT_NOT_EXIST.getMessage());
        }

        //认证成功把全部数据封装为LoginUser存入redis  方便后续权限的管理
        String key = RedisGlobalKey.PERMISSION + userName;
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(loginUser));
        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);

        String jwt = JwtUtil.createJWT(userName);
        return SystemJsonResponse.success(jwt);
    }

    @Override
    public void logout() {
        //获取 SecurityContextHolder信息
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userName = loginUser.getUser().getUserName();

        //删除redis对应信息
        String key = RedisGlobalKey.PERMISSION + userName;
        stringRedisTemplate.delete(key);
    }

    @Override
    public SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status) {
        Page<UserLectureInfo> pageInfo  = gqUserLectureInfoService.getByLectureId(page, pageSize, id, status);
        List<UserLectureInfo> records = pageInfo.getRecords();
        log.info("BackendServiceImpl getAttendLectureUser lectureId = {}, userLectureInfos = {}", id, JSONUtil.toJsonStr(records));
        List<String> userOpenidList = records.stream()
                .map(UserLectureInfo::getOpenid)
                .collect(Collectors.toList());
        List<User> users = gqUserService.getByOpenIds(userOpenidList);
        List<UserVo> userVoList = users.stream()
                .map(user -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    return userVo;
                })
                .collect(Collectors.toList());
        log.info("BackendServiceImpl getAttendLectureUser lectureId = {}, userVoList = {}", id, JSONUtil.toJsonStr(userVoList));
        SystemResultList systemResultList = new SystemResultList(userVoList, (int) pageInfo.getTotal());

        return SystemJsonResponse.success(systemResultList);
    }

    @Override
    public SystemJsonResponse updateOrSaveLecture(LectureDto lectureDto) {
        log.info("BackendServiceImpl updateOrSaveLecture lectureDto= {}", JSONUtil.toJsonStr(lectureDto));
        String key = RedisGlobalKey.UNSTART_LECTURE;
        Lecture lecture = new Lecture();
        BeanUtils.copyProperties(lectureDto, lecture);
        lecture.setUpdateTime(LocalDateTime.now());
        String id = lectureDto.getId();

        String state;
        //新增
        if (id == null || id.isEmpty()) {
            //插入数据库
            lecture.setCreateTime(LocalDateTime.now());
            gqLectureService.save(lecture);
            //删除原来抢票的人
            stringRedisTemplate.delete(RedisGlobalKey.IS_GRAB_TICKETS);
            //删除讲座
            stringRedisTemplate.delete(RedisGlobalKey.UNSTART_LECTURE);
            state = "新增成功";
        } else {
            gqLectureService.updateById(lecture);
            //看redis的讲座是否要更新
            Optional.ofNullable(stringRedisTemplate.opsForValue().get(key))
                    .map(lec -> JSONUtil.toBean(lec, Lecture.class))
                    .ifPresent(lec -> {
                        if (lec.getId().equals(id)) {
                            stringRedisTemplate.delete(key);
                            //更新票的数量
                            stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER, lectureDto.getTicketNumber().toString());
                        }
                    });
            state = "更新成功";
        }
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), state);
    }

    @Override
    public SystemJsonResponse saveUpdatePosterTweet(PosterTweetDto posterTweetDto) {
        log.info("BackendServiceImpl saveUpdatePosterTweet posterTweetDto= {}", JSONUtil.toJsonStr(posterTweetDto));
        String id = posterTweetDto.getId();
        PosterTweet posterTweet = new PosterTweet();
        BeanUtils.copyProperties(posterTweetDto, posterTweet);
        posterTweet.setUpdateTime(LocalDateTime.now());
        String state;
        //新增
        if (id == null || id.isEmpty()) {
            //新增数据
            posterTweet.setCreateTime(LocalDateTime.now());
            gqPosterTweetService.save(posterTweet);
            state = "新增成功";
        } else {
            gqPosterTweetService.updateById(posterTweet);
            state = "更新成功";
        }
        stringRedisTemplate.delete(RedisGlobalKey.POSTER_TWEET + posterTweetDto.getType());
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), state);
    }

    @Override
    public SystemJsonResponse getLecture(int page, int pageSize, String name) {
        Page<Lecture> pageInfo = gqLectureService.getBackendLectures(page, pageSize, name);
        List<Lecture> records = pageInfo.getRecords();
        log.info("BackendServiceImpl getLecture lectures = {}", JSONUtil.toJsonStr(records));
        List<Object> lectureVos = records.stream()
                .map(lecture -> {
                    LectureVo lectureVo = new LectureVo();
                    BeanUtils.copyProperties(lecture, lectureVo);
                    return lectureVo;
                })
                .collect(Collectors.toList());
        SystemResultList systemResultList = new SystemResultList(lectureVos, (int) pageInfo.getTotal());

        return SystemJsonResponse.success(systemResultList);
    }

    @Override
    public SystemJsonResponse exportUser(String id, Integer status) {
        List<UserLectureInfo> records = gqUserLectureInfoService.getByLectureId(id, status);
        log.info("BackendServiceImpl exportUser lectureId = {}, UserLectureInfos = {}", id, JSONUtil.toJsonStr(records));
        List<String> userOpenidList = records.stream()
                .map(UserLectureInfo::getOpenid)
                .collect(Collectors.toList());
        List<UserVo> userVoList = gqUserService.getByOpenIds(userOpenidList)
                .stream()
                .map(user -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    return userVo;
                })
                .collect(Collectors.toList());
        log.info("BackendServiceImpl exportUser lectureId = {}, userVoList = {}", id, JSONUtil.toJsonStr(userVoList));
        Integer count = userVoList.size();
        SystemResultList systemResultList = new SystemResultList(userVoList, count);

        return SystemJsonResponse.success(systemResultList);
    }

    @Override
    public SystemJsonResponse exportCurriculumVitae(String departmentId, Integer term) {
        List<CurriculumVitae> curriculumVitaes = gqCurriculumVitaeService.getByDepartmentIdAndTerm(departmentId, term);
        log.info("BackendServiceImpl exportCurriculumVitae departmentId = {}, curriculumVitaes = {}", departmentId, JSONUtil.toJsonStr(curriculumVitaes));
        Map<String, User> userMap = gqUserService.getByOpenIds(
                curriculumVitaes.stream()
                        .filter(Objects::nonNull)
                        .map(CurriculumVitae::getOpenid)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(
                        User::getOpenid,
                        Function.identity(),
                        (o1, o2) -> o1)
        );
        log.info("BackendServiceImpl exportCurriculumVitae departmentId = {}, term = {}, userMap = {}", departmentId, term, JSONUtil.toJsonStr(userMap));
        Map<String, Department> departmentMap = gqDepartmentService.getByIds(
                curriculumVitaes.stream()
                        .map(CurriculumVitae::getDepartmentId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Department::getId, Function.identity()));
        log.info("BackendServiceImpl exportCurriculumVitae departmentId = {}, term = {}, departmentMap = {}", departmentId, term, JSONUtil.toJsonStr(departmentMap));
        Map<String, Position> positionMap = gqPositionService.getByIds(
                curriculumVitaes.stream()
                        .map(CurriculumVitae::getPositionId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Position::getId, Function.identity()));
        log.info("BackendServiceImpl exportCurriculumVitae departmentId = {}, term = {}, positionMap = {}", departmentId, term, JSONUtil.toJsonStr(positionMap));
        // 组装返还值
        List<CurriculumVitaeVo> curriculumVitaeVoList = curriculumVitaes.stream()
                .filter(record -> userMap.containsKey(record.getOpenid())
                        && departmentMap.containsKey(record.getDepartmentId())
                        && positionMap.containsKey(record.getPositionId())
                ).map(record -> {
                    User user = userMap.get(record.getOpenid());
                    Department department = departmentMap.get(record.getDepartmentId());
                    Position position = positionMap.get(record.getPositionId());
                    return buildCurriculumVitaeVo(record, user, department, position);
                }).collect(Collectors.toList());
        log.info("BackendServiceImpl exportCurriculumVitae departmentId = {}, term = {}, curriculumVitaeVoList = {}", departmentId, term, JSONUtil.toJsonStr(curriculumVitaeVoList));

        return SystemJsonResponse.success(new SystemResultList(curriculumVitaeVoList, curriculumVitaes.size()));
    }

    private CurriculumVitaeVo buildCurriculumVitaeVo(CurriculumVitae curriculumVitae, User user, Department department, Position position) {
        CurriculumVitaeVo curriculumVitaeVo = new CurriculumVitaeVo();
        BeanUtils.copyProperties(curriculumVitae, curriculumVitaeVo);
        curriculumVitaeVo.setCollege(user.getCollege());
        curriculumVitaeVo.setName(user.getName());
        curriculumVitaeVo.setStudentId(user.getStudentId());
        curriculumVitaeVo.setNaturalClass(user.getNaturalClass());
        curriculumVitaeVo.setDepartmentName(department.getDepartmentName());
        curriculumVitaeVo.setPositionName(position.getPositionName());
        return curriculumVitaeVo;
    }


    @Override
    public SystemJsonResponse deleteLecture(String id) {
        log.info("BackendServiceImpl deleteLecture lectureId = {}", id);
        // 更新讲座记录
        Lecture lecture = new Lecture();
        lecture.setIsDeleted(1);
        lecture.setId(id);
        gqLectureService.updateById(lecture);
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

    @Override
    public SystemJsonResponse deleteDepartment(String id) {
        log.info("BackendServiceImpl deleteDepartment departmentId = {}", id);
        Department department = new Department();
        department.setIsDeleted(1);
        department.setId(id);
        gqDepartmentService.updateById(department);
        return SystemJsonResponse.success();
    }

    @Override
    public SystemJsonResponse deletePosition(String id) {
        log.info("BackendServiceImpl deletePosition positionId = {}", id);
        Position position = new Position();
        position.setIsDeleted(1);
        position.setId(id);
        gqPositionService.updateById(position);
        return SystemJsonResponse.success();
    }

    @Override
    public SystemJsonResponse saveAndUpdateDep(DepartmentDto departmentDto) {
        log.info("BackendServiceImpl saveAndUpdateDep departmentDto = {}", JSONUtil.toJsonStr(departmentDto));
        String id = departmentDto.getId();
        Department department = new Department();
        BeanUtils.copyProperties(departmentDto, department);
        department.setUpdateTime(LocalDateTime.now());
        String status;
        //新增
        if (id == null || id.isEmpty()) {
            department.setCreateTime(LocalDateTime.now());
            gqDepartmentService.save(department);
            status = "新增成功";
        } else {
            gqDepartmentService.updateById(department);
            status = "修改成功";
        }
        return SystemJsonResponse.success(status);
    }

    @Override
    public SystemJsonResponse saveAndUpdatePos(PositionDto positionDto) {
        log.info("BackendServiceImpl saveAndUpdatePos positionDto = {}", JSONUtil.toJsonStr(positionDto));
        String id = positionDto.getId();
        Position position = new Position();
        BeanUtils.copyProperties(positionDto, position);
        position.setUpdateTime(LocalDateTime.now());
        String status;
        if (id == null || id.isEmpty()) {
            position.setCreateTime(LocalDateTime.now());
            gqPositionService.save(position);
            status = "新增成功";
        } else {
            gqPositionService.updateById(position);
            status = "修改成功";
        }
        return SystemJsonResponse.success(status);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = gqUserService.getByUserName(userName);
        //判断用户是否存在数据库中
        Optional<User> optionalUser = Optional.ofNullable(user);
        //不存在就抛出异常
        if (!optionalUser.isPresent()) {
            throw new GlobalSystemException(
                    GlobalResponseCode.OPERATE_FAIL.getCode(),
                    "账户不存在");
        }
        //权限信息
        String permission = user.getPermission();
        List<String> list = new ArrayList<>();
        list.add(permission);

        return new LoginUser(user, list);
    }
}

