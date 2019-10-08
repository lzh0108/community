package com.lzh0108.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkComfig {

    private static final Logger logger = LoggerFactory.getLogger(WkComfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    public void init() {
        // 创建wk图片目录
        File file = new File(wkImageStorage);

        if (!file.exists()) {
            file.mkdir();
            logger.info("创建wk图片目录：" + wkImageStorage);
        }

    }

}