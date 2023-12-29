package com.dgut.gq.www.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger提供接口
 * @author hyj
 * @since 2022-9-3
 * @version 1.0
 */
@Configuration
@EnableSwagger2
//@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer{

        @Bean
        public Docket docket(Environment environment) {
            Profiles profiles = Profiles.of("pro");
            boolean flag = environment.acceptsProfiles(profiles);
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .enable(!flag)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.dgut.gq.www.admin"))
                    .paths(PathSelectors.any())
                    .build();

        }


        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("莞青小程序")
                    .description("后台模块接口")
                    .version("1.0.0")
                    // 作者信息
                    .contact(new Contact("莞青技术组", "", ""))
                    .build();
        }
}
