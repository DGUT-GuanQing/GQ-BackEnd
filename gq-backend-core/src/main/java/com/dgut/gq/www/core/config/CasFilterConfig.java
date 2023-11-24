package com.dgut.gq.www.core.config;


import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 功能描述：CAS集成核心配置类
 * 修改记录:
 * <pre>
 * 修改时间：
 * 修改人：
 * 修改内容：
 * </pre>
 *
 */
@Configuration
public class CasFilterConfig {


    /**
     * 需要走cas拦截的地址（/* 所有地址都拦截）
     */
    private  final static String URL_PATTERN="/user/getUserInfo" ;




    /**
     * 默认的cas地址，防止通过 配置信息获取不到
     */
    @Value("${cas.server-url:https://127.0.0.1:8090/authserver}")
    private String casUrl;

    /**
     * 应用访问地址（这个地址需要在cas服务端进行配置）
     */
    @Value("${app.url}")
    public String appUrl;

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
        ServletListenerRegistrationBean listenerRegistrationBean = new ServletListenerRegistrationBean();
        listenerRegistrationBean.setListener(new SingleSignOutHttpSessionListener());
        listenerRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return listenerRegistrationBean;
    }

    /**
     * 单点登录退出
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SingleSignOutFilter());
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.addInitParameter("casServerUrlPrefix", casUrl);
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    /**
     * 单点登录认证
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean AuthenticationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setName("CAS Filter");
        registrationBean.addInitParameter("casServerLoginUrl", casUrl);
        registrationBean.addInitParameter("serverName", appUrl);
        registrationBean.setOrder(3);
        return registrationBean;
    }

    /**
     * 单点登录校验
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean Cas30ProxyReceivingTicketValidationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setName("CAS Validation Filter");
        registrationBean.addInitParameter("casServerUrlPrefix", casUrl);
        registrationBean.addInitParameter("serverName", appUrl);
        registrationBean.setOrder(4);
        return registrationBean;
    }

    /**
     * 单点登录请求包装
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean httpServletRequestWrapperFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(5);
        return registrationBean;
    }
}
