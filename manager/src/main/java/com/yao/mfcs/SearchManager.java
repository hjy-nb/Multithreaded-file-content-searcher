package com.yao.mfcs;


import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

//具体实现搜索逻辑
public class SearchManager {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    public static final ExecutorService CPU_THREAD_POOL = ThreadPool.createCpuIntensiveThreadPool();
    public static final ExecutorService IO_THREAD_POOL = ThreadPool.createIoIntensiveThreadPool();

    private final ResultAggregator resultAggregator = ResultAggregator.getInstance();   //结果聚合器 存结果

    private final List<Path> searchResults;                                // 目录搜索结果
    private final String taskIdPrev="taskId_";
    private final AtomicLong taskId;

    public SearchManager(List<Path> searchResults) {
        if(searchResults==null){
            throw new SearchResultIsNull("搜索结果异常");
        }

        this.searchResults = searchResults;
        taskId = new AtomicLong(1);
    }

    //搜索主入口
    public void search(String keyword) {

        //创建多个任务,但是异步执行要等待结果完成
        List<CompletableFuture<List<SearchResult>>> futures = searchResults.stream().map(path ->
              CompletableFuture.supplyAsync(() -> {
                String totalTaskId = taskIdPrev + taskId.getAndIncrement();
                BUSINESS_LOGGER.info("开始搜索任务：{}",totalTaskId);

                SearchTask searchTask = new SearchTask(path, keyword, totalTaskId);
                return searchTask.get();
            }, IO_THREAD_POOL)).toList();

        //等待所有任务完成
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        //聚合结果
        futures.stream().map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .forEach(resultAggregator::addResult);

    }

    //关闭线程池
    public static void close() {
        CPU_THREAD_POOL.shutdown();
        IO_THREAD_POOL.shutdown();
    }

    public ResultAggregator getResultAggregator() {
        return resultAggregator;
    }
}
