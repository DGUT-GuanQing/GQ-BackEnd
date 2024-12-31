package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;

public interface PosterTweetService {


    /**
     * 获取推文
     *
     * @param type 0-活动 1-招新
     * @return
     */
    SystemJsonResponse getByType(Integer type);
}
