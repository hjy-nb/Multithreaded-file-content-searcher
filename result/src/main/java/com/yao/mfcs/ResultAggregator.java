package com.yao.mfcs;

import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

// 结果聚合器 收集搜索结果
public class ResultAggregator {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();

    private final ConcurrentLinkedQueue<SearchResult> allResults = new ConcurrentLinkedQueue<>();     // 所有结果
    private final AtomicLong processingTime = new AtomicLong(0);   // 总处理时间

    private ResultAggregator() {
    }

    static class SingletonHolder {
        private static final ResultAggregator INSTANCE = new ResultAggregator();
    }

    public static ResultAggregator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // 添加搜索结果
    public void addResult(SearchResult results) {
        if(results!=null && allResults.add(results)){
            processingTime.addAndGet(results.getSearchTime());
        }

    }

    //获取统计结果
    public void getStatistics() {

         allResults.forEach(result -> {
             System.out.println("文件路径：" + result.getFilePath() + "  " +
                     "文件名：" + result.getFileName() + "  " +
                     "行号：" + result.getLineNumber() + "  " +
                     "行内容：" + result.getLineContent() + "  " +
                     "匹配位置：" + result.getStartPosition() + "  " +
                     "处理时间：" + result.getSearchTime() + "毫秒");
         });

        System.out.println("已找到匹配数：" + allResults.size() + "  " +
                "总处理时间：" + processingTime.get() + "毫秒");
    }

    //清空结果
    public void clear() {

        allResults.clear();
        LOGGER.info("已清空所有结果");

        processingTime.set(0);
        LOGGER.info("已清空总处理时间");

    }

    //获取所有结果，不可变视图
    public Collection<SearchResult> getAllResults() {
        return Collections.unmodifiableCollection(allResults);
    }

}
