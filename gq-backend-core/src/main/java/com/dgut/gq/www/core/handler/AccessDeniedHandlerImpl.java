package com.dgut.gq.www.core.handler;

import com.alibaba.fastjson.JSON;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.util.WebUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 授权失败异常拦截
 * @since 2022-9-12
 * @author  hyj
 * @version  1.0
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        SystemJsonResponse systemJsonResponse = SystemJsonResponse.fail(GlobalResponseCode.USER_NOT_PERMISSIONS.getCode(),
                GlobalResponseCode.USER_NOT_PERMISSIONS.getMessage());
        //处理异常
        String s = JSON.toJSONString(systemJsonResponse);
        WebUtil.renderString(response, s);
    }
}
