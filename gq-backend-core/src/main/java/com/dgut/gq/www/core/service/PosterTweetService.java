package com.dgut.gq.www.core.service;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.core.model.dto.LectureDto;
import com.dgut.gq.www.core.model.dto.PosterTweetDto;

public interface PosterTweetService  {


    /**
     * 获取推文
     * @param type
     * @return
     */
    SystemJsonResponse getByType(Integer type);


    /**
     * 新增或者更新推文
     * @param posterTweetDto
     * @return
     */
    SystemJsonResponse updatePosterTweet(PosterTweetDto posterTweetDto);
}
