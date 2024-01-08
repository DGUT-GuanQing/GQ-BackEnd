package com.dgut.gq.www.recruit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.recruit.mapper.CurriculumVitaeMapper;
import com.dgut.gq.www.recruit.mapper.DepartmentMapper;
import com.dgut.gq.www.recruit.mapper.PositionMapper;
import com.dgut.gq.www.recruit.mapper.UserMapper;
import com.dgut.gq.www.recruit.common.model.dto.CurriculumVitaeDto;
import com.dgut.gq.www.recruit.common.model.dto.DepartmentDto;
import com.dgut.gq.www.recruit.common.model.dto.PositionDto;
import com.dgut.gq.www.recruit.common.model.entity.CurriculumVitae;
import com.dgut.gq.www.recruit.common.model.entity.Department;
import com.dgut.gq.www.recruit.common.model.entity.Position;
import com.dgut.gq.www.recruit.common.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.recruit.common.model.vo.DepartmentVo;
import com.dgut.gq.www.recruit.common.model.vo.PositionVo;
import com.dgut.gq.www.recruit.service.RecruitmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 简历模块
 * @author hyj
 * @version 1.0
 * @since  2023-5-10
 */
@Service
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

    @Autowired
    private CurriculumVitaeMapper curriculumVitaeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private  StringRedisTemplate stringRedisTemplate;

    /**
     * 上传或者修改简历
     * @param openid
     * @param curriculumVitaeDto
     * @return
     */
    @Override
    public SystemJsonResponse updateOrSave(String openid, CurriculumVitaeDto curriculumVitaeDto) {
        //先查询用户是否上传过简历
        LambdaQueryWrapper<CurriculumVitae>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CurriculumVitae::getOpenid,openid);
        Integer count = curriculumVitaeMapper.selectCount(lambdaQueryWrapper);
        CurriculumVitae curriculumVitae = new CurriculumVitae();
        BeanUtils.copyProperties(curriculumVitaeDto,curriculumVitae);
        curriculumVitae.setUpdateTime(LocalDateTime.now());
        curriculumVitae.setOpenid(openid);
        String msg;
        if(count == null || count == 0){
            curriculumVitae.setCreateTime(LocalDateTime.now());
            //获取年份，计算第几期
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            curriculumVitae.setTerm(year - 2010);
            curriculumVitaeMapper.insert(curriculumVitae);
            msg = "上传成功";
        }else {
            curriculumVitaeMapper.update(curriculumVitae,lambdaQueryWrapper);
            msg = "修改成功";
        }
        //把班级存入数据库
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = new User();
        user.setNaturalClass(curriculumVitaeDto.getNaturalClass());
        userLambdaQueryWrapper.eq(User::getOpenid,openid);
        userMapper.update(user,userLambdaQueryWrapper);
        stringRedisTemplate.delete(RedisGlobalKey.USER_MESSAGE + openid);
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(),msg);
    }

    /**
     * 获取我的简历
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMyCurriculumVitae(String openid) {
        LambdaQueryWrapper<CurriculumVitae> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CurriculumVitae::getOpenid,openid);
        CurriculumVitae curriculumVitae = curriculumVitaeMapper.selectOne(lambdaQueryWrapper);
        CurriculumVitaeVo curriculumVitaeVo = new CurriculumVitaeVo();
        if(curriculumVitae == null || curriculumVitae.getOpenid() == null){
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"没有简历");
        }else {
            //查询用户信息
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getOpenid,openid);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            //查询部门信息
            LambdaQueryWrapper<Department>departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            departmentLambdaQueryWrapper.eq(Department::getId,curriculumVitae.getDepartmentId());
            Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
            //查询职位信息
            LambdaQueryWrapper<Position>positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            positionLambdaQueryWrapper.eq(Position::getId,curriculumVitae.getPositionId());
            Position position = positionMapper.selectOne(positionLambdaQueryWrapper);
            //对象转换
            BeanUtils.copyProperties(curriculumVitae,curriculumVitaeVo);
            //将学生信息添加到返回对象集合
            curriculumVitaeVo.setCollege(user.getCollege());
            curriculumVitaeVo.setName(user.getName());
            curriculumVitaeVo.setStudentId(user.getStudentId());
            curriculumVitaeVo.setNaturalClass(user.getNaturalClass());
            curriculumVitaeVo.setDepartmentName(department.getDepartmentName());
            curriculumVitaeVo.setPositionName(position.getPositionName());
        }
        return SystemJsonResponse.success(curriculumVitaeVo);
    }

    /**
     * 获取简历
     * @return
     */
    @Override
    public SystemJsonResponse getAllCurriculumVitae(int page, int pageSize,String departmentId,Integer term) {
        Page<CurriculumVitae>pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<CurriculumVitae>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //未被删除
        lambdaQueryWrapper.eq(CurriculumVitae::getIsDeleted,0);
        //按更新时间降序
        lambdaQueryWrapper.orderByDesc(CurriculumVitae::getUpdateTime);
        //部门
        lambdaQueryWrapper.eq(departmentId != null && !departmentId.equals(""),CurriculumVitae::getDepartmentId,departmentId);
        //第几期
        lambdaQueryWrapper.eq(term != null,CurriculumVitae::getTerm,term);
        curriculumVitaeMapper.selectPage(pageInfo,lambdaQueryWrapper);
        List<CurriculumVitae> records = pageInfo.getRecords();
        List<CurriculumVitaeVo>curriculumVitaeVoList = new ArrayList<>();
        Integer count = curriculumVitaeMapper.selectCount(lambdaQueryWrapper);
        for (CurriculumVitae record : records) {
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
     * 获取全部部门
     * @return
     */
    @Override
    public SystemJsonResponse getDepartment() {
        //条件构造器
        LambdaQueryWrapper<Department> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //没被删除
        lambdaQueryWrapper.eq(Department::getIsDeleted,0);
        List<Department> departments = departmentMapper.selectList(lambdaQueryWrapper);
        List<DepartmentVo> departmentVoList = new ArrayList<>();
        for (Department department : departments) {
            DepartmentVo departmentVo = new DepartmentVo();
            BeanUtils.copyProperties(department,departmentVo);
            departmentVoList.add(departmentVo);
        }
        return SystemJsonResponse.success(departmentVoList);
    }

    /**
     * 获取职位
     * @param departmentId
     * @return
     */
    @Override
    public SystemJsonResponse getPosition(String departmentId) {
        //条件构造器
        LambdaQueryWrapper<Position> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //没被删除
        lambdaQueryWrapper.eq(Position::getIsDeleted,0);
        lambdaQueryWrapper.eq(Position::getDepartmentId,departmentId);
        List<Position> positions = positionMapper.selectList(lambdaQueryWrapper);
        List<PositionVo> positionVos = new ArrayList<>();
        for (Position position : positions) {
            PositionVo positionVo = new PositionVo();
            BeanUtils.copyProperties(position,positionVo);
            positionVos.add(positionVo);
        }
        return SystemJsonResponse.success(positionVos);
    }

    /**
     * 导出简历
     * @param departmentId
     * @param term
     * @return
     */
    @Override
    public SystemJsonResponse exportCurriculumVitae(String departmentId, Integer term) {
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
     * 新增或者修稿部门
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

}
