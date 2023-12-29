package com.dgut.gq.www.recruit.config;


import com.dgut.gq.www.recruit.handler.GqLoginInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * 拦截处理
 * @since 2022-10-7
 * @author  hyj
 * @version  1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 跨域处理
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry
                //允许访问路径
                .addMapping("/**")
                //配置请求来源
                .allowedOrigins("*")
                //允许访问的方法
                .allowedMethods("GET","POST","DELETE","PUT","OPTION")
                //最大效应时间
                .maxAge(3600)
                .allowedHeaders("*")
                //是否允许携带参数,携带token
                .allowCredentials(true);
    }

    /**
     *   实现拦截
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //中央认证登录拦截器配置不用拦截的接口
        registry.addInterceptor(new GqLoginInterceptor(stringRedisTemplate))
                .excludePathPatterns(
                        "/recruitment/getAllCurriculumVitae",
                        "/recruitment/getDepartment",
                        "/recruitment/getPosition",
                        "/*.svg","/*.png","/*.js","/*.css","/*.html",
                        "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                        "/getLoginData",
                        "/25fVBQwXXS.txt"
                );
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        MappingJackson2HttpMessageConverter converter =new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }
    //@Bean
    ObjectMapper objectMapper(){
        ObjectMapper om =new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"));
        return  om;
    }

    /**
     *防止@EnableMvc把静态资源覆盖了
     * @param registry
     */
     public  void addResourceHandlers(ResourceHandlerRegistry registry){
        //解决静态资源
         registry.addResourceHandler("/**")
                 .addResourceLocations("classpath:/static/");
         //swagger
         registry.addResourceHandler("/swagger-ui.html")
                 .addResourceLocations("classpath:/META-INF/resources/");
         //swagger无法访问js
         registry.addResourceHandler("/webjars/**")
                 .addResourceLocations("classpath:/META-INF/resources/webjars/");
     }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        registrar.registerFormatters(registry);
    }
}
