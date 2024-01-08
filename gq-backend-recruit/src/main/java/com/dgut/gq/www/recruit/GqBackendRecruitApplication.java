package com.dgut.gq.www.recruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.dgut.gq.www"})
@EnableFeignClients(basePackages = {"com.dgut.gq.www.recruit.common.feign"})
public class GqBackendRecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendRecruitApplication.class, args);
    }

}
