package com.yao.mfcs;

import org.slf4j.Logger;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LoadConfig {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();
    private static final Properties properties=new Properties();

    static{
        BUSINESS_LOGGER.info("开始加载配置文件");
        try(InputStream inputStream=LoadConfig.class.getClassLoader().getResourceAsStream("config.properties")){
            if(inputStream==null){
                ERROR_LOGGER.error("找不到配置文件：config.properties");
                BUSINESS_LOGGER.warn("找不到配置文件：config.properties,将使用默认配置\n");
                setDefaultConfig();
            }
            else{
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                BUSINESS_LOGGER.info("加载配置文件成功\n");
            }
        } catch (Exception e) {
            ERROR_LOGGER.error("配置文件加载异常",e);
            throw new RuntimeException(e);
        }
    }

    private LoadConfig() {}

    // 设置默认配置
    private static void setDefaultConfig(){
        properties.setProperty("ThreadPool.CorePoolSize","10");
        properties.setProperty("ThreadPool.MaximumPoolSize","20");
        properties.setProperty("ThreadPool.KeepAliveTime","1000");
    }

    // 获取string配置
    public static String getProperty(String key){
        return properties.getProperty(key);
    }

    // 获取string配置带默认值
    public static String getProperty(String key,String defaultValue){
        return properties.getProperty(key,defaultValue);
    }

    // 获取int配置
    public static int getPropertyInt(String key){
        int value;

        try{
            value=Integer.parseInt(properties.getProperty(key));
        }
        catch(Exception e){
            ERROR_LOGGER.error("配置文件参数转换int异常",e);
            throw new IllegalArgumentException(e);
        }

        return value;
    }

    // 获取int配置带默认值
    public static int getPropertyInt(String key,int defaultValue){
        int value;
        try{
            value=Integer.parseInt(properties.getProperty(key));
        }
        catch(Exception e){
            BUSINESS_LOGGER.warn("配置文件参数转换int异常,将使用默认值",e);
            return defaultValue;
        }

        return value;
    }

    // 获取boolean配置
    public static boolean getPropertyBoolean(String key){
        boolean value;
        try{
            value=Boolean.parseBoolean(properties.getProperty(key));
        }
        catch(Exception e){
            ERROR_LOGGER.error("配置文件参数转换boolean异常",e);
            throw new IllegalArgumentException(e);
        }

        return value;
    }

    // 获取boolean配置带默认值
    public static boolean getPropertyBoolean(String key,boolean defaultValue){
        boolean value;
        try{
            value=Boolean.parseBoolean(properties.getProperty(key));
        }
        catch(Exception e){
            BUSINESS_LOGGER.warn("配置文件参数转换boolean异常,将使用默认值",e);
            return defaultValue;
        }

        return value;
    }
}
