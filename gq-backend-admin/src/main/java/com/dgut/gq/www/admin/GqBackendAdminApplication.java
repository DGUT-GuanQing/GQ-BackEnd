package com.dgut.gq.www.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dgut.gq.www"})
public class GqBackendAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendAdminApplication.class, args);
    }

}
