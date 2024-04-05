package com.yannqing.dockerdesktop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import com.yannqing.dockerdesktop.vo.container.ContainerStartVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class DockerDesktopApplicationTests {
    @Resource
    PermissionsMapper permissionsMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void contextLoads() throws JsonProcessingException {
        String runLog = "{\n" +
                "    \"run_log\":[\n" +
                "    {\"time\":\"2021-8-23 11:23:23\",\"action\":\"容器停止\"},\n" +
                "    {\"time\":\"2021-8-23 11:23:23\",\"action\":\"容器停止\"},\n" +
                "    {\"time\":\"2021-9-26 11:23:23\",\"action\":\"用户user087恢复容器运行\"},\n" +
                "    {\"time\":\"2021-9-26 11:23:23\",\"action\":\"管理员admin强行停止容器\"}\n" +
                "    ]\n" +
                "}";
        List<RunLogVo> runLogList = JSON.parseArray(JSON.parseObject(runLog).getString("run_log"), RunLogVo.class);
        System.out.println(runLogList);
    }

    @Test
    void test1() throws JsonProcessingException {
        List<String> list = new ArrayList<String>();
        list.add("2024-10-01 12:00:00");
        list.add("2024-10-02 12:00:00");
        list.add("2024-10-03 12:00:00");
        list.add("2024-10-04 12:00:00");
//        ContainerStartVo vo = new ContainerStartVo();
//        vo.setContainerId(1);
//        vo.setRunLog(list);
//        System.out.println(vo);
    }
    @Test
    void test2(){
        String s = UUID.randomUUID().toString();
        System.out.println(s);
    }
}
