package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.PosterTweetMapper;
import com.dgut.gq.www.common.db.entity.PosterTweet;
import com.dgut.gq.www.common.db.service.GqPosterTweetService;
import org.springframework.stereotype.Service;

@Service
public class GqPosterTweetServiceImpl
        extends ServiceImpl<PosterTweetMapper, PosterTweet> implements GqPosterTweetService {
    @Override
    public PosterTweet getLatestByType(Integer type) {
        return lambdaQuery()
                .eq(PosterTweet::getType, type)
                .eq(PosterTweet::getIsDeleted, 0)
                .orderByDesc(PosterTweet::getCreateTime)
                .last("limit 1")
                .one();
    }
}
