package com.dgut.gq.www.core.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.core.mapper.PosterTweetMapper;
import com.dgut.gq.www.core.model.entity.PosterTweet;
import com.dgut.gq.www.core.model.vo.PosterTweetVo;
import com.dgut.gq.www.core.service.PosterTweetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
        PosterTweet posterTweet = new PosterTweet();
        PosterTweetVo posterTweetVo = new PosterTweetVo();
        String key = RedisGlobalKey.POSTER_TWEET + type;
        String s = stringRedisTemplate.opsForValue().get(key);
        //为空就去数据库查询
        if(s == null || s.equals("")){
            LambdaQueryWrapper<PosterTweet>lambdaQueryWrapper = new LambdaQueryWrapper<>();
            //查询最新的推文
            lambdaQueryWrapper.orderByDesc(PosterTweet::getCreateTime);
            lambdaQueryWrapper.last("LIMIT 1");
            lambdaQueryWrapper.eq(PosterTweet::getIsDeleted,0);
            lambdaQueryWrapper.eq(PosterTweet::getType,type);

            posterTweet= posterTweetMapper.selectOne(lambdaQueryWrapper);
            //存入redis
            BeanUtils.copyProperties(posterTweet,posterTweetVo);
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(posterTweetVo));
        }else {
            posterTweetVo =  JSONUtil.toBean(s,PosterTweetVo.class);
        }
        return SystemJsonResponse.success(posterTweetVo);
    }

}
