package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.core.model.dto.DepartmentDto;
import com.dgut.gq.www.core.model.dto.LectureDto;
import com.dgut.gq.www.core.model.dto.PositionDto;
import com.dgut.gq.www.core.model.dto.PosterTweetDto;

/**
 * 后台
 * @author  hyj
 * @since  2022-10-8
 * @version  1.0
 */

public interface BackendService {
    /**
     * 后台登入
     * @param user
     * @return
     */
    SystemJsonResponse login(User user) ;

    /**
     * 后台登出
     */
    void logout();

    /**
     * 获取参加讲座的人员信息
     * @param page
     * @param pageSize
     * @param id
     * @return
     */
    SystemJsonResponse getAllUser(int page, int pageSize, String id,Integer status);


    /**
     * 新增或者更新讲座
     * @param lectureDto
     * @return
     */
    SystemJsonResponse updateOrSave(LectureDto lectureDto);

    /**
     * 新增或者更新推文
     * @param posterTweetDto
     * @return
     */
    SystemJsonResponse saveUpdatePosterTweet(PosterTweetDto posterTweetDto);

    /**
     * 获取讲座
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
     * @param
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
     * 新增或者修稿部门
     * @param departmentDto
     * @return
     */
    SystemJsonResponse saveAndUpdateDep(DepartmentDto departmentDto);


    /**
     * 新增或者修改职位
     * @param positionDto
     * @return
     */
    SystemJsonResponse saveAndUpdatePos(PositionDto positionDto);

    /**
     * 绑定票号和学号
     * @param studentId
     * @param ticketId
     * @return
     */
    SystemJsonResponse bandTicket(String studentId, String ticketId);


    /**
     * 导出票号和学号绑定信息
     * @param startId
     * @param endId
     * @return
     */
    SystemJsonResponse exportTicketBand(String startId, String endId);
}
