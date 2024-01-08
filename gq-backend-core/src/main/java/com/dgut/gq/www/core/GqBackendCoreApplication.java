package com.dgut.gq.www.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"com.dgut.gq.www.core.common.feign"})
@SpringBootApplication(scanBasePackages = {"com.dgut.gq.www"})
public class GqBackendCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendCoreApplication.class, args);
    }

}
