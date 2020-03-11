package com.caili.utils;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @Author: huey  已经测试成功
 * @Desc: redis锁
 */
@Slf4j
public class RedisLock {


    //从配置类中获取redisson对象
    private static Redisson redisson = RedissonManager.getInstance(1).getRedisson(1);

    private static final String LOCK_TITLE = "redisLock_";

    /**  加锁 */
    public static boolean acquire(String lockName){
        //声明key对象
        String key = LOCK_TITLE + lockName;
        //获取锁对象
        RLock mylock = redisson.getLock(key);
        //加锁，并且设置锁过期时间，防止死锁的产生
        mylock.lock(3, TimeUnit.SECONDS);
         //最多等待 2秒  8秒后自动解锁
        //boolean res = mylock.tryLock(2, 8, TimeUnit.SECONDS);
        return true;
    }


    /** 锁的释放 */
    public static void release(String lockName){
        //必须是和加锁时的同一个key
        String key = LOCK_TITLE + lockName;
        //获取所对象
        RLock mylock = redisson.getLock(key);
        //释放锁（解锁）
        mylock.unlock();
    }
}
