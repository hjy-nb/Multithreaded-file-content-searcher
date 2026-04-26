package com.yao.mfcs;

import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private SearchManager searchManager; // 搜索管理器
    private final Scanner scanner= new Scanner(System.in);

    //初始化搜索管理器
    public void initSearchManager(String directoryPath) {
        searchManager = new SearchManager(Path.of(directoryPath));
    }

    //初始化搜索管理器,带搜索深度
    public void initDirectoryScanner(String directoryPath, int maxDepth) {
        searchManager = new SearchManager(Path.of(directoryPath), maxDepth);
    }


    //搜索操作提示
    public void printSearchOperationTips() {
        System.out.println("输入1：执行搜索 输入0：退出");
    }

    public static void main(String[] args) {
        BUSINESS_LOGGER.info("创建Main对象");
        Main main = new Main();

        try {
            while(true){
                main.printSearchOperationTips();
                int operation = main.scanner.nextInt();
                main.scanner.nextLine();
                BUSINESS_LOGGER.info("用户输入操作：{}",operation);

                if(operation==0){
                    BUSINESS_LOGGER.info("退出程序");
                    break;
                } else if(operation!=1){
                    BUSINESS_LOGGER.info("输入错误，请重新输入");
                    continue;
                }

                BUSINESS_LOGGER.info("开始执行一次搜索");
               if(main.search(main)==-1) {
                   BUSINESS_LOGGER.info("权限不足，请重试");
                   continue;
               }

                BUSINESS_LOGGER.info("搜索完成,打印搜索结果");
                main.searchManager.getResultAggregator().getStatistics();

                BUSINESS_LOGGER.info("打印完成，清空单例结果聚集器");
                main.searchManager.getResultAggregator().clear();

            }
        } catch (InvalidPathException e) {
            ERROR_LOGGER.error("目录或文件不存在", e);
        } catch (IllegalArgumentException e) {
            ERROR_LOGGER.error("关键词为空", e);
        } catch (FileReadException e) {
            ERROR_LOGGER.error("文件读取异常", e);
        } catch (SearchResultIsNull e) {
            ERROR_LOGGER.error("获取目录扫描器结果为空", e);
        } catch (Exception e) {
            ERROR_LOGGER.error(e.getMessage(), e);
        } finally {
            BUSINESS_LOGGER.info("关闭线程池");
            SearchManager.close();
        }
    }

    //单次搜索
    public int search(Main main) throws Exception{

        RetryPolicyExecutor.execute(new InvalidPathRetryPolicy("初始化搜索管理器"), () -> {
            System.out.println("请输入要搜索的目录：");
            String directoryPath = main.scanner.nextLine();
            BUSINESS_LOGGER.info("输入搜素目录 directoryPath:{}",directoryPath);

            BUSINESS_LOGGER.info("开始初始化搜索管理器");
            main.initSearchManager(directoryPath);
        });

        BUSINESS_LOGGER.info("开始搜索");

        try{
            CompletableFuture.runAsync(searchManager::addFilePattern, SearchManager.IO_THREAD_POOL)
                    .thenRunAsync(()->{
                        System.out.println("请输入要搜索的关键字：");
                        String keyword = main.scanner.nextLine();
                        BUSINESS_LOGGER.info("输入搜素关键字 keyword:{}",keyword);

                        main.searchManager.search(keyword);
                    }, SearchManager.IO_THREAD_POOL).join();
        } catch (FileReadException e) {
            ERROR_LOGGER.error("权限不足,请重试", e);
            return -1;
        }

        return 0;
    }
}
