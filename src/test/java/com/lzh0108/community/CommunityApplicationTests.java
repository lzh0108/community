package com.lzh0108.community;

import com.lzh0108.community.service.AlphaService;
import com.lzh0108.community.controller.AlphaController;
import com.lzh0108.community.dao.AlphaDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);

        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());

        alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
        System.out.println(alphaDao.select());
    }


    /**
     * Spring Boot 管理 Bean
     */
    @Test
    public void testBeanManagement() {
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }


    @Test
    public void testBeanConfig() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    /**
     * 自动注入
     */

    @Autowired
    @Qualifier("alphaHibernate")
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private AlphaController alphaController;

    @Test
    public void testDI() {
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(alphaController);
    }

}
