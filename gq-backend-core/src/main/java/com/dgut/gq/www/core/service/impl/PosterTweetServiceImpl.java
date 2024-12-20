package com.dgut.gq.www.core.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.db.entity.PosterTweet;
import com.dgut.gq.www.common.db.mapper.PosterTweetMapper;

import com.dgut.gq.www.core.common.model.dto.PosterTweetDto;

import com.dgut.gq.www.core.common.model.vo.PosterTweetVo;
import com.dgut.gq.www.core.service.PosterTweetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PosterTweetServiceImpl implements PosterTweetService {

    @Autowired
    private PosterTweetMapper posterTweetMapper;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取推文
     *
     * @param type
     * @return
     */
    @Override
    public SystemJsonResponse getByType(Integer type) {
        String key = RedisGlobalKey.POSTER_TWEET + type;

        PosterTweetVo posterTweetVo = Optional.ofNullable(stringRedisTemplate.opsForValue().get(key))
                .map(s -> JSONUtil.toBean(s, PosterTweetVo.class))
                .orElseGet(() -> {
                    LambdaQueryWrapper<PosterTweet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    //查询最新的推文
                    lambdaQueryWrapper.orderByDesc(PosterTweet::getCreateTime);
                    lambdaQueryWrapper.last("LIMIT 1");
                    lambdaQueryWrapper.eq(PosterTweet::getIsDeleted, 0);
                    lambdaQueryWrapper.eq(PosterTweet::getType, type);
                    PosterTweet posterTweet = posterTweetMapper.selectOne(lambdaQueryWrapper);
                    PosterTweetVo newPosterTweetVo = new PosterTweetVo();

                    //存入redis
                    BeanUtils.copyProperties(posterTweet, newPosterTweetVo);
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newPosterTweetVo));
                    stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);

                    return newPosterTweetVo;
                });

        return SystemJsonResponse.success(posterTweetVo);
    }

    /**
     * 更新或者新增推文
     *
     * @param posterTweetDto
     * @return
     */
    @Override
    public SystemJsonResponse updatePosterTweet(PosterTweetDto posterTweetDto) {
        String id = posterTweetDto.getId();
        PosterTweet posterTweet = new PosterTweet();
        BeanUtils.copyProperties(posterTweetDto, posterTweet);
        posterTweet.setUpdateTime(LocalDateTime.now());
        String state;
        //新增
        if (id == null || id.equals("")) {
            //新增数据
            posterTweet.setCreateTime(LocalDateTime.now());
            posterTweetMapper.insert(posterTweet);
            state = "新增成功";
        } else {
            posterTweetMapper.updateById(posterTweet);
            state = "更新成功";
        }
        stringRedisTemplate.delete(RedisGlobalKey.POSTER_TWEET + posterTweetDto.getType());
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), state);
    }

}
