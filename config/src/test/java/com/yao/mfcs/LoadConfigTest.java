package com.yao.mfcs;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoadConfigTest {
    /**
     * 测试获取int类型配置项
     */
    @Test
    public void testGetPropertyInt() {
        int value = LoadConfig.getPropertyInt("ThreadPool.CorePoolSize");
        assertEquals(16, value);
    }

    /**
     * 测试获取int类型配置项（带默认值）
     */
    @Test
    public void testGetPropertyIntWithDefault() {
        int value = LoadConfig.getPropertyInt("NonExistentKey", 100);
        assertEquals(100, value);
    }

}
