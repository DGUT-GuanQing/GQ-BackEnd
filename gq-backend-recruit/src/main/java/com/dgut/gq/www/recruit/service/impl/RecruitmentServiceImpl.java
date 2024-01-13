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
import java.util.Optional;
import java.util.stream.Collectors;

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
        // 否上传过简历
        boolean isResumeExists = checkResumeExists(openid);
        CurriculumVitae curriculumVitae = createCurriculumVitae(curriculumVitaeDto, openid);
        String msg;
        if (!isResumeExists) {
            initializeNewCurriculumVitae(curriculumVitae);
            curriculumVitaeMapper.insert(curriculumVitae);
            msg = "上传成功";
        } else {
            updateExistingCurriculumVitae(curriculumVitae, openid);
            msg = "修改成功";
        }

        // 更新用户班级信息，中央认证拿不到班级，这里是通过用户上传间接拿到他填的班级
        updateUserClassInfo(openid, curriculumVitaeDto.getNaturalClass());

        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), msg);
    }

    private void updateUserClassInfo(String openid, String naturalClass) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        User user = new User();
        user.setNaturalClass(naturalClass);
        userMapper.update(user, queryWrapper);
        stringRedisTemplate.delete(RedisGlobalKey.USER_MESSAGE + openid);
    }

    private void updateExistingCurriculumVitae(CurriculumVitae curriculumVitae, String openid) {
        LambdaQueryWrapper<CurriculumVitae> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumVitae::getOpenid, openid);
        curriculumVitaeMapper.update(curriculumVitae, queryWrapper);
    }

    private void initializeNewCurriculumVitae(CurriculumVitae curriculumVitae) {
        curriculumVitae.setCreateTime(LocalDateTime.now());
        int year = LocalDate.now().getYear();
        curriculumVitae.setTerm(year - 2010);
    }

    private CurriculumVitae createCurriculumVitae(CurriculumVitaeDto curriculumVitaeDto, String openid) {
        CurriculumVitae curriculumVitae = new CurriculumVitae();
        BeanUtils.copyProperties(curriculumVitaeDto, curriculumVitae);
        curriculumVitae.setUpdateTime(LocalDateTime.now());
        curriculumVitae.setOpenid(openid);
        return curriculumVitae;
    }

    private boolean checkResumeExists(String openid) {
        LambdaQueryWrapper<CurriculumVitae> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumVitae::getOpenid, openid);
        Integer count = curriculumVitaeMapper.selectCount(queryWrapper);
        return count != null && count > 0;
    }

    /**
     * 获取我的简历
     * @param openid
     * @return
     */
    @Override
    public SystemJsonResponse getMyCurriculumVitae(String openid) {
        CurriculumVitae curriculumVitae = queryCurriculumVitae(openid);
        if (!Optional.ofNullable(curriculumVitae).isPresent()) {
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "没有简历");
        }

        User user = queryUser(openid);
        Department department = queryDepartment(curriculumVitae.getDepartmentId());
        Position position = queryPosition(curriculumVitae.getPositionId());
        CurriculumVitaeVo curriculumVitaeVo = buildCurriculumVitaeVo(curriculumVitae, user, department, position);

        return SystemJsonResponse.success(curriculumVitaeVo);
    }

    private CurriculumVitae queryCurriculumVitae(String openid) {
        LambdaQueryWrapper<CurriculumVitae> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CurriculumVitae::getOpenid, openid);
        return curriculumVitaeMapper.selectOne(lambdaQueryWrapper);
    }

    private User queryUser(String openid) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getOpenid, openid);
        return userMapper.selectOne(userLambdaQueryWrapper);
    }

    private Department queryDepartment(String departmentId) {
        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        departmentLambdaQueryWrapper.eq(Department::getId, departmentId);
        return departmentMapper.selectOne(departmentLambdaQueryWrapper);
    }

    private Position queryPosition(String positionId) {
        LambdaQueryWrapper<Position> positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        positionLambdaQueryWrapper.eq(Position::getId, positionId);
        return positionMapper.selectOne(positionLambdaQueryWrapper);
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

    /**
     * 获取简历
     * @return
     */
    @Override
    public SystemJsonResponse getAllCurriculumVitae(int page, int pageSize,String departmentId,Integer term) {
        Page<CurriculumVitae>pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<CurriculumVitae>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CurriculumVitae::getIsDeleted,0)
                          .orderByDesc(CurriculumVitae::getUpdateTime)
                          .eq(departmentId != null && !departmentId.equals(""),CurriculumVitae::getDepartmentId,departmentId)
                          .eq(term != null,CurriculumVitae::getTerm,term);
        curriculumVitaeMapper.selectPage(pageInfo,lambdaQueryWrapper);
        List<CurriculumVitae> records = pageInfo.getRecords();
        Integer count = curriculumVitaeMapper.selectCount(lambdaQueryWrapper);

        List<CurriculumVitaeVo> curriculumVitaeVoList = records.stream().map(record -> {
            User user = queryUser(record.getOpenid());
            Department department = queryDepartment(record.getDepartmentId());
            Position position = queryPosition(record.getPositionId());
            return buildCurriculumVitaeVo(record, user, department, position);
        }).collect(Collectors.toList());

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
        lambdaQueryWrapper.eq(CurriculumVitae::getIsDeleted,0)
                .orderByDesc(CurriculumVitae::getUpdateTime)
                .eq(departmentId != null && !departmentId.equals(""),CurriculumVitae::getDepartmentId,departmentId)
                .eq(term != null,CurriculumVitae::getTerm,term);
        Integer count = curriculumVitaeMapper.selectCount(lambdaQueryWrapper);
        List<CurriculumVitae> curriculumVitaes = curriculumVitaeMapper.selectList(lambdaQueryWrapper);

        List<CurriculumVitaeVo> curriculumVitaeVoList = curriculumVitaes.stream().map(record -> {
            User user = queryUser(record.getOpenid());
            Department department = queryDepartment(record.getDepartmentId());
            Position position = queryPosition(record.getPositionId());
            return buildCurriculumVitaeVo(record, user, department, position);
        }).collect(Collectors.toList());
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
