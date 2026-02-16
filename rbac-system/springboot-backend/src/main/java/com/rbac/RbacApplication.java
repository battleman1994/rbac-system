package com.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RbacApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RbacApplication.class, args);
    }
}
