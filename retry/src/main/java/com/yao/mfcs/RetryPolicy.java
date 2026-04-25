package com.yao.mfcs;

public interface RetryPolicy {
    boolean shouldRetry(Exception e,int retryCount); // 判断是否需要重试
    int getMaxRetryCount();                 // 获取最大重试次数
    long getRetryDelayMillis();            // 获取重试延迟时间
    String getRetryOperationName();          // 获取重试操作名称
}
