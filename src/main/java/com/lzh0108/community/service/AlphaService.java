package com.lzh0108.community.service;

import com.lzh0108.community.dao.AlphaDao;
import com.lzh0108.community.dao.DiscussPostMapper;
import com.lzh0108.community.dao.UserMapper;
import com.lzh0108.community.entity.DiscussPost;
import com.lzh0108.community.entity.User;
import com.lzh0108.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
@Scope("prototype") // 可以创建多个实例，不是单例模式
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

//    public AlphaService() {
//        System.out.println("实例化AlphaService");
//    }

//    @PostConstruct
//    public void init() {
//        System.out.println("初始化AlphaService");
//    }/

//    @PreDestroy
//    public void destroy() {
//        System.out.println("销毁AlphaService");
//    }

    public String find() {
        return alphaDao.select();
    }


    // 测试Spring两种管理事务的方法

    // 测试一个完整的事务，新增用户并且新增新人报道帖子
    //
    // 事务的传播机制
    // REQUIRED       ：若当前存在事务，则加入该事务，若不存在事务，则新建一个事务。
    // REQUIRES_NEW   ：若当前没有事务，则新建一个事务。若当前存在事务，则新建一个事务，新老事务相互独立。外部事务抛出异常回滚不会影响内部事务的正常提交。
    // NESTED         ：如果当前存在事务，则嵌套在当前事务中执行。如果当前没有事务，则新建一个事务。

    // 声明式事务
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }

    // 编程式事务
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {

                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);
                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

    // 让该方法在多线程环境下，被异步地调用
//    @Async
//    public void execute1() {
//        logger.debug("execute1");
//    }

    // initialDelay：多长时间之后执行第一次
    // fixedRate：多长时间间隔一次
//    @Scheduled(initialDelay = 10000,fixedRate = 1000)
//    public void execute2(){
//        logger.debug("execute2");
//    }

}
