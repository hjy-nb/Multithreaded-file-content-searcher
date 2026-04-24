package com.yao.mfcs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTaskTest {

    @TempDir
    Path tempDir; // 临时目录，测试结束后自动清理

    @Test
    void testConstructorWithInvalidPath() {
        // 测试：文件路径不存在时抛出 InvalidPathException
        assertThrows(InvalidPathException.class, () -> {
            new SearchTask(Path.of("non_existent_file.txt"), "keyword", "test-1");
        });
    }

    @Test
    void testConstructorWithEmptyKeyword() throws IOException {
        // 测试：关键词为空时抛出 IllegalArgumentException
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SearchTask(file, "", "test-2");
        });
    }

    @Test
    void testConstructorWithNullKeyword() throws IOException {
        // 测试：关键词为 null 时抛出 IllegalArgumentException
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "content");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new SearchTask(file, null, "test-3");
        });
    }

    @Test
    void testSearchSmallFileCaseSensitive() throws IOException {
        // 测试：小文件区分大小写搜索
        Path file = tempDir.resolve("small.txt");
        Files.writeString(file, "Hello World\nhello world\nHello");
        
        SearchTask task = new SearchTask(file, "Hello", "test-4", true);
        List<SearchResult> results = task.get();
        
        assertEquals(2, results.size()); // 只匹配到大写 Hello
        assertEquals(1, results.get(0).getLineNumber());
        assertEquals(3, results.get(1).getLineNumber());
    }

    @Test
    void testSearchSmallFileCaseInsensitive() throws IOException {
        // 测试：小文件不区分大小写搜索
        Path file = tempDir.resolve("small.txt");
        Files.writeString(file, "Hello World\nhello world");
        
        SearchTask task = new SearchTask(file, "hello", "test-5", false);
        List<SearchResult> results = task.get();
        
        assertEquals(2, results.size()); // 匹配到 Hello 和 hello
        assertTrue(results.get(0).getLineContent().contains("Hello")); // 保留原始内容
    }

    @Test
    void testMultipleMatchesInOneLine() throws IOException {
        // 测试：一行中有多个匹配
        Path file = tempDir.resolve("multi.txt");
        Files.writeString(file, "abc abc abc");
        
        SearchTask task = new SearchTask(file, "abc", "test-6", true);
        List<SearchResult> results = task.get();
        
        assertEquals(3, results.size());
        assertEquals(0, results.get(0).getStartPosition());
        assertEquals(4, results.get(1).getStartPosition());
        assertEquals(8, results.get(2).getStartPosition());
    }

    @Test
    void testNoMatch() throws IOException {
        // 测试：无匹配时返回空列表
        Path file = tempDir.resolve("nomatch.txt");
        Files.writeString(file, "some content");
        
        SearchTask task = new SearchTask(file, "xyz", "test-7", true);
        List<SearchResult> results = task.get();
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchBigFile() throws IOException {
        // 测试：大文件搜索（>10MB）
        Path file = tempDir.resolve("big.txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200000; i++) { // 约 10MB+
            sb.append("Line ").append(i).append(" with keyword here\n");
        }
        Files.writeString(file, sb.toString());
        
        SearchTask task = new SearchTask(file, "keyword", "test-8", true);
        List<SearchResult> results = task.get();
        
        assertEquals(200000, results.size());
    }
}
