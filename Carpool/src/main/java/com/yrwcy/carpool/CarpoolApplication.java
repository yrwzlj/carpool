package com.yrwcy.carpool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.yrwcy.carpool.mapper")
@SpringBootApplication
@EnableScheduling
public class CarpoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarpoolApplication.class, args);
    }

}
