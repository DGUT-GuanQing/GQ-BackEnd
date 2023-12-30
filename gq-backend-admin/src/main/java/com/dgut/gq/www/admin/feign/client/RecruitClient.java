package com.dgut.gq.www.admin.feign.client;

import com.dgut.gq.www.admin.model.dto.DepartmentDto;
import com.dgut.gq.www.admin.model.dto.PositionDto;
import com.dgut.gq.www.admin.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import io.swagger.annotations.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Feign讲座模块客户端
 */
@FeignClient(name = "gq-backend-recruit",contextId = "recruit")
public interface RecruitClient {



    /**
     * 远程调用导出简历
     * @param
     * @return
     */
    @GetMapping("/feign-recruit/exportCurriculumVitae")
    SystemJsonResponse exportCurriculumVitae(@RequestParam String departmentId, @RequestParam Integer term);



    /**
     * 删除部门远程调用
     * @param id
     * @return
     */
    @DeleteMapping("/feign-recruit/deleteDepartment/{id}")
    SystemJsonResponse deleteDepartment(@PathVariable String id);



    /**
     * 删除职位远程调用
     * @param id
     * @return
     */
    @DeleteMapping("/feign-recruit/deletePosition/{id}")
    public SystemJsonResponse deletePosition(@PathVariable String id);


    /**
     * 新增或者修改部门远程调用
     * @param departmentDto
     * @return
     */
    @PostMapping("/feign-recruit/saveAndUpdateDep")
    SystemJsonResponse saveAndUpdateDep(@RequestBody DepartmentDto departmentDto);



    /**
     * 新增或者修改职位远程调用
     * @param positionDto
     * @return
     */
    @PostMapping("/feign-recruit/saveAndUpdatePos")
    SystemJsonResponse saveAndUpdatePos(@RequestBody PositionDto positionDto);


}
