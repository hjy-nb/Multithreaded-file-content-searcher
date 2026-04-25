package com.yao.mfcs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchManagerTest {

    @TempDir
    Path tempDir;

    private SearchManager searchManager;

    // 测试前准备：创建测试文件并初始化 SearchManager
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        Path file1 = Files.createFile(tempDir.resolve("test1.txt"));
        Path file2 = Files.createFile(tempDir.resolve("test2.java"));
        
        // 写入测试内容
        Files.writeString(file1, "Hello World\nThis is a test file");
        Files.writeString(file2, "public class Test {\n    public static void main(String[] args) {}\n}");

        List<Path> files = List.of(file1, file2);
        searchManager = new SearchManager(files);
    }

    // 测试搜索功能：关键词存在
    @Test
    void testSearchWithExistingKeyword() {
        assertDoesNotThrow(() -> {
            searchManager.search("Hello");
        });

        ResultAggregator aggregator = ResultAggregator.getInstance();
        assertFalse(aggregator.getAllResults().isEmpty());
    }

    // 测试搜索功能：关键词不存在
    @Test
    void testSearchWithNonExistingKeyword() {
        assertDoesNotThrow(() -> {
            searchManager.search("NonExistentKeyword123456");
        });

        ResultAggregator aggregator = ResultAggregator.getInstance();
        assertEquals(0, aggregator.getAllResults().size());
    }

    // 测试构造函数：空列表抛出异常
    @Test
    void testConstructorWithEmptyList() {
        assertThrows(SearchResultIsNull.class, () -> {
            new SearchManager(List.of());
        });
    }

    // 测试构造函数：null 列表抛出异常
    @Test
    void testConstructorWithNullList() {
        assertThrows(SearchResultIsNull.class, () -> {
            new SearchManager(null);
        });
    }

    // 测试关闭线程池
    @AfterAll
    static void testClose() {
        assertDoesNotThrow(SearchManager::close);
    }

    // 清理资源
    @AfterEach
    void tearDown() {
        ResultAggregator.getInstance().clear();
    }
}
