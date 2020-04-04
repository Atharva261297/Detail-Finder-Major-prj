package com.destro.linkcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LinkCalculatorMain extends SpringBootServletInitializer {
    public static void main(final String[] args) {
        SpringApplication.run(LinkCalculatorMain.class, args);
    }
}
