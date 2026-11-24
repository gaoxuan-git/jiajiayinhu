package com.lidexin.mymodle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {"com.lidexin.mymodle", "com.lidexin.business"})
@MapperScan(basePackages = {"com.lidexin.business.mapper"})
public class MymodleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MymodleApplication.class, args);
    }

}
