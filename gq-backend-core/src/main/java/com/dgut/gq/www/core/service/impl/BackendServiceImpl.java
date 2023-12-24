package com.dgut.gq.www.core.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.model.entity.LoginUser;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.common.util.JwtUtil;
import com.dgut.gq.www.core.mapper.*;
import com.dgut.gq.www.core.model.dto.DepartmentDto;
import com.dgut.gq.www.core.model.dto.LectureDto;
import com.dgut.gq.www.core.model.dto.PositionDto;
import com.dgut.gq.www.core.model.dto.PosterTweetDto;
import com.dgut.gq.www.core.model.entity.*;
import com.dgut.gq.www.core.model.vo.*;
import com.dgut.gq.www.core.service.BackendService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

;

/**
 * 后台管理
 * @since  2022-10-8
 * @author  hyj
 * @version  1.0
 */
@Service
public class BackendServiceImpl implements BackendService, UserDetailsService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private LectureMapper lectureMapper;
    @Autowired
    private UserLectureInfoMapper userLectureInfoMapper;

    @Autowired
    private PosterTweetMapper posterTweetMapper;


    @Autowired
    private CurriculumVitaeMapper curriculumVitaeMapper;


    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private  PositionMapper positionMapper;

    @Autowired
    private TicketUserInfoMapper ticketUserInfoMapper;

    /**
     * 后台登录认证
     * @param user
     * @return
     */
    @Override
    public SystemJsonResponse login(User user) {
        //后台管理密码,
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //认证失败
        if(Objects.isNull(authenticate)) {
            throw new GlobalSystemException(
                    GlobalResponseCode.ACCOUNT_NOT_EXIST.getCode(),
                    GlobalResponseCode.ACCOUNT_NOT_EXIST.getMessage());
        }

        //认证成功把全部数据封装为LoginUser存入redis  方便后续权限的管理
        String key = RedisGlobalKey.PERMISSION+user.getUserName();
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(loginUser));

        //设置超时时间
        stringRedisTemplate.expire(key,10,TimeUnit.DAYS);
        String jwt = JwtUtil.createJWT(user.getUserName());
        return  SystemJsonResponse.success(jwt);
    }

    /**
     * 后台登出
     */
    @Override
    public void logout() {
       //获取 SecurityContextHolder信息
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser= (LoginUser) authentication.getPrincipal();
        String userName = loginUser.getUser().getUserName();

        //删除redis对应信息
        String key = RedisGlobalKey.PERMISSION+userName;
       stringRedisTemplate.delete(key);
    }

    /**
     * 获取参加讲座的用户信息
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse getAllUser(int page, int pageSize, String id,Integer status) {
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
        SystemResultList systemResultList = new SystemResultList();
        systemResultList.setList(list);
        systemResultList.setCount(count);
        return SystemJsonResponse.success(systemResultList);
    }



    /**
     * 更新或者新增讲座
     * @param lectureDto
     * @return
     */
    @Override
    public SystemJsonResponse updateOrSave(LectureDto lectureDto) {
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
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),
              state  );
    }


    /**
     * 更新或者新增推文
     * @param posterTweetDto
     * @return
     */
    @Override
    public SystemJsonResponse saveUpdatePosterTweet(PosterTweetDto posterTweetDto) {
        String key = RedisGlobalKey.POSTER_TWEET;
        String id = posterTweetDto.getId();
        PosterTweet posterTweet = new PosterTweet();
        BeanUtils.copyProperties(posterTweetDto,posterTweet);
        PosterTweetVo posterTweetVo = new PosterTweetVo();
        posterTweet.setUpdateTime(LocalDateTime.now());
        String state;
        //新增
        if(id == null || id.equals("")){
            //新增数据
            posterTweet.setCreateTime(LocalDateTime.now());
            posterTweetMapper.insert(posterTweet);
            state = "新增成功";
        }else {
            posterTweetMapper.updateById(posterTweet);
            state = "更新成功";
        }
        stringRedisTemplate.delete(key + posterTweetDto.getType());
        return SystemJsonResponse.success(999,state);
    }

    /**
     * 后台获取讲座
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
     * 导出参加讲座的用户
     * @param id
     * @param status
     * @return
     */
    @Override
    public SystemJsonResponse exportUser(String id, Integer status) {
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
     * 导出简历
     * @param
     * @param term
     * @return
     */
    @Override
    public SystemJsonResponse exportCurriculumVitae(String  departmentId, Integer term) {
        LambdaQueryWrapper<CurriculumVitae>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //未被删除
        lambdaQueryWrapper.eq(CurriculumVitae::getIsDeleted,0);
        //按更新时间降序
        lambdaQueryWrapper.orderByDesc(CurriculumVitae::getUpdateTime);
        //部门
        lambdaQueryWrapper.eq(departmentId != null && !departmentId.equals(""),CurriculumVitae::getDepartmentId,departmentId);
        //第几期
        lambdaQueryWrapper.eq(term != null,CurriculumVitae::getTerm,term);
        List<CurriculumVitaeVo>curriculumVitaeVoList = new ArrayList<>();
        Integer count = curriculumVitaeMapper.selectCount(lambdaQueryWrapper);
        List<CurriculumVitae> curriculumVitaes = curriculumVitaeMapper.selectList(lambdaQueryWrapper);
        for (CurriculumVitae record : curriculumVitaes) {
            String openid = record.getOpenid();
            //查询用户信息
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getOpenid,openid);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            //查询部门信息
            LambdaQueryWrapper<Department>departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            departmentLambdaQueryWrapper.eq(Department::getId,record.getDepartmentId());
            Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
            //查询职位信息
            LambdaQueryWrapper<Position>positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            positionLambdaQueryWrapper.eq(Position::getId,record.getPositionId());
            Position position = positionMapper.selectOne(positionLambdaQueryWrapper);
            CurriculumVitaeVo curriculumVitaeVo = new CurriculumVitaeVo();
            //对象转换
            BeanUtils.copyProperties(record,curriculumVitaeVo);
            //将学生信息添加到返回对象集合
            curriculumVitaeVo.setCollege(user.getCollege());
            curriculumVitaeVo.setName(user.getName());
            curriculumVitaeVo.setStudentId(user.getStudentId());
            curriculumVitaeVo.setNaturalClass(user.getNaturalClass());
            curriculumVitaeVo.setDepartmentName(department.getDepartmentName());
            curriculumVitaeVo.setPositionName(position.getPositionName());
            curriculumVitaeVoList.add(curriculumVitaeVo);
        }
        SystemResultList systemResultList = new SystemResultList(curriculumVitaeVoList,count);
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
     * 删除部门
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse deleteDepartment(String id) {
        Department department = new Department();
        department.setIsDeleted(1);
        department.setId(id);
        departmentMapper.updateById(department);
        return SystemJsonResponse.success();
    }


    /**
     * 删除职位
     * @param id
     * @return
     */
    @Override
    public SystemJsonResponse deletePosition(String id) {
        Position position = new Position();
        position.setIsDeleted(1);
        position.setId(id);
        positionMapper.updateById(position);
        return SystemJsonResponse.success();
    }

    /**
     * 新增或者修改部门
     * @param departmentDto
     * @return
     */
    @Override
    public SystemJsonResponse saveAndUpdateDep(DepartmentDto departmentDto) {
        String id = departmentDto.getId();
        Department department = new Department();
        BeanUtils.copyProperties(departmentDto,department);
        department.setUpdateTime(LocalDateTime.now());
        String status;
        //新增
        if(id == null || id.equals("")){
            department.setCreateTime(LocalDateTime.now());
            departmentMapper.insert(department);
            status = "新增成功";
        }else {
            departmentMapper.updateById(department);
            status = "修改成功";
        }
        return SystemJsonResponse.success(status);
    }


    /**
     * 新增或者修改职位
     * @param positionDto
     * @return
     */
    @Override
    public SystemJsonResponse saveAndUpdatePos(PositionDto positionDto) {
        String id = positionDto.getId();
        Position position = new Position();
        BeanUtils.copyProperties(positionDto,position);
        position.setUpdateTime(LocalDateTime.now());
        String status;
        if(id == null || id.equals("")){
            position.setCreateTime(LocalDateTime.now());
            positionMapper.insert(position);
            status = "新增成功";
        }else {
            positionMapper.updateById(position);
            status = "修改成功";
        }
        return SystemJsonResponse.success(status);
    }


    /**
     * 绑定票号和学号
     * @param studentId
     * @param ticketId
     * @return
     */
    @Override
    public SystemJsonResponse bandTicket(String studentId, String ticketId) {
        LambdaQueryWrapper<TicketUserInfo>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TicketUserInfo::getTicketId,ticketId);
        Integer count = ticketUserInfoMapper.selectCount(lambdaQueryWrapper);
        if(count > 0){
            return SystemJsonResponse.fail(999,"票已经绑定过了");
        }
        TicketUserInfo ticketUserInfo = new TicketUserInfo();
        ticketUserInfo.setTicketId(ticketId);
        ticketUserInfo.setCreateTime(LocalDateTime.now());
        ticketUserInfo.setUpdateTime(LocalDateTime.now());
        ticketUserInfo.setStudentId(studentId);
        ticketUserInfoMapper.insert(ticketUserInfo);
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),"绑定成功");
    }

    /**
     * 导出票号和学号绑定信息
     * @param startId
     * @param endId
     * @return
     */
    @Override
    public SystemJsonResponse exportTicketBand(String startId, String endId) {
        LambdaQueryWrapper<TicketUserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ge(TicketUserInfo::getTicketId,startId);
        lambdaQueryWrapper.le(TicketUserInfo::getTicketId,endId);
        lambdaQueryWrapper.eq(TicketUserInfo::getIsDeleted,0);
        List<TicketUserInfo> ticketUserInfos = ticketUserInfoMapper.selectList(lambdaQueryWrapper);
        List<TicketUserInfoVo> ticketUserInfoVos = new ArrayList<>();
        for (TicketUserInfo ticketUserInfo : ticketUserInfos) {
            TicketUserInfoVo ticketUserInfoVo = new TicketUserInfoVo();
            BeanUtils.copyProperties(ticketUserInfo,ticketUserInfoVo);
            ticketUserInfoVos.add(ticketUserInfoVo);
        }
        return SystemJsonResponse.success(ticketUserInfoVos);
    }


    /**
     * 对用户进行密码和账户的以及权限认证
     * @param
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LambdaQueryWrapper<User>lq=new LambdaQueryWrapper<>();
        lq.eq(User::getUserName,userName);
        User user = userMapper.selectOne(lq);
        //判断用户是否存在数据库中
        Optional<User> optionalUser=Optional.ofNullable(user);
        //不存在就抛出异常
        if(!optionalUser.isPresent()){
            throw  new GlobalSystemException(
                    GlobalResponseCode.OPERATE_FAIL.getCode(),
                    "账户不存在");
        }
        //权限信息
        String permission = user.getPermission();
        List<String> list = new ArrayList<>();
        list.add(permission);

        return new LoginUser(user,list);
    }
}

