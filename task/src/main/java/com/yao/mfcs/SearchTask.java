package com.yao.mfcs;

import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SearchTask implements Supplier<List<SearchResult>> {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private final Path path;      // 要搜索的目标文件
    private String keyword;       // 搜索关键词
    private String searchId;          // 任务ID（用于日志追踪）
    private final boolean caseSensitive;           // 是否区分大小写

    public SearchTask(Path path, String keyword, String searchId) {
        pathExists(path);
        keywordIsEmpty(keyword);

        this.path = path;
        this.keyword = keyword;
        this.searchId = searchId;
        this.caseSensitive = true;
    }

    public SearchTask(Path path, String keyword, String searchId, boolean caseSensitive) {
        pathExists(path);
        keywordIsEmpty(keyword);

        this.path = path;
        this.keyword = keyword;
        this.searchId = searchId;
        this.caseSensitive = caseSensitive;
        if (!caseSensitive) {
            this.keyword = keyword.toLowerCase();
            BUSINESS_LOGGER.info("不区分大小写,全部转为小写");
        }
    }

    //判断path是否存在
    public void pathExists(Path path) {
        if(Files.notExists(path)){
            throw new InvalidPathException("文件不存在：" + path);
        }
    }

    //判断keyword是否为空
    public void keywordIsEmpty(String keyword) {
        if(keyword==null || keyword.isEmpty()){
            throw new IllegalArgumentException("关键词不能为空");
        }
    }

    @Override
    public List<SearchResult> get() {
        BUSINESS_LOGGER.info("开始搜索任务文件：{}",path);

        try {
            if(!Files.isReadable(path)) {
                throw new IOException(path.toString());
            }

            long size = Files.size(path);

            long maxFileSize = 1024 * 1024 * 10;

            if(size> maxFileSize){
                BUSINESS_LOGGER.info("文件过大，将使用大文件搜索,大小为：{} B",size);

                return searchBigFile();
            }
            else {
                BUSINESS_LOGGER.info("文件大小小于10M，将使用小文件搜索,大小为：{} B",size);

                return searchSmallFile();
            }

        } catch (IOException e) {
            throw new FileReadException("文件不可读",e);
        }
    }

    //小文件读取
    public List<SearchResult> searchSmallFile() {
        List<SearchResult> results = new ArrayList<>();

        //保持final变量，lambda中外部变量必须是final的
        final int[] lineNumber={1};   // 行号

        try(Stream<String> stream=Files.lines(path)){
            stream.forEach(line->{
                processLine(line, results, lineNumber[0]);

                lineNumber[0]++;
            });

            BUSINESS_LOGGER.info("搜索完成,匹配数：{}，搜索总行数：{}",results.size(),lineNumber[0]);
            return results;
        } catch (IOException e) {
            throw new FileReadException("文件读取异常",e);
        }
    }

    //大文件读取
    public List<SearchResult> searchBigFile() {
        List<SearchResult> results = new ArrayList<>();

        try(BufferedReader reader=Files.newBufferedReader(path)){

            int lineNumber=1;
            String line;

            while((line=reader.readLine())!=null)
            {
                processLine(line, results, lineNumber);

                lineNumber++;
            }

            BUSINESS_LOGGER.info("搜索完成，匹配数：{}，判断总行数：{}",results.size(),lineNumber);
            return results;
        } catch (IOException e) {
            throw new FileReadException("文件读取异常",e);
        }
    }

    //处理单行文本
    public void processLine(String line,List<SearchResult> results,int lineNumber) {

        if(line==null||line.isEmpty()) return;

        String previousLine=line;

        if(!caseSensitive) {
            line = line.toLowerCase();
        }

        if(line.contains(keyword)){
            int position=0; // 起始匹配位置
            int index;
            long startTime=System.currentTimeMillis(); // 开始时间

            while((index=line.indexOf(keyword, position))!=-1){
                long endTime=System.currentTimeMillis();

                results.add(new SearchResult(path.toString(), path.getFileName().toString(),
                        lineNumber, previousLine, index,endTime-startTime));

                position=index+keyword.length();
                startTime=endTime;
            }
        }
    }
}
