package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;

public interface PosterTweetService  {


    /**
     * 获取推文
     * @param type
     * @return
     */
    SystemJsonResponse getByType(Integer type);
}
