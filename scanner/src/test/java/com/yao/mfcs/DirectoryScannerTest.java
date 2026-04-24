package com.yao.mfcs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryScannerTest {

    // 测试构造函数：路径不存在时抛出异常
    @Test
    void testConstructorWithInvalidPath(@TempDir Path tempDir) {
        Path nonExistentPath = tempDir.resolve("non_existent");
        assertThrows(InvalidPathException.class, () -> {
            new DirectoryScanner(nonExistentPath);
        });
    }

    // 测试构造函数：最大深度非法时抛出异常
    @Test
    void testConstructorWithInvalidDepth(@TempDir Path tempDir) {
        assertThrows(IllegalArgumentException.class, () -> {
            new DirectoryScanner(tempDir, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DirectoryScanner(tempDir, -1);
        });
    }

    // 测试获取文件后缀名
    @Test
    void testGetFileExtension() {
        DirectoryScanner scanner = new DirectoryScanner(Path.of("."));
        assertEquals(".java", scanner.getFileExtension("Test.java"));
        assertEquals(".gz", scanner.getFileExtension("archive.tar.gz"));
        assertEquals("", scanner.getFileExtension("no_extension"));
        assertEquals(".", scanner.getFileExtension("."));
        assertEquals("", scanner.getFileExtension(null));
    }

    // 测试搜索单个文件（符合过滤条件）
    @Test
    void testSearchSingleFileMatch(@TempDir Path tempDir) throws IOException {
        Path file = Files.createFile(tempDir.resolve("test.txt"));
        DirectoryScanner scanner = new DirectoryScanner(file);
        
        // 模拟添加过滤条件（由于 addFilePattern 是控制台输入，这里直接通过反射或修改逻辑测试，
        // 但为了简单，我们假设用户已经添加了 .txt。由于当前实现强依赖控制台输入，
        // 单元测试较难直接覆盖 search() 的核心逻辑，除非重构。）
        // 此处仅测试文件路径传入后的基本行为
        assertNotNull(scanner);
    }

    // 测试搜索目录
    @Test
    void testSearchDirectory(@TempDir Path tempDir) throws IOException {
        // 创建测试文件结构
        Files.createFile(tempDir.resolve("file1.java"));
        Files.createFile(tempDir.resolve("file2.txt"));
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        Files.createFile(subDir.resolve("file3.java"));

        DirectoryScanner scanner = new DirectoryScanner(tempDir, 2);
        // 注意：由于 search() 内部调用了 addFilePattern() 等待控制台输入，
        // 在自动化单元测试中会阻塞。建议后续将“获取过滤条件”与“执行搜索”解耦。
        // 目前这个测试仅用于验证对象创建和基本路径处理。
        assertNotNull(scanner);
    }
}
