package com.dgut.gq.www.recruit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dgut.gq.www"})
public class GqBackendRecruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendRecruitApplication.class, args);
    }

}
