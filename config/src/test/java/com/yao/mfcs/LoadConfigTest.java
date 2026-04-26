package com.yao.mfcs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadConfigTest {

    // 测试正常加载配置文件并获取 String 属性
    @Test
    void testGetProperty() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertNotNull(config.getProperty("ThreadPool.cpu.CorePoolSize"));
    }

    // 测试获取不存在的属性时返回 null
    @Test
    void testGetPropertyNotFound() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertNull(config.getProperty("non.existent.key"));
    }

    // 测试获取 String 属性时使用默认值
    @Test
    void testGetPropertyWithDefault() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertEquals("default_value", config.getProperty("non.existent.key", "default_value"));
    }

    // 测试正常获取 int 类型属性
    @Test
    void testGetPropertyInt() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        int depth = config.getPropertyInt("ThreadPool.cpu.CorePoolSize");
        assertEquals(11, depth);
    }

    // 测试获取 int 属性失败时抛出异常（值不是数字）
    @Test
    void testGetPropertyIntInvalid() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertThrows(IllegalArgumentException.class, () -> {
            config.getPropertyInt("non.existent.key"); 
        });
    }

    // 测试获取 int 属性失败时使用默认值
    @Test
    void testGetPropertyIntWithDefault() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertEquals(100, config.getPropertyInt("non.existent.int", 100));
    }

    // 测试获取 boolean 属性：值为非 true/false 字符串时抛出异常
    @Test
    void testGetPropertyBooleanInvalid() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertThrows(IllegalArgumentException.class, () -> {
            config.getPropertyBoolean("ThreadPool.cpu.CorePoolSize");
        });
    }

    // 测试获取 boolean 属性失败时使用默认值
    @Test
    void testGetPropertyBooleanWithDefault() {
        LoadConfig config = new LoadConfig("thread-pool.properties");
        assertTrue(config.getPropertyBoolean("non.existent.bool", true));
    }

    // 测试加载不存在的配置文件时抛出异常
    @Test
    void testLoadNonExistentConfig() {
        assertThrows(RuntimeException.class, () -> {
            new LoadConfig("non_existent.properties");
        });
    }
}
