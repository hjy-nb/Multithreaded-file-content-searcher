package com.yao.mfcs;

import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


// 线程池的工厂类，只提供创建线程池的工厂方法，不用其余成员变量
public class ThreadPool {

    private  static final LoadConfig CONFIG = new LoadConfig("thread-pool.properties");

    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private ThreadPool() {
    }

    // 创建cpu密集型线程池
    public static ExecutorService createCpuIntensiveThreadPool() {
        BUSINESS_LOGGER.info("开始加载配置文件thread-pool.properties中配置 ");

        int CorePoolSize = CONFIG.getPropertyInt("ThreadPool.cpu.CorePoolSize",11);
        int MaximumPoolSize = CONFIG.getPropertyInt("ThreadPool.cpu.MaximumPoolSize",11);
        int KeepAliveTime = CONFIG.getPropertyInt("ThreadPool.cpu.KeepAliveTime",60);
        int QueueCapacity = CONFIG.getPropertyInt("ThreadPool.cpu.QueueCapacity",2048);

        BUSINESS_LOGGER.info("加载完成，开始创建线程池");

        return new ThreadPoolExecutor(CorePoolSize, MaximumPoolSize, KeepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QueueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 创建io密集型线程池
    public static ExecutorService createIoIntensiveThreadPool() {
        BUSINESS_LOGGER.info("开始加载配置文件thread-pool.properties中配置");

        int CorePoolSize = CONFIG.getPropertyInt("ThreadPool.io.CorePoolSize",16);
        int MaximumPoolSize = CONFIG.getPropertyInt("ThreadPool.io.MaximumPoolSize",32);
        int KeepAliveTime = CONFIG.getPropertyInt("ThreadPool.io.KeepAliveTime",60);
        int QueueCapacity = CONFIG.getPropertyInt("ThreadPool.io.QueueCapacity",2048);

        BUSINESS_LOGGER.info("加载完成，开始创建线程池");

        return new ThreadPoolExecutor(CorePoolSize, MaximumPoolSize, KeepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QueueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
