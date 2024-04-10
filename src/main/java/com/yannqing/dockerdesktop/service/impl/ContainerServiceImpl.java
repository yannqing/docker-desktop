package com.yannqing.dockerdesktop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.InspectContainerCmdImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.ContainerMapper;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.utils.RedisCache;
import com.yannqing.dockerdesktop.vo.container.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @author 67121
* @description 针对表【yan_container】的数据库操作Service实现
* @createDate 2024-03-17 01:15:32
*/
@Service
@Slf4j
public class ContainerServiceImpl extends ServiceImpl<ContainerMapper, Container>
    implements ContainerService {


    @Resource
    private ContainerMapper containerMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ObjectMapper objectMapper;

    private final DockerClient dockerClient;

    @Autowired
    public ContainerServiceImpl(){
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();
        this.dockerClient =  DockerClientImpl.getInstance(config, httpClient);
    }

    public ContainerInfoVo getConInfo(String containerId) {
        InspectContainerCmdImpl inspectContainerCmd = (InspectContainerCmdImpl) dockerClient.inspectContainerCmd(containerId);

        // 执行命令并获取容器信息
        InspectContainerResponse inspectContainerResponse = inspectContainerCmd.exec();

        // 输出容器信息
        System.out.println("Container ID: " + inspectContainerResponse.getId());
        System.out.println("Container State: " + inspectContainerResponse.getState());
        System.out.println("Container Name: " + inspectContainerResponse.getName());
        System.out.println("Container Created: " + inspectContainerResponse.getCreated());
        System.out.println("Container ImageId: " + inspectContainerResponse.getImageId());
        return null;
    }

    /**
     * 获取容器详细信息（TODO：管理员限制）
     * @param containerId
     * @return
     */
    @Override
    public ContainerInfoVo getContainerInfo(String containerId) {
        Container container = containerMapper.selectById(containerId);
        User createUser = userMapper.selectById(container.getUser_id());
        List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

        long runTime = 0;

        for (int i = 0; i < start_log.size(); i++) {
            String startTime = start_log.get(i).getStartTime();
            String endTime = start_log.get(i).getEndTime();

            runTime += getRunTime(startTime, endTime);
        }
        ContainerInfoVo containerInfoVo = new ContainerInfoVo(container, createUser.getUsername(), runTime);
        log.info("获取容器信息{}成功！", containerInfoVo.toString());
        return containerInfoVo;
    }

    /**
     * 查询容器日志
     * @param containerId
     * @return
     */
    @Override
    public List<RunLogVo> getLog(String containerId) {

        Container container = containerMapper.selectById(containerId);
        return JSON.parseArray(JSON.parseObject(container.getRun_log()).getString("run_log"), RunLogVo.class);
    }

    /**
     * 查看容器的启动历史记录
     * @return
     */
    @Override
    public List<ContainerStartVo> getContainerStart() {
        //1. 从redis中取数据
        String containerStartLogs = redisCache.getCacheObject("container:start:logs");
        if (containerStartLogs == null) {
            return null;
        }
        //2. 对数据进行处理，获取到需要的数据
        Map<String, StartLogVo> startLogs = getStartLog(containerStartLogs);
        //3. 遍历数据，返回固定格式的数据
        List<ContainerStartVo> containerStartVoList = new ArrayList<>();
        startLogs.forEach((key, value) -> {
            Container container = containerMapper.selectById(key);
            String author = userMapper.selectById(container.getUser_id()).getUsername();
            ContainerStartVo containerStartVo = new ContainerStartVo(container, author, value.getStartTime(), value.getEndTime());
            containerStartVoList.add(containerStartVo);
        });
        log.info("获取所有容器启动历史记录成功");
        return containerStartVoList;
    }

    /**
     * 查看正在运行的容器
     * @return
     */
    @Override
    public List<RunningContainerVo> getRunning() {
        QueryWrapper<Container> query = new QueryWrapper<>();
        query.eq("status", 1);
        List<Container> containers = containerMapper.selectList(query);
        List<RunningContainerVo> containerInfos = new ArrayList<>();
        for (Container container : containers) {
            User author = userMapper.selectById(container.getUser_id());
            //获取容器运行日志
            List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);
            //得出容器运行时间
            long runTime = 0;
            for (int i = 0; i < start_log.size(); i++) {
                String startTime = start_log.get(i).getStartTime();
                String endTime = start_log.get(i).getEndTime();
                runTime += getRunTime(startTime, endTime);
            }
            RunningContainerVo containerInfo = new RunningContainerVo(container, author.getUsername(), runTime);
            containerInfos.add(containerInfo);
        }
        log.info("查询正在运行的容器: {}", containerInfos);
        return containerInfos;
    }

    /**
     * 自定义方法：获取容器在两个时间段内运行的时间
     * @param startTime
     * @param endTime
     * @return
     */
    public long getRunTime(String startTime, String endTime) {
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 解析字符串为 LocalDateTime 对象
        LocalDateTime beginTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime offTime = LocalDateTime.parse(endTime, formatter);
        Duration duration = Duration.between(beginTime, offTime);
        //计算时间差
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours;
    }

    /**
     * 自定义方法：解析json，返回处理后的数据
     * @param startLogs 待解析的String
     * @return Map<key, StartLogVo> 
     */
    public Map<String, StartLogVo> getStartLog(String startLogs) {
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
        return log;
    }

    /**
     * 自定义方法：新增容器的启动历史记录
     * @param startLogs
     * @param key
     * @param startLogVo
     * @throws JsonProcessingException
     */
    public void addStartLogsMessage(String startLogs, String key, StartLogVo startLogVo) throws JsonProcessingException {
        //存储时间的json对象
        JSONObject startLogJson = new JSONObject();
        //获取到json集合
        JSONArray runLogList = JSON.parseArray(startLogs);
        //判断是否为空
        //1. 开始时间为空，结束时间有值
        if (startLogVo.getStartTime() == null) {
            int size = runLogList.size();
            //修改传入的数据的时间
            JSONObject jsonObject = (JSONObject) runLogList.get(size - 1);
            JSONObject startLogTime = jsonObject.getJSONObject(key);
            startLogVo.setStartTime(startLogTime.getString("startTime"));
            //移除之前的数据
            runLogList.remove(size-1);
        } else if (startLogVo.getEndTime() == null){
            startLogVo.setEndTime("无");
        }
        startLogJson.put("startTime", startLogVo.getStartTime());
        startLogJson.put("endTime", startLogVo.getEndTime());
        //将容器id和时间，设置为json对象并存入json集合
        JSONObject startLog = new JSONObject();
        startLog.put(key, startLogJson);
        runLogList.add(startLog);
        //序列化后存入redis
        String runLogsString = objectMapper.writeValueAsString(runLogList);
        redisCache.setCacheObject("container:start:logs", runLogsString);
    }
}




