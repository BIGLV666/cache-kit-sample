package com.example.cachekitsample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.cachekitsample.user")
public class CacheKitSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheKitSampleApplication.class, args);
    }
}
