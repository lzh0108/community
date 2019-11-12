package com.lzh0108.community;

import com.lzh0108.community.entity.DiscussPost;
import com.lzh0108.community.service.AlphaService;
import com.lzh0108.community.controller.AlphaController;
import com.lzh0108.community.dao.AlphaDao;
import org.junit.*;
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
// 表明测试代码以CommunityApplication.class为配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    // Spring容器
    private ApplicationContext applicationContext;

    // 在类初始化之前执行，只执行一次，需用static修饰
    @BeforeClass
    public static void beforeClass() {
//        System.out.println("beforeClass");
    }

    // 在类销毁的时候只执行一次，需用static修饰
    @AfterClass
    public static void afterClass() {
//        System.out.println("afterClass");
    }

    // 每执行一个方法之前调用一次
    @Before
    public void before() {
//        System.out.println("before");

        // 初始化测试数据
    }

    // 每执行一个方法之后调用一次
    @After
    public void after() {
//        System.out.println("after");

        // 删除测试数据
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);

        // 从容器中获取自动装配的Bean
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());


        alphaDao = applicationContext.getBean("alphaHibernate", AlphaDao.class);
        System.out.println(alphaDao.select());
    }


    /**
     * Spring容器管理Bean
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
     * Qualifier 可以指定要注入的Bean的名字
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
