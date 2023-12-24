package com.dgut.gq.www.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dgut.gq.www"})
public class GqBackendCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendCommonApplication.class, args);
    }

}
