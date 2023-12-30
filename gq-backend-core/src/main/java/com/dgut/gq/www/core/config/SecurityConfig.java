package com.dgut.gq.www.core.config;


import com.dgut.gq.www.common.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 配置安全框架
 * 包括权限和认证
 * @since 2022-9-26
 * @author  hyj
 * @version  1.0
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//权限注解
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 密码的加密
     * 将BCryptPasswordEncoder注入容器
     */
    @Bean
    public PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     *传入密码和账户的bean
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 认证异常
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 授权
     */
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 对登录进行放行
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/wxLogin/{code}").anonymous()
                .antMatchers("/25fVBQwXXS.txt").permitAll()
                .antMatchers( "/feign-user/**").permitAll()
                .antMatchers("/feign-lecture/**").permitAll()
                .antMatchers("/feign-poster/**").permitAll()
                //讲座全部信息所有人都可以访问
                .antMatchers("/lecture/**").permitAll()
                .antMatchers("/user/getUserInfo").permitAll()
                .antMatchers("/common/posterTweet/{type}").permitAll()
                //swagger
                .antMatchers("/swagger-resources/**", "/webjars/**",
                        "/v2/**", "/swagger-ui.html/**").permitAll()
                .antMatchers("/common/**").permitAll()
                //静态资源
                .antMatchers("/*.svg","/*.png","/*.js","/*.css","/*.html").permitAll()
                //正在进行的讲座所有人都可以访问
                //.antMatchers("/lecture/unStartLecture").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //将过滤器配置加入security框架,请求时候验证有没有登录
        http.addFilterBefore(authenticationTokenFilter,UsernamePasswordAuthenticationFilter.class);

        //配置异常处理器
        http.exceptionHandling()
                //认证失败
                .authenticationEntryPoint(authenticationEntryPoint)
                //授权失败
                .accessDeniedHandler(accessDeniedHandler);
        http.cors();

    }

}
