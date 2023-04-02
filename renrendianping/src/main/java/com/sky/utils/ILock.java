package com.sky.utils;

/**
 * Redis分布式锁
 */
public interface ILock {

    /**
     * 尝试获取锁
     * @param timeoutSec 过期时间
     * @return
     */
    boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
