package com.clwu.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.clwu.sms"})
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }

}
