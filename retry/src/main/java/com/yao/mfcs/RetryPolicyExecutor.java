package com.yao.mfcs;

import org.slf4j.Logger;
import java.util.concurrent.Callable;

public class RetryPolicyExecutor {

    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();

    private RetryPolicyExecutor() {
    }

    //执行有返回值操作
    public static <T> T execute(RetryPolicy retryPolicy, Callable<T> callable) throws Exception{
        BUSINESS_LOGGER.info("开始读取重试参数");

        int maxRetryCount = retryPolicy.getMaxRetryCount();
        long retryDelayMillis = retryPolicy.getRetryDelayMillis();
        String retryOperationName = retryPolicy.getRetryOperationName();

        int retryCount = 0;    //实际重试次数

        while (retryCount < maxRetryCount) {
            try {

                BUSINESS_LOGGER.info("第{}次执行{}操作", retryCount+1,retryOperationName);
                return callable.call();
            } catch (Exception e) {
                if(retryPolicy.shouldRetry(e,retryCount)){
                    BUSINESS_LOGGER.info("执行失败重试次数{}次", retryCount+1);
                    retryCount++;

                    BUSINESS_LOGGER.info("开始休眠{}ms", retryDelayMillis);
                    Thread.sleep(retryDelayMillis);
                }
                else{
                    BUSINESS_LOGGER.info("第{}次执行{}操作失败，不再重试", retryCount,retryOperationName);
                    throw e;
                }
            }
        }
        throw new Exception("重试次数已满");
    }

    //执行无返回值操作
    public static void execute(RetryPolicy retryPolicy, Runnable runnable) throws Exception{
        execute(retryPolicy, () -> {
            runnable.run();
            return null;
        });
    }
}
