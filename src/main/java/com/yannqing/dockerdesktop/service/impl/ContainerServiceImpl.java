package com.yannqing.dockerdesktop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StopContainerCmd;
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
import com.yannqing.dockerdesktop.utils.DateFormat;
import com.yannqing.dockerdesktop.utils.JwtUtils;
import com.yannqing.dockerdesktop.utils.RedisCache;
import com.yannqing.dockerdesktop.vo.container.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            String startTime = start_log.get(i).getStart_time();
            String endTime = start_log.get(i).getEnd_time();
            if (endTime == null) {
                continue;
            }else {
                runTime += getRunTime(startTime, endTime);
            }
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

    @Override
    public String createContainer(String containerName, String token) throws JsonProcessingException {
        //获取到登录者的信息
        String userInfoFromToken = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfoFromToken, User.class);

        // 构造创建容器命令
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("dulljz/uos-1060")
                .withCmd("echo", "Create Container Success!")
                .withPrivileged(true)
                .withCmd("/usr/sbin/init")
                .withName(containerName);

        // 执行创建容器命令
        String containerId = createContainerCmd.exec().getId();
        dockerClient.startContainerCmd(containerId).exec();
        log.info("{}创建并运行容器{}成功", loginUser.getUsername(), containerName);
        // 存入数据库
        Container container = new Container();
        container.setId(containerId);
        container.setName(containerName);
        container.setCreate_time(new Date());
        container.setInternet(1);
        container.setDisk_size(50);
        container.setStatus(1);
        container.setUser_id(loginUser.getUser_id());
        //1. 格式化容器日志，并存入数据库
        JSONObject runLogObject = new JSONObject();
        runLogObject.put("time", DateFormat.getCurrentTime());
        //TODO: 如果是管理员，则修改
        runLogObject.put("action", "用户"+ loginUser.getUsername() + "创建并启动了容器: "+containerName);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(runLogObject);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("run_log", jsonArray);
        container.setRun_log(objectMapper.writeValueAsString(jsonObject));
        //2. 格式化容器启动记录，存入数据库
        JSONObject startLogObject = new JSONObject();
        startLogObject.put("start_time", DateFormat.getCurrentTime());
        //TODO: 如果是管理员，则修改
        startLogObject.put("end_time", null);
        JSONArray startLogJsonArray = new JSONArray();
        startLogJsonArray.add(startLogObject);
        JSONObject startLogJsonObject = new JSONObject();
        startLogJsonObject.put("start_log", startLogJsonArray);
        container.setStart_log(objectMapper.writeValueAsString(startLogJsonObject));
        container.setIsDelete(0);
        containerMapper.insert(container);
        //所有容器的启动历史，存入redis
        addStartLogsMessage(containerId, new StartLogVo(DateFormat.getCurrentTime(), null));
        return containerId;
    }

    @Override
    public void stopContainer(String containerId) throws JsonProcessingException {
        //1. 停止容器
        StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(containerId);
        stopContainerCmd.exec();
        //2. 修改数据库里容器的状态
        containerMapper.update(new UpdateWrapper<Container>().eq("id", containerId).set("status", 1));
        //3. 新增容器启动记录到redis
        addStartLogsMessage(containerId, new StartLogVo(null, DateFormat.getCurrentTime()));

        log.info("停止运行容器：{}", containerId);
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
            ContainerStartVo containerStartVo = new ContainerStartVo(container, author, value.getStart_time(), value.getEnd_time());
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
                String startTime = start_log.get(i).getStart_time();
                String endTime = start_log.get(i).getEnd_time();
                if (endTime == null || startTime == null) {
                    continue;
                }
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
     * @param key
     * @param startLogVo
     * @throws JsonProcessingException
     */
    public void addStartLogsMessage(String key, StartLogVo startLogVo) throws JsonProcessingException {
        //存储时间的json对象
        JSONObject startLogJson = new JSONObject();
        //redis数据
        String startLogs = redisCache.getCacheObject("container:start:logs");
        JSONArray runLogList = null;

        if (startLogs == null) {
            runLogList = new JSONArray();
        }else {
            runLogList = JSON.parseArray(startLogs);
        }
        //获取到json集合
        //判断是否为空
        //1. 开始时间为空，结束时间有值
        if (startLogVo.getStart_time() == null) {
            int size = runLogList.size();
            //修改传入的数据的时间
            JSONObject startLogTime = new JSONObject();
            for (int i = runLogList.size() - 1; i >= 0; i--) {
                JSONObject jsonObject = (JSONObject) runLogList.get(i);
                if (jsonObject.getJSONObject(key) != null) {
                    startLogTime = jsonObject.getJSONObject(key);
                    break;
                } else {
                    startLogTime = null;
                }
            }
            if (startLogTime == null) {
                throw new IllegalArgumentException("无法添加启动历史记录");
            }
            startLogVo.setStart_time(startLogTime.getString("startTime"));
            //移除之前的数据
//            runLogList.remove(size-1);
        } else if (startLogVo.getEnd_time() == null || Objects.equals(startLogVo.getEnd_time(), "无") || startLogVo.equals("")){
            startLogVo.setEnd_time("无");
        }
        startLogJson.put("startTime", startLogVo.getStart_time());
        startLogJson.put("endTime", startLogVo.getEnd_time());
        //将容器id和时间，设置为json对象并存入json集合
        JSONObject startLog = new JSONObject();
        startLog.put(key, startLogJson);
        runLogList.add(startLog);
        //序列化后存入redis
        String runLogsString = objectMapper.writeValueAsString(runLogList);
        redisCache.setCacheObject("container:start:logs", runLogsString);
    }
}




