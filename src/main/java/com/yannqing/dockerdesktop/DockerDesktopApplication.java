package com.yannqing.dockerdesktop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yannqing.dockerdesktop.mapper")
public class DockerDesktopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerDesktopApplication.class, args);
    }

}
