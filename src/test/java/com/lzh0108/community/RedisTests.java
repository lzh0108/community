package com.lzh0108.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {

        String redisKey = "test:count";

        // 存数据
        redisTemplate.opsForValue().set(redisKey, 1);

        // 取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));


        // 将value值增加或者减少
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }


    @Test
    public void testHashs() {

        String rediskey = "test:user";

        redisTemplate.opsForHash().put(rediskey, "id", 1);
        redisTemplate.opsForHash().put(rediskey, "username", "zhangsan");

        System.out.println(redisTemplate.opsForHash().get(rediskey, "id"));
        System.out.println(redisTemplate.opsForHash().get(rediskey, "username"));
    }

    @Test
    public void testLists() {

        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets() {

        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "赵云", "张飞", "诸葛亮");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }


    @Test
    public void testSortedSets() {

        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }

    @Test
    public void testKeys() {

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);

    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {

        String redisKey = "test:count";

        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);

        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();

        System.out.println(operations.get());

    }

    // 编程式事务
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String redisKey = "test:tx";

                // 启用事务
                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey));

                return operations.exec();

            }
        });

        System.out.println(obj);

    }

    // 统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";
        for (int i = 1; i < 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }

        for (int i = 1; i < 100000; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));

    }

    // 将三组数据合并，再统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion() {
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }

        String redisKey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);

    }

    // 统计一组数据的布尔值
    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }

    // 统计3组数据的布尔值，并对这3组数据做OR运算
    @Test
    public void testBitMapOperation() {
        String rediskey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(rediskey2, 0, true);
        redisTemplate.opsForValue().setBit(rediskey2, 1, true);
        redisTemplate.opsForValue().setBit(rediskey2, 2, true);

        String rediskey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(rediskey3, 2, true);
        redisTemplate.opsForValue().setBit(rediskey3, 3, true);
        redisTemplate.opsForValue().setBit(rediskey3, 4, true);


        String rediskey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(rediskey4, 4, true);
        redisTemplate.opsForValue().setBit(rediskey4, 5, true);
        redisTemplate.opsForValue().setBit(rediskey4, 6, true);

        String redisKey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), rediskey2.getBytes(), rediskey3.getBytes(), rediskey4.getBytes());
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,7));

    }


}
