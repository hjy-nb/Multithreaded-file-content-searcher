package com.yao.mfcs;

import java.util.Set;

public class InvalidPathRetryPolicy implements RetryPolicy{
    private static final LoadConfig loadConfig = new LoadConfig("retry-policy.properties");

    private final int maxRetryCount;             // 最大重试次数
    private final long retryDelayMillis;              // 重试延迟时间
    private final String operation;                    // 操作名
    private final Set<Class<? extends Exception> > retryableExceptions;

    public InvalidPathRetryPolicy(String operation) {
        this.operation = operation;
        this.maxRetryCount = loadConfig.getPropertyInt("InvalidPathTryPolicy.retryCount");
        this.retryDelayMillis = loadConfig.getPropertyInt("InvalidPathTryPolicy.retryDelay");
        this.retryableExceptions = Set.of(InvalidPathException.class);
    }

    @Override
    public boolean shouldRetry(Exception e, int retryCount) {
        if(retryCount<0 || retryCount >= maxRetryCount) return false;

        return retryableExceptions.contains(e.getClass());
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public long getRetryDelayMillis() {
        return retryDelayMillis;
    }

    @Override
    public String getRetryOperationName() {
        return operation;
    }

}
