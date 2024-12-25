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
}
