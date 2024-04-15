package com.yannqing.dockerdesktop.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 在这里编写初始化逻辑

        Properties properties = new Properties();

        try {
            // 加载 properties 文件
            properties.load(new FileInputStream("config.properties"));

            // 获取并输出所有的键值对
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                System.out.println("Key: " + key + ", Value: " + value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}