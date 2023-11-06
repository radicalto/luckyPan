package com.luckypan;


import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync // 异步
@SpringBootApplication(scanBasePackages = {"com.luckypan"})
@MapperScan(basePackages = {"com.luckypan.mapper"})
@EnableTransactionManagement
@EnableScheduling // 定时任务
public class LuckyPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(LuckyPanApplication.class, args);
    }
}
