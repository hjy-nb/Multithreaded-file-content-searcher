package com.yao.mfcs;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

// 结果聚合器 收集搜索结果
public class ResultAggregator {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();

    private final ConcurrentHashMap<String, List<SearchResult>> allResults = new ConcurrentHashMap<>();     // 所有结果
    private final LongAdder totalFilesProcessed = new LongAdder();       // 已成功处理的文件数
    private final AtomicLong totalMatchesFound = new AtomicLong(0);          // 找到的匹配数
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
    public boolean addResult(String id, List<SearchResult> results) {

        if(results.isEmpty() || allResults.putIfAbsent(id, results)!=null) return false;

        totalFilesProcessed.increment();

        long count=results.size();
        totalMatchesFound.addAndGet(count);

        long sumTime=results.stream().mapToLong(SearchResult::getSearchTime).sum();
        processingTime.addAndGet(sumTime);

        return true;
    }

    //获取统计结果
    public String getStatistics() {

        return "已成功处理文件数：" + totalFilesProcessed.sum() + "  " +
                "已找到匹配数：" + totalMatchesFound.get() + "  " +
                "总处理时间：" + processingTime.get() + "毫秒";
    }

    //清空结果
    public void clear() {

        allResults.clear();
        LOGGER.info("已清空所有结果");

        totalFilesProcessed.reset();
        LOGGER.info("已清空已处理文件数");

        totalMatchesFound.set(0);
        LOGGER.info("已清空已找到匹配数");

        processingTime.set(0);
        LOGGER.info("已清空总处理时间");

    }

    //获取所有结果，不可见视图
    public Map<String, List<SearchResult>> getAllResults() {
        return Collections.unmodifiableMap(allResults);
    }

}
