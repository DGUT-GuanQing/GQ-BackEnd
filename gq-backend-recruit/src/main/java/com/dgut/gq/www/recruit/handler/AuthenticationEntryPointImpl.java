package com.dgut.gq.www.recruit.handler;

import com.alibaba.fastjson.JSON;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.util.WebUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证全局异常处理
 * @since 2022-9-12
 * @version  1.0
 * @author  hyj
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        SystemJsonResponse systemJsonResponse=SystemJsonResponse.fail
                (GlobalResponseCode.USER_NOT_LOGIN.getCode(),
                        GlobalResponseCode.USER_NOT_LOGIN.getMessage());
        String s = JSON.toJSONString(systemJsonResponse);
        //处理异常
        WebUtil.renderString(response, s);
    }
}
