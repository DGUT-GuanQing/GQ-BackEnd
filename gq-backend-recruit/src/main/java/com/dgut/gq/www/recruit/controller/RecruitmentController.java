package com.dgut.gq.www.recruit.controller;

import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.util.ParseToken;
import com.dgut.gq.www.recruit.common.model.dto.CurriculumVitaeDto;
import com.dgut.gq.www.recruit.common.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.recruit.common.model.vo.DepartmentVo;
import com.dgut.gq.www.recruit.common.model.vo.PositionVo;
import com.dgut.gq.www.recruit.service.RecruitmentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 招新接口
 * @author  hyj
 * @since  2023-5-10
 * @version  1.0
 */
@RequestMapping("/recruitment")
@RestController
@Api(tags = "招新模块")
public class RecruitmentController {


    @Autowired
    RecruitmentService recruitmentService;

    /**
     * 上传或者修改简历
     * @param
     * @return
     */
    @PostMapping("/updateSaveCurriculumVitae")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "上传或者修改简历")
    public SystemJsonResponse updateSaveCurriculumVitae(@RequestBody CurriculumVitaeDto curriculumVitaeDto,
                                                        HttpServletRequest request){
        String token =  request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
       return recruitmentService.updateOrSave(openid,curriculumVitaeDto);
    }

    /**
     * 获取我的简历
     * @param
     * @return
     */
    @PostMapping("/getMyCurriculumVitae")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "获取我的简历")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = CurriculumVitaeVo.class)
    }
    )
    public SystemJsonResponse getMyCurriculumVitae(HttpServletRequest request){
        String token =  request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return recruitmentService.getMyCurriculumVitae(openid);
    }




    /**
     * 获取全部简历
     * @param
     * @return
     */
    @PostMapping("/getAllCurriculumVitae")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "获取简历")
    @ApiImplicitParams({@ApiImplicitParam(value = "页数",name = "page",required = true),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",required = true),
            @ApiImplicitParam(value = "部门id",name = "departmentId",required = true),
            @ApiImplicitParam(value = "第几期新人",name = "term",required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response =CurriculumVitaeVo.class)
    }
    )
    public SystemJsonResponse getAllCurriculumVitae(int page , int pageSize, String departmentId, Integer term){
        return recruitmentService.getAllCurriculumVitae(page,pageSize, departmentId,term);
    }


    /**
     * 获取部门
     * @param
     * @return
     */
    @PostMapping("/getDepartment")
    @ApiOperation(value = "获取部门")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = DepartmentVo.class)
    }
    )
    public SystemJsonResponse getDepartment(){
        return recruitmentService.getDepartment();
    }





    /**
     * 获取职位
     * @param
     * @return
     */

    @PostMapping("/getPosition")
    @ApiOperation(value = "获取职位")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "部门id",name = "departmentId",required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = PositionVo.class)
    }
    )
    public SystemJsonResponse getPosition(String departmentId){
        return recruitmentService.getPosition(departmentId);
    }
}
