package com.dgut.gq.www.core.common.config;


import com.dgut.gq.www.core.common.wx.WxMappingJackson2HttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 微信配置
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-20
 */
@Configuration
public class WxConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}
