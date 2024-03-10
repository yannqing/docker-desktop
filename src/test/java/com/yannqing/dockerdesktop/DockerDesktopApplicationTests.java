package com.yannqing.dockerdesktop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import com.yannqing.dockerdesktop.vo.ContainerRunVo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DockerDesktopApplicationTests {
    @Resource
    PermissionsMapper permissionsMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() throws JsonProcessingException {
        List<String> list = new ArrayList<String>();
        list.add("2024-10-01 12:00:00");
        list.add("2024-10-02 12:00:00");
        list.add("2024-10-03 12:00:00");
        list.add("2024-10-04 12:00:00");
        System.out.println(objectMapper.writeValueAsString(list));
        System.out.println(list.toString());
    }

    @Test
    void test1() throws JsonProcessingException {
        List<String> list = new ArrayList<String>();
        list.add("2024-10-01 12:00:00");
        list.add("2024-10-02 12:00:00");
        list.add("2024-10-03 12:00:00");
        list.add("2024-10-04 12:00:00");
        ContainerRunVo vo = new ContainerRunVo();
        vo.setContainerId(1);
        vo.setRunLog(list);
        System.out.println(vo);
    }

}
