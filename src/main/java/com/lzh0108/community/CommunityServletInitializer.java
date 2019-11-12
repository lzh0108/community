package com.lzh0108.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Tomcat入口，Tomcat会先访问这个方法，通过这个方法作为入口来启动运行这个项目
 */

public class CommunityServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 这个项目的核心配置文件
        return builder.sources(CommunityApplication.class);
    }
}
