package com.yuetiku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yuetiku")
public class YueTikuApplication {
    public static void main(String[] args) {
        SpringApplication.run(YueTikuApplication.class, args);
    }
}
