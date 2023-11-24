package com.dgut.gq.www.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dgut.gq"})
public class GqBackendCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GqBackendCoreApplication.class, args);
    }

}
