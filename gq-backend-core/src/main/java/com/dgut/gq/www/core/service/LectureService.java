package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;

/**
 * 讲座
 * @author  hyj
 * @since  2022-10-8
 * @version  1.0
 */
public interface LectureService {



    /**
     * 获取正在进行的讲座
     * @return
     */
    SystemJsonResponse findUnStartLecture();

    /**
     * 抢票
     * @param id
     * @param openid
     * @return
     */
    SystemJsonResponse robTicket(String openid,String id);

    /**
     * 获取讲座回顾
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLectureReview(int page, int pageSize, String name);

    /**
     * 获取讲座预告
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    SystemJsonResponse getLectureTrailer(int page, int pageSize, String name);


    /**
     * 扫码签到
     * @param openid
     * @param id
     * @return
     */
    SystemJsonResponse ScanCheckin(String openid, String id);



    /**
     * 扫码签退
     * @param openid
     * @param id
     * @return
     */
    SystemJsonResponse ScanCheckout(String openid, String id);

    /**
     * 获取参加讲座的人员信息
     * @param page
     * @param pageSize
     * @param id
     * @param status
     */
    SystemResultList getAttendLectureUser(int page, int pageSize, String id, Integer status);
}
