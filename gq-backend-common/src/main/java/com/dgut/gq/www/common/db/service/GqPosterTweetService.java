package com.dgut.gq.www.common.db.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.PosterTweet;

public interface GqPosterTweetService extends IService<PosterTweet> {

    /**
     * 获取最新的推文或者
     * @param type 0-活动 1-招新
     * @return
     */
    PosterTweet getLatestByType(Integer type);
}
