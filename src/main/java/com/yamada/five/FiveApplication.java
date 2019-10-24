package com.yamada.five;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@MapperScan("com.yamada.five.mapper")
@SpringBootApplication
@EnableCaching
public class FiveApplication {

    public static void main(String[] args) {
        // SpringBoot 2.X 的 spring-boot-starter-data-redis 默认是以 lettuce 作为连接池的，
        // 而在 lettuce ， elasticsearch transport 中都会依赖netty, 二者的netty 版本不一致，不能够兼容
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(FiveApplication.class, args);
    }

//    @Bean
//    public Queue queue() {
//        return new Queue("esitem");
//    }
}
