package com.dgut.gq.www.recruit.feign.server;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.recruit.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign简历模块服务端
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


}
