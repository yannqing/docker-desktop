package com.yannqing.dockerdesktop;

import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DockerDesktopApplicationTests {
    @Resource
    PermissionsMapper permissionsMapper;

    @Test
    void contextLoads() {
        List<String> permissions = permissionsMapper.queryPermissionsByUserId(1);
        System.out.println(permissions);
    }

}
