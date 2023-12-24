package com.dgut.gq.www.recruit.handler;


import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.util.ParseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 中央认证的拦截器
 * @since  2022-10-7
 * @author  hyj
 */
@Component
public class GqLoginInterceptor implements HandlerInterceptor {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public GqLoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 拦截请求 判断中央认证是否登录或者过期
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("sss");
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.DGUT_LOGIN + openid);
        if(s == null || s.equals("") || !s.equals(openid)){
            throw new GlobalSystemException(GlobalResponseCode.GQ_USER_ACCOUNT_OVERDUE.getCode(), GlobalResponseCode.GQ_USER_ACCOUNT_OVERDUE.getMessage());
        }
        return true;
    }
}
