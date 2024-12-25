package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.core.common.model.dto.PosterTweetDto;

public interface PosterTweetService {


    /**
     * 获取推文
     *
     * @param type 0-活动 1-招新
     * @return
     */
    SystemJsonResponse getByType(Integer type);


    /**
     * 新增或者更新推文
     *
     * @param posterTweetDto
     * @return
     */
    SystemJsonResponse updatePosterTweet(PosterTweetDto posterTweetDto);
}
