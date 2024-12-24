package com.dgut.gq.www.recruit.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.recruit.common.model.dto.CurriculumVitaeDto;
import com.dgut.gq.www.recruit.common.model.dto.DepartmentDto;
import com.dgut.gq.www.recruit.common.model.dto.PositionDto;


/**
 * 简历
 *
 * @author hyj
 */
public interface RecruitmentService {

    /**
     * 上传或者修改简历
     *
     * @param openid
     * @param curriculumVitaeDto
     * @return
     */
    SystemJsonResponse updateOrSave(String openid, CurriculumVitaeDto curriculumVitaeDto);

    /**
     * 获取我的简历
     *
     * @param openid
     * @return
     */
    SystemJsonResponse getMyCurriculumVitae(String openid);

    /**
     * 获取全部简历
     *
     * @return
     */
    SystemJsonResponse getAllCurriculumVitae(int page, int pageSize, String departmentId, Integer term);

    /**
     * 获取部门
     *
     * @return
     */
    SystemJsonResponse getDepartment();

    /**
     * 获取职位
     *
     * @param departmentId
     * @return
     */
    SystemJsonResponse getPosition(String departmentId);
}
