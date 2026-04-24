package com.yao.mfcs;

import org.slf4j.Logger;

//仅仅封装搜索结果
public class SearchResult {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();

    private final String filePath;      // 文件路径
    private final String fileName;       // 文件名
    private final int lineNumber;        // 匹配行号
    private final String lineContent;        // 匹配行的内容
    private final int startPosition;         // 关键词在行中的起始位置
    private final long searchTime;        // 搜索耗时（毫秒）

    public SearchResult(String filePath, String fileName, int lineNumber, String lineContent, int startPosition, long searchTime) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
        this.startPosition = startPosition;
        this.searchTime = searchTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public long getSearchTime() {
        return searchTime;
    }
}
