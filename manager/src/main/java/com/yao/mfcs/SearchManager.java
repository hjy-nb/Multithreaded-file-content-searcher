package com.yao.mfcs;


import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

//具体实现搜索逻辑
public class SearchManager {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private static final ExecutorService CPU_THREAD_POOL = ThreadPool.createCpuIntensiveThreadPool();
    private static final ExecutorService IO_THREAD_POOL = ThreadPool.createIoIntensiveThreadPool();

    private final ResultAggregator resultAggregator = ResultAggregator.getInstance();   //结果聚合器 存结果

    private final List<Path> searchResults;                                // 目录搜索结果
    private final String taskIdPrev="taskId_";
    private final AtomicLong taskId;

    public SearchManager(List<Path> searchResults) {
        if(searchResults==null || searchResults.isEmpty()){
            ERROR_LOGGER.error("搜索结果为空");
            throw new SearchResultIsNull("搜索结果为空");
        }

        this.searchResults = searchResults;
        taskId = new AtomicLong();
    }

    //搜索主入口
    public void search(String keyword) {

        
    }

    //关闭线程池
    public void close() {
        CPU_THREAD_POOL.shutdown();
        IO_THREAD_POOL.shutdown();
    }
}
