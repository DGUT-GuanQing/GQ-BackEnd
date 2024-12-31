package com.dgut.gq.www.recruit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.db.entity.CurriculumVitae;
import com.dgut.gq.www.common.db.entity.Department;
import com.dgut.gq.www.common.db.entity.Position;
import com.dgut.gq.www.common.db.entity.User;
import com.dgut.gq.www.common.db.service.GqCurriculumVitaeService;
import com.dgut.gq.www.common.db.service.GqDepartmentService;
import com.dgut.gq.www.common.db.service.GqPositionService;
import com.dgut.gq.www.common.db.service.GqUserService;
import com.dgut.gq.www.recruit.common.model.dto.CurriculumVitaeDto;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简历模块
 *
 * @author hyj
 * @version 1.0
 * @since 2023-5-10
 */
@Service
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GqCurriculumVitaeService gqCurriculumVitaeService;

    @Autowired
    private GqUserService gqUserService;

    @Autowired
    private GqPositionService gqPositionService;

    @Autowired
    private GqDepartmentService gqDepartmentService;

    /**
     * 上传或者修改简历
     *
     * @param openid
     * @param curriculumVitaeDto
     * @return
     */
    @Override
    public SystemJsonResponse updateOrSave(String openid, CurriculumVitaeDto curriculumVitaeDto) {
        log.info("RecruitmentServiceImpl updateOrSave openid = {}, curriculumVitaeDto = {}", openid, curriculumVitaeDto);
        // 否上传过简历
        boolean isResumeExists = checkResumeExists(openid);
        CurriculumVitae curriculumVitae = createCurriculumVitae(curriculumVitaeDto, openid);
        String msg;
        if (!isResumeExists) {
            initializeNewCurriculumVitae(curriculumVitae);
            gqCurriculumVitaeService.save(curriculumVitae);
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
        gqUserService.update(user, queryWrapper);
        stringRedisTemplate.delete(RedisGlobalKey.USER_MESSAGE + openid);
    }

    private void updateExistingCurriculumVitae(CurriculumVitae curriculumVitae, String openid) {
        LambdaQueryWrapper<CurriculumVitae> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CurriculumVitae::getOpenid, openid);
        gqCurriculumVitaeService.update(curriculumVitae, queryWrapper);
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
        Integer count = gqCurriculumVitaeService.countByOpenId(openid);
        return count != null && count > 0;
    }

    /**
     * 获取我的简历
     *
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
        log.info("RecruitmentServiceImpl getMyCurriculumVitae openid = {}, curriculumVitaeVo = {}", openid, JSONUtil.toJsonStr(curriculumVitaeVo));
        return SystemJsonResponse.success(curriculumVitaeVo);
    }

    private CurriculumVitae queryCurriculumVitae(String openid) {
        return gqCurriculumVitaeService.getByOpenid(openid);
    }

    private User queryUser(String openid) {
        return gqUserService.getByOpenid(openid);
    }

    private Department queryDepartment(String departmentId) {
        return gqDepartmentService.getById(departmentId);
    }

    private Position queryPosition(String positionId) {
        return gqPositionService.getById(positionId);
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
     *
     * @return
     */
    @Override
    public SystemJsonResponse getAllCurriculumVitae(int page, int pageSize, String departmentId, Integer term) {
        Page<CurriculumVitae> pageInfo = gqCurriculumVitaeService.pageByDepartmentIdAndTerm(page, pageSize, departmentId, term);
        List<CurriculumVitae> records = pageInfo.getRecords();
        log.info("RecruitmentServiceImpl getAllCurriculumVitae CurriculumVitaes = {}", JSONUtil.toJsonStr(records));
        Map<String, User> userMap = gqUserService.getByOpenIds(
                records.stream()
                        .filter(Objects::nonNull)
                        .map(CurriculumVitae::getOpenid)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(
                User::getOpenid,
                Function.identity(),
                (o1, o2) -> o1)
        );
        log.info("RecruitmentServiceImpl getAllCurriculumVitae departmentId = {}, term = {}, userMap = {}", departmentId, term, JSONUtil.toJsonStr(userMap));
        Map<String, Department> departmentMap = gqDepartmentService.getByIds(
                records.stream()
                        .map(CurriculumVitae::getDepartmentId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Department::getId, Function.identity()));
        log.info("RecruitmentServiceImpl getAllCurriculumVitae departmentId = {}, term = {}, departmentMap = {}", departmentId, term, JSONUtil.toJsonStr(departmentMap));
        Map<String, Position> positionMap = gqPositionService.getByIds(
                records.stream()
                        .map(CurriculumVitae::getPositionId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Position::getId, Function.identity()));
        log.info("RecruitmentServiceImpl getAllCurriculumVitae departmentId = {}, term = {}, positionMap = {}", departmentId, term, JSONUtil.toJsonStr(positionMap));
        // 组装返还值
        List<CurriculumVitaeVo> curriculumVitaeVoList = records.stream()
                .filter(record -> userMap.containsKey(record.getOpenid())
                        && departmentMap.containsKey(record.getDepartmentId())
                        && positionMap.containsKey(record.getPositionId())
                ).map(record -> {
                    User user = userMap.get(record.getOpenid());
                    Department department = departmentMap.get(record.getDepartmentId());
                    Position position = positionMap.get(record.getPositionId());
                    return buildCurriculumVitaeVo(record, user, department, position);
                }).collect(Collectors.toList());
        log.info("RecruitmentServiceImpl getAllCurriculumVitae departmentId = {}, term = {}, curriculumVitaeVoList = {}", departmentId, term, JSONUtil.toJsonStr(curriculumVitaeVoList));

        return SystemJsonResponse.success(new SystemResultList<>(curriculumVitaeVoList, (int) pageInfo.getTotal()));
    }

    /**
     * 获取全部部门
     *
     * @return
     */
    @Override
    public SystemJsonResponse getDepartment() {
        List<Department> departments = gqDepartmentService.getAll();
        List<DepartmentVo> departmentVoList = new ArrayList<>();
        for (Department department : departments) {
            DepartmentVo departmentVo = new DepartmentVo();
            BeanUtils.copyProperties(department, departmentVo);
            departmentVoList.add(departmentVo);
        }
        log.info("RecruitmentServiceImpl getDepartment departmentVoList = {}", JSONUtil.toJsonStr(departmentVoList));
        return SystemJsonResponse.success(departmentVoList);
    }

    /**
     * 获取职位
     *
     * @param departmentId
     * @return
     */
    @Override
    public SystemJsonResponse getPosition(String departmentId) {
        List<Position> positions = gqPositionService.getByDepartmentId(departmentId);
        List<PositionVo> positionVos = new ArrayList<>();
        for (Position position : positions) {
            PositionVo positionVo = new PositionVo();
            BeanUtils.copyProperties(position, positionVo);
            positionVos.add(positionVo);
        }
        log.info("RecruitmentServiceImpl getPosition positionVos = {}", JSONUtil.toJsonStr(positionVos));
        return SystemJsonResponse.success(positionVos);
    }
}
