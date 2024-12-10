package com.dgut.gq.www.admin.controller;

import com.dgut.gq.www.admin.common.model.dto.DepartmentDto;
import com.dgut.gq.www.admin.common.model.dto.LectureDto;
import com.dgut.gq.www.admin.common.model.dto.PositionDto;
import com.dgut.gq.www.admin.common.model.dto.PosterTweetDto;
import com.dgut.gq.www.admin.common.model.vo.CurriculumVitaeVo;
import com.dgut.gq.www.admin.common.model.vo.LectureVo;
import com.dgut.gq.www.admin.common.model.vo.UserVo;
import com.dgut.gq.www.admin.service.BackendService;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员后台
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-7
 */
@RestController
@RequestMapping("/backend")
@Api(tags = "后台管理模块")
public class BackendController {

    @Autowired
    private BackendService backendService;


    /**
     * 后台的登录
     *
     * @param userName
     * @param password
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "后台登录")
    @ApiImplicitParams({@ApiImplicitParam(value = "用户名", name = "userName", required = true), @ApiImplicitParam(value = "密码", name = "password", required = true)})
    public SystemJsonResponse backendLogin(String userName, String password) {
        return backendService.login(userName, password);
    }

    /**
     * 后台登出
     *
     * @return
     */
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "后台登出")
    public SystemJsonResponse backendLogout() {
        backendService.logout();
        return SystemJsonResponse.success("退出成功");
    }

    /**
     * 获取参加讲座的人员分页信息
     *
     * @param page
     * @return
     */
    @GetMapping("/alluser")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "获取参加讲座的用户信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "讲座id", name = "id", required = true), @ApiImplicitParam(value = "状态 0-抢到票的人  1-签到和签退都完成的人(观看了讲座)", name = "status", required = true), @ApiImplicitParam(value = "页数", name = "page", required = true), @ApiImplicitParam(value = "每页数量", name = "pageSize", required = true)})
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功", response = UserVo.class)})
    public SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status) {
        return backendService.getAttendLectureUser(page, pageSize, id, status);
    }

    /**
     * 新增或者更新讲座
     *
     * @param lectureDto
     * @return
     */
    @PostMapping("/updateSaveLecture")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "更新或者新增讲座预告信息")
    public SystemJsonResponse updateOrSaveLecture(@RequestBody LectureDto lectureDto) {
        return backendService.updateOrSaveLecture(lectureDto);
    }

    /**
     * 新增或者更新推文信息
     *
     * @param posterTweetDto
     * @return
     */
    @PostMapping("/updatePosterTweet")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "更新或者新增推文信息")
    public SystemJsonResponse saveUpdatePosterTweet(@RequestBody PosterTweetDto posterTweetDto) {
        return backendService.saveUpdatePosterTweet(posterTweetDto);
    }

    /**
     * 获取讲座
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/allLecture")
    @ApiOperation(value = "后台获取讲座")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "页数", name = "page", required = true), @ApiImplicitParam(value = "每页数量", name = "pageSize", required = true), @ApiImplicitParam(value = "模糊查询字段", name = "name", required = true)})
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功", response = LectureVo.class)})
    public SystemJsonResponse getLecture(int page, int pageSize, String name) {
        return backendService.getLecture(page, pageSize, name);
    }

    /**
     * 导出参加讲座的用户
     *
     * @param id
     * @param status
     * @return
     */
    @GetMapping("/exportUser")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "导出参加讲座的用户")
    @ApiImplicitParams({@ApiImplicitParam(value = "讲座id", name = "id", required = true), @ApiImplicitParam(value = "状态 0-抢到票的  1-观看讲座的", name = "status", required = true)})
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功", response = UserVo.class)})
    public SystemJsonResponse exportUser(String id, Integer status) {
        return backendService.exportUser(id, status);
    }

    /**
     * 导出简历
     *
     * @param departmentId
     * @param term
     * @return
     */
    @ApiResponse(code = 200, message = "成功", response = CurriculumVitaeVo.class)
    @GetMapping("/exportCurriculumVitae")
    @PreAuthorize("hasAuthority('admin')")
    @ApiOperation(value = "导出简历")
    @ApiImplicitParams({@ApiImplicitParam(value = "部门id", name = "departmentId", required = true), @ApiImplicitParam(value = "第几期新人", name = "term", required = true)})
    @ApiResponses({@ApiResponse(code = 200, message = "查询成功", response = CurriculumVitaeVo.class)})
    public SystemJsonResponse getAllCurriculumVitae(String departmentId, Integer term) {
        return backendService.exportCurriculumVitae(departmentId, term);
    }

    /**
     * 删除讲座
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除讲座")
    @DeleteMapping("/deleteLecture/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParam(value = "讲座id", name = "id", required = true)
    public SystemJsonResponse deleteLecture(@PathVariable String id) {
        return backendService.deleteLecture(id);
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除部门")
    @DeleteMapping("/deleteDepartment/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParam(value = "部门id", name = "id", required = true)
    public SystemJsonResponse deleteDepartment(@PathVariable String id) {
        return backendService.deleteDepartment(id);
    }

    /**
     * 删除职位
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除职位")
    @DeleteMapping("/deletePosition/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParam(value = "职位id", name = "id", required = true)
    public SystemJsonResponse deletePosition(@PathVariable String id) {
        return backendService.deletePosition(id);
    }

    /**
     * 新增或者修改部门
     *
     * @param departmentDto
     * @return
     */
    @ApiOperation(value = "新增或者修改部门 ,id为空就是新增，否则就是修改")
    @PostMapping("/saveAndUpdateDep")
    @PreAuthorize("hasAuthority('admin')")
    public SystemJsonResponse saveAndUpdateDep(@RequestBody DepartmentDto departmentDto) {
        return backendService.saveAndUpdateDep(departmentDto);
    }

    /**
     * 新增或者修改职位
     *
     * @param positionDto
     * @return
     */
    @ApiOperation(value = "新增或者修改职位 ,id为空就是新增，否则就是修改")
    @PostMapping("/saveAndUpdatePos")
    @PreAuthorize("hasAuthority('admin')")
    public SystemJsonResponse saveAndUpdatePos(@RequestBody PositionDto positionDto) {
        return backendService.saveAndUpdatePos(positionDto);
    }

    /**
     * 绑定票号和学号
     *
     * @param studentId
     * @param ticketId
     * @return
     */
    @ApiOperation(value = "绑定票号和学号")
    @PostMapping("/bandTicket")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "学号", name = "studentId", required = true), @ApiImplicitParam(value = "票号", name = "ticketId", required = true)})
    public SystemJsonResponse bandTicket(@RequestParam("studentId") String studentId, @RequestParam("ticketId") String ticketId) {
        return SystemJsonResponse.success();
    }

    /**
     * 导出票号和学号绑定信息
     *
     * @param startId
     * @param endId
     * @return
     */
    @ApiOperation(value = "导出票号和学号绑定信息")
    @GetMapping("/exportTicketBand")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "起始票号", name = "startId", required = true), @ApiImplicitParam(value = "结束票号", name = "endId", required = true)})
    public SystemJsonResponse exportTicketBand(@RequestParam("startId") String startId, @RequestParam("endId") String endId) {
        return SystemJsonResponse.success();
    }
}
