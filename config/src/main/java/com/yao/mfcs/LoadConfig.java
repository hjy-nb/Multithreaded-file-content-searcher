package com.yao.mfcs;

import org.slf4j.Logger;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class LoadConfig {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();
    private final Properties properties=new Properties();

    public LoadConfig(String path)
    {
        try(InputStream inputStream=LoadConfig.class.getClassLoader().getResourceAsStream(path)){
            if(inputStream==null){
                ERROR_LOGGER.error("找不到配置文件：{}", path);
                throw new RuntimeException("找不到配置文件："+ path);
            }
            else{
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            ERROR_LOGGER.error("配置文件加载异常",e);
            throw new RuntimeException("配置文件加载异常",e);
        }
    }

    // 获取string配置
    public String getProperty(String key){
        return properties.getProperty(key);
    }

    // 获取string配置带默认值
    public String getProperty(String key,String defaultValue){
        return properties.getProperty(key,defaultValue);
    }

    // 获取int配置
    public int getPropertyInt(String key){
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
    public  int getPropertyInt(String key,int defaultValue){
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

    // 获取boolean配置，字符串保证存在转换为boolean时候不会抛出异常，而是返回false
    public  boolean getPropertyBoolean(String key){
        String value=properties.getProperty(key);

        if(value==null){
            BUSINESS_LOGGER.warn("配置文件参数转换boolean异常");
            return false;
        }

        if(value.equals("true")||value.equals("false")){
            return Boolean.parseBoolean(value);
        }
        else{
            BUSINESS_LOGGER.warn("配置文件参数转换boolean异常");
            throw new IllegalArgumentException("配置文件参数转换boolean异常"+ value);
        }

    }

    // 获取boolean配置带默认值
    public  boolean getPropertyBoolean(String key,boolean defaultValue){
       String value=properties.getProperty(key);

       if(value==null){
           BUSINESS_LOGGER.warn("配置文件参数转换boolean异常,将使用默认值");
           return defaultValue;
       }

       if(value.equals("true")||value.equals("false")){
           return Boolean.parseBoolean(value);
       }
       else{
           BUSINESS_LOGGER.warn("配置文件参数转换boolean异常,将使用默认值");
           return defaultValue;
       }
    }
}
