package com.sky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class RrdpApplication {
    public static void main(String[] args) {
        SpringApplication.run(RrdpApplication.class, args);
    }

}
