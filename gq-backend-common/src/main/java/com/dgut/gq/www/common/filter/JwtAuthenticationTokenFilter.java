package com.dgut.gq.www.common.filter;


import cn.hutool.json.JSONUtil;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.model.entity.LoginUser;
import com.dgut.gq.www.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


/**
 * 判断小程序token
 * 有没有过期
 * 最后还要配置到security中
 * @since 2022-9-9
 * @author  hyj
 * @version  1.0
 */

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

   @Autowired
   private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws RuntimeException,ServletException, IOException {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        //解析token
        String openid;
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            throw  new RuntimeException("token错误");
        }
        openid = claims.getSubject();

        //从redis中获取信息，查看是否登录并且授予权限
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.PERMISSION +openid);
        LoginUser loginUser = JSONUtil.toBean(s, LoginUser.class);
        if(Objects.isNull(loginUser)||Objects.isNull(loginUser.getUser())){
            throw  new RuntimeException("token错误");
        }

        //获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, ( loginUser).getAuthorities());

        //将信息存入 SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

}






