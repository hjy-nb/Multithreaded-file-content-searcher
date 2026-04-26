package com.yao.mfcs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        searchManager = new SearchManager(tempDir);
        searchManager.addFilePattern(".txt");
        searchManager.addFilePattern(".java");
    }

    // 测试搜索功能：关键词存在
    @Test
    void testSearchWithExistingKeyword() {
        assertDoesNotThrow(() -> {
            searchManager.search("Hello");
        });

        ResultAggregator aggregator = searchManager.getResultAggregator();
        assertFalse(aggregator.getAllResults().isEmpty());
    }

    // 测试搜索功能：关键词不存在
    @Test
    void testSearchWithNonExistingKeyword() {
        assertDoesNotThrow(() -> {
            searchManager.search("NonExistentKeyword123456");
        });

        ResultAggregator aggregator = searchManager.getResultAggregator();
        assertEquals(0, aggregator.getAllResults().size());
    }

    // 测试构造函数：目录不存在抛出异常
    @Test
    void testConstructorWithInvalidPath() {
        assertThrows(InvalidPathException.class, () -> {
            new SearchManager(Path.of("non_existent_directory"));
        });
    }

    // 测试构造函数：最大深度非法抛出异常
    @Test
    void testConstructorWithInvalidMaxDepth() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SearchManager(tempDir, 0);
        });
    }

    // 测试添加文件扩展名过滤
    @Test
    void testAddFilePattern() {
        assertDoesNotThrow(() -> {
            searchManager.addFilePattern(".py");
        });
    }

    // 测试添加非法文件扩展名
    @Test
    void testAddInvalidFilePattern() {
        assertThrows(IllegalArgumentException.class, () -> {
            searchManager.addFilePattern("invalid");
        });
    }

    // 测试获取文件扩展名
    @Test
    void testGetFileExtension() {
        assertEquals(".txt", searchManager.getFileExtension("test.txt"));
        assertEquals("", searchManager.getFileExtension("test"));
        assertEquals(".gz", searchManager.getFileExtension("test.tar.gz"));
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
