package com.yannqing.dockerdesktop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.mapper.PermissionsMapper;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.service.impl.ContainerServiceImpl;
import com.yannqing.dockerdesktop.utils.RedisCache;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import com.yannqing.dockerdesktop.vo.container.StartLogVo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.alibaba.fastjson.JSON;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DockerDesktopApplicationTests {
    @Resource
    PermissionsMapper permissionsMapper;

    @Autowired
    ObjectMapper objectMapper;
    @Resource
    private RedisCache redisCache;

    @Resource
    private ContainerService containerService;

    @Test
    void testJson() throws JsonProcessingException {
        String runLog = "[\n" +
                "    {\n" +
                "        \"140d22bd-489b-4580-b9ab-14b6fd5229ec\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"e65a28c7-1785-4da3-853b-eeec31fc1e0f\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"53d9d77a-8ded-4ab0-9a4e-c309fe96d7cc\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"30be0c8e-b564-48b1-988d-7812443169d8\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    }\n" +
                "]";
        JSONArray runLogList = JSON.parseArray(runLog);
        for (Object obj : runLogList) {
            if (obj instanceof JSONObject jsonObject) {
                for (String key : jsonObject.keySet()) {
                    JSONObject innerObject = jsonObject.getJSONObject(key);
                    StartLogVo startLogVo = innerObject.toJavaObject(StartLogVo.class);

                    System.out.println(startLogVo);
                }
            }
        }
    }

    @Test
    void setRedis() throws JsonProcessingException {
        String runLog = "[\n" +
                "    {\n" +
                "        \"140d22bd-489b-4580-b9ab-14b6fd5229ec\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"e65a28c7-1785-4da3-853b-eeec31fc1e0f\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"53d9d77a-8ded-4ab0-9a4e-c309fe96d7cc\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"30be0c8e-b564-48b1-988d-7812443169d8\":{\n" +
                "            \"startTime\":\"2222-22-22 22:22:22\", \"endTime\":\"2222-22-22 22:22:22\"\n" +
                "        }\n" +
                "    }\n" +
                "]";
//        Map<String, StartLogVo> map = new LinkedHashMap<>();
//        map.put("xxx", new StartLogVo("", ""));
        redisCache.setCacheObject("container:start:logs", runLog, 60*60*24*30, TimeUnit.SECONDS);
//        Map.Entry<String, StartLogVo> entry;

    }

    @Test
    void getRedis() throws JsonProcessingException {
        String startLogs = redisCache.getCacheObject("container:start:logs");
        //1. 定义返回值
        Map<String, StartLogVo> log = new LinkedHashMap<>();
        //2. 解析json，并返回数据
        JSONArray runLogList = JSON.parseArray(startLogs);
        for (Object obj : runLogList) {
            if (obj instanceof JSONObject jsonObject) {
                for (String key : jsonObject.keySet()) {
                    JSONObject innerObject = jsonObject.getJSONObject(key);
                    StartLogVo startLogVo = innerObject.toJavaObject(StartLogVo.class);
                    log.put(key, startLogVo);
                }
            }
        }

        System.out.println(log);
        //添加数据  ac9f4f97-90cd-487b-a275-fdb202400fd3
//        String key = "ac9f4f97-90cd-487b-a275-fdb202400fd3";
//        StartLogVo startLogVo = new StartLogVo("2222-22-22 22:22:22", "2222-22-22 22:22:22");
//
//        addStartLogsMessage(startLogs, key, startLogVo);



    }
    @Test
    void getUUID(){
        String s = UUID.randomUUID().toString();
        System.out.println(s);
    }

    @Test
    void dockerTest() {
//        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
//        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
//                .dockerHost(config.getDockerHost())
//                .build();
//        DockerClient dockerClient =  DockerClientImpl.getInstance(config, httpClient);
//
//        // docker info
//        Info info = dockerClient.infoCmd().exec();
//        System.out.print(info);

        ContainerInfoVo containerInfo = containerService.getConInfo("b0407a21078c");


    }


    @Test
    void testContainer() {
        ContainerServiceImpl containerService1 = new ContainerServiceImpl();
        Container container = containerService.getById("0bd41a70-02c7-4498-833d-6025fb42f8e5");
        JSON.parseObject(container.getStart_log()).getString("start_log");
    }

    public void addStartLogsMessage(String startLogs, String key, StartLogVo startLogVo) throws JsonProcessingException {
        JSONObject startLogJson = new JSONObject();
        startLogJson.put("startTime", startLogVo.getStart_time());
        startLogJson.put("endTime", startLogVo.getEnd_time());

        JSONArray runLogList = JSON.parseArray(startLogs);
        JSONObject startLog = new JSONObject();
        startLog.put(key, startLogJson);
        runLogList.add(startLog);
        String runLogsString = objectMapper.writeValueAsString(runLogList);
        redisCache.setCacheObject("container:start:logs", runLogsString);
    }
}
