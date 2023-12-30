package com.dgut.gq.www.recruit.feign.server;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.recruit.model.dto.DepartmentDto;
import com.dgut.gq.www.recruit.model.dto.PositionDto;
import com.dgut.gq.www.recruit.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Feign招新模块服务端
 */
@RequestMapping("/feign-recruit")
@RestController
public class RecruitServer {

    @Autowired
    private RecruitmentService recruitmentService;


    /**
     * 远程调用导出简历
     * @param
     * @return
     */
    @GetMapping("/exportCurriculumVitae")
    SystemJsonResponse exportCurriculumVitae(@RequestParam String departmentId, @RequestParam Integer term){
        return recruitmentService.exportCurriculumVitae(departmentId,term);
    }


    /**
     * 删除部门远程调用
     * @param id
     * @return
     */
    @DeleteMapping("/deleteDepartment/{id}")
    SystemJsonResponse deleteDepartment(@PathVariable String id){
        return recruitmentService.deleteDepartment(id);
    }



    /**
     * 删除职位远程调用
     * @param id
     * @return
     */
    @DeleteMapping("/deletePosition/{id}")
    public SystemJsonResponse deletePosition(@PathVariable String id){
        return recruitmentService.deletePosition(id);
    }


    /**
     * 新增或者修改部门远程调用
     * @param departmentDto
     * @return
     */
    @PostMapping("/saveAndUpdateDep")
    SystemJsonResponse saveAndUpdateDep(@RequestBody DepartmentDto departmentDto){
        return recruitmentService.saveAndUpdateDep(departmentDto);
    }



    /**
     * 新增或者修改职位远程调用
     * @param positionDto
     * @return
     */
    @PostMapping("/saveAndUpdatePos")
    SystemJsonResponse saveAndUpdatePos(@RequestBody PositionDto positionDto){
        return recruitmentService.saveAndUpdatePos(positionDto);
    }

}
