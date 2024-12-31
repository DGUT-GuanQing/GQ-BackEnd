package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.core.common.model.dto.LectureDto;

/**
 * 讲座
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-8
 */
public interface LectureService {

    /**
     * 获取正在进行的讲座
     *
     * @return
     */
    SystemJsonResponse findUnStartLecture();


    /**
     * 抢票
     *
     * @param id
     * @param openid
     * @return
     */
    SystemJsonResponse robTicket(String openid, String id);


    /**
     * 获取讲座回顾
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLectureReview(int page, int pageSize, String name);


    /**
     * 获取讲座预告
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLectureTrailer(int page, int pageSize, String name);


    /**
     * 获取参加讲座的人员信息
     *
     * @param page
     * @param pageSize
     * @param id
     * @param status
     */
    SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status);


    /**
     * 更新或者新增讲座
     *
     * @param lectureDto
     */
    SystemJsonResponse updateSaveLecture(LectureDto lectureDto);


    /**
     * 后台获取讲座信息
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLecture(int page, int pageSize, String name);


    /**
     * 导出参加讲座的用户信息
     *
     * @param id
     * @param status
     * @return
     */
    SystemJsonResponse exportAttendLectureUser(String id, Integer status);


    /**
     * 删除讲座
     *
     * @param id
     * @return
     */
    SystemJsonResponse deleteLecture(String id);
}
