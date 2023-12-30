package com.dgut.gq.www.admin.service;


import com.dgut.gq.www.admin.model.dto.DepartmentDto;
import com.dgut.gq.www.admin.model.dto.LectureDto;
import com.dgut.gq.www.admin.model.dto.PositionDto;
import com.dgut.gq.www.admin.model.dto.PosterTweetDto;
import com.dgut.gq.www.common.common.SystemJsonResponse;

/**
 * 后台
 * @author  hyj
 * @since  2022-10-8
 * @version  1.0
 */

public interface BackendService {

    /**
     * 登陆
     * @param username
     * @param password
     * @return
     */
    SystemJsonResponse login(String username, String password);

    /**
     * 登出
     */
    void logout();


    /**
     * 获取参加讲座的用户
     * @param page
     * @param pageSize
     * @param id
     * @param status
     * @return
     */
    SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status);


    /**
     * 更新或者新增讲座
     * @param lectureDto
     * @return
     */
    SystemJsonResponse updateOrSaveLecture(LectureDto lectureDto);


    /**
     * 更新或者新增推文
     * @param posterTweetDto
     * @return
     */
    SystemJsonResponse saveUpdatePosterTweet(PosterTweetDto posterTweetDto);


    /**
     * 后台获取讲座
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLecture(int page, int pageSize, String name);


    /**
     * 导出参加讲座的用户
     * @param id
     * @param status
     * @return
     */
    SystemJsonResponse exportUser(String id, Integer status);


    /**
     * 导出简历
     * @param departmentId
     * @param term
     * @return
     */
    SystemJsonResponse exportCurriculumVitae(String departmentId, Integer term);


    /**
     * 删除讲座
     * @param id
     * @return
     */
    SystemJsonResponse deleteLecture(String id);

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
