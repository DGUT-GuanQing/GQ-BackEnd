package com.dgut.gq.www.admin.feign.client;

import com.dgut.gq.www.admin.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import io.swagger.annotations.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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




}
