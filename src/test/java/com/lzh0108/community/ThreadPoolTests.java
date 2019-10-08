package com.lzh0108.community;

import com.lzh0108.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);

    // JDK普通线程池，初始化以后包含5个线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;


    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // JDK普通线程池
    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    // JDK定时任务线程池
    @Test
    public void testSchedaledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello SchedaledExecutorService");
            }
        };

        // command：要执行的任务
        // initialDelay：延迟首次执行的时间
        // delay：执行之间的延迟
        // unit：initialDelay和delay参数的时间单位
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }


    // Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    // Spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        Date startTime = new Date(System.currentTimeMillis() + 10000);
        // task：执行的任务
        // startTime：第一次开始执行的时间
        // period：执行之间的延迟，默认以毫秒为单位
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);

        sleep(30000);
    }

    // Spring普通线程池（简化）
/*    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }

        sleep(10000);

    }*/

    // Spring定时任务线程池（简化）
/*    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(30000);
    }*/

}
