package com.dgut.gq.www.recruit.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.recruit.model.dto.CurriculumVitaeDto;
import com.dgut.gq.www.recruit.model.dto.DepartmentDto;
import com.dgut.gq.www.recruit.model.dto.PositionDto;


/**
 * 简历
 * @author  hyj
 */
public interface RecruitmentService {

    /**
     * 上传或者修改简历
     * @param openid
     * @param curriculumVitaeDto
     * @return
     */
    SystemJsonResponse updateOrSave(String openid, CurriculumVitaeDto curriculumVitaeDto);


    /**
     * 获取我的简历
     * @param openid
     * @return
     */
    SystemJsonResponse getMyCurriculumVitae(String openid);


    /**
     * 获取全部简历
     * @return
     */
    SystemJsonResponse getAllCurriculumVitae(int page, int pageSize,String departmentId,Integer term);





    /**
     * 获取部门
     * @return
     */
    SystemJsonResponse getDepartment();


    /**
     * 获取职位
     * @param departmentId
     * @return
     */
    SystemJsonResponse getPosition(String departmentId);


    /**
     * 导出简历
     * @param departmentId
     * @param term
     */
    SystemJsonResponse exportCurriculumVitae(String departmentId, Integer term);

    /**
     * 删除部门
     * @param id
     * @return
     */
    SystemJsonResponse deleteDepartment(String id);

    /**
     * 删除职位
     * @param id
     * @return
     */
    SystemJsonResponse deletePosition(String id);


    /**
     * 新增或者更新部门
     * @param departmentDto
     * @return
     */
    SystemJsonResponse saveAndUpdateDep(DepartmentDto departmentDto);


    /**
     * 新增或者更新职位
     * @param positionDto
     * @return
     */
    SystemJsonResponse saveAndUpdatePos(PositionDto positionDto);
}
