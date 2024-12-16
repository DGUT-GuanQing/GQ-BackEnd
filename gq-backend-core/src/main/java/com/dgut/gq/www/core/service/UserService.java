package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.db.entity.User;
import com.dgut.gq.www.core.common.model.dto.UserDto;

public interface UserService {
    /**
     * 微信登录
     *
     * @param code
     * @return
     */
    String wxLogin(String code);


    /**
     * 获取小程序d端个人信息
     *
     * @param openid
     * @return
     */
    SystemJsonResponse getMe(String openid);

    /**
     * 获取自己参加过的讲座信息
     *
     * @param openid
     * @return
     */
    SystemJsonResponse getMyLecture(String openid, Integer page, Integer pageSize);


    /**
     * 是否抢到票
     *
     * @param openid
     * @return
     */
    SystemJsonResponse isGrabTicket(String openid);


    /**
     * 中央认证
     *
     * @param userDto
     * @return
     */
    SystemJsonResponse dgutLogin(UserDto userDto, String openid);


    /**
     * 签到
     *
     * @param lectureId
     * @param openid
     * @return
     */
    SystemJsonResponse signIn(String lectureId, String openid);


    /**
     * 是否在黑名单
     *
     * @param openid
     * @return
     */
    SystemJsonResponse isBlack(String openid);

    /**
     * 根据用户名获取用户信息
     *
     * @param username
     * @return
     */
    User getUserByUsername(String username);
}
