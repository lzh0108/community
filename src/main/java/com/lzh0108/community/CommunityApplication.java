package com.lzh0108.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init() {
        // 解决Netty启动冲突问题（Redis和Elasticsearch都基于Netty）
        // 阅读Netty4Utils类的源码解决，Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        // 会自动的创建Spring容器，并将Bean（配置类所在的包，以及子包下的Bean（带有Component、Controller、Service、Repository注解））装配到容器里
        SpringApplication.run(CommunityApplication.class, args);
    }

}
