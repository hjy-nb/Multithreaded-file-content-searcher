package com.yao.mfcs;

import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryScanner {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private static final LoadConfig CONFIG = new LoadConfig("filename-extension.properties");
    private static final Map<Integer,String> FILE_EXTENSIONS= new HashMap<>();  // 待选择文件扩展名

    private final Path directoryPath;                       // 要搜索的目录路径
    private final List<String> filePatterns=new ArrayList<>();                  // 文件扩展名过滤（如.txt, .java）--要在里面
    private final int maxDepth;                       // 最大搜索深度

    static {
        BUSINESS_LOGGER.info("初始化待选则文件扩展名");

        FILE_EXTENSIONS.put(1,CONFIG.getProperty("code"));
        FILE_EXTENSIONS.put(2,CONFIG.getProperty("web"));
        FILE_EXTENSIONS.put(3,CONFIG.getProperty("config"));
        FILE_EXTENSIONS.put(4,CONFIG.getProperty("document"));
        FILE_EXTENSIONS.put(5,CONFIG.getProperty("data"));
        FILE_EXTENSIONS.put(6,CONFIG.getProperty("archive"));

        BUSINESS_LOGGER.info("文件扩展名初始化完成");
    }

    public DirectoryScanner(Path directoryPath,int maxDepth) {
        pathExists(directoryPath);
        maxDepthIsValid(maxDepth);

        this.directoryPath = directoryPath;
        this.maxDepth = maxDepth;
    }

    public DirectoryScanner(Path directoryPath) {
        pathExists(directoryPath);

        this.directoryPath = directoryPath;
        this.maxDepth = Integer.MAX_VALUE;
    }

    //判断路径是否存在
    public void pathExists(Path path) {
        if(!Files.exists(path)){
            throw new InvalidPathException("目录不存在：" + path);
        }
    }

    //判断最大深度是否合法
    public void maxDepthIsValid(int maxDepth) {
        if(maxDepth<=0){
            ERROR_LOGGER.error("最大深度必须大于0");
            throw new IllegalArgumentException("最大深度必须大于0");
        }
    }

    //判断文件扩展名是否合法
    public void filePatternIsValid(String pattern) {
        if (pattern == null || !pattern.startsWith(".")) {
            throw new IllegalArgumentException("无效格式: " + pattern);
        }
    }

    //展示待选择文件扩展名
    public void showFileExtensions() {
        System.out.println("请选择要搜索的文件类型（输入编号）：");
        System.out.println("1: 源代码文件 (.java, .py, .js...)");
        System.out.println("2: 网页开发文件 (.html, .css, .vue...)");
        System.out.println("3: 配置文件 (.xml, .json, .yaml...)");
        System.out.println("4: 文档文件 (.txt, .md, .docx...)");
        System.out.println("5: 数据文件 (.csv, .xlsx, .sql...)");
        System.out.println("6: 压缩归档文件 (.zip, .tar, .rar...)");
        System.out.println("请输入您的选择：(一行输入逗号隔开)");
    }


    //添加文件扩展名过滤（程序化调用）
    public void addFilePattern(String pattern) {
        filePatternIsValid(pattern);
        if (!filePatterns.contains(pattern)) {
            filePatterns.add(pattern);
        }
    }

    //添加文件扩展名过滤,不直接添加到search方法中，可实现异步，控制台输入也是io操作
    public void addFilePattern() {
        Scanner scanner = new Scanner(System.in);
        showFileExtensions();
        String selection = scanner.nextLine();

        // 处理输入
        Arrays.stream(selection.split(","))
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .filter(s -> s.matches("\\d+"))
                .map(Integer::parseInt)
                .filter(i -> i >= 1 && i <= 6)
                .map(FILE_EXTENSIONS::get)   // 获取对应的扩展名
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .distinct()
                .forEach(this::addFilePattern); // 复用上面的方法
    }

    //获取文件后缀名称，最后一个.之后的字符串
    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf(".");

        return lastDotIndex != -1 ? fileName.substring(lastDotIndex) : "";
    }

    //搜索目录
    public List<Path> search() {

        if(Files.isRegularFile(directoryPath)) {
            BUSINESS_LOGGER.info("directoryPath不是目录");

            String extension = getFileExtension(directoryPath.getFileName().toString());
            if (filePatterns.contains(extension)) {
                BUSINESS_LOGGER.info("directoryPath是文件，且扩展名符合要求");

                return List.of(directoryPath);
            }
            else {
                BUSINESS_LOGGER.info("directoryPath是文件，但扩展名不符合要求");

                return List.of();
            }
        }

        int batchSize = 1000;
        List<Path> result = new ArrayList<>();   //总结果
        List<Path> batchDeal = new ArrayList<>(batchSize);  //批量处理

        try(Stream<Path> stream = Files.walk(directoryPath, maxDepth)){

             stream.parallel()
                     .filter(Files::isRegularFile)
                    .filter(path -> filePatterns.contains(getFileExtension(path.getFileName().toString())))
                    .forEach(path -> {
                        synchronized (batchDeal) {
                            batchDeal.add(path);
                            if (batchDeal.size() == batchSize) {
                                result.addAll(batchDeal);
                                batchDeal.clear();
                            }
                        }
                    });

             if (!batchDeal.isEmpty()) {
                 result.addAll(batchDeal);
             }

             return result;

        } catch (Exception e) {
            throw new FileReadException("搜索目录异常,权限不足",e);
        }
    }

}
