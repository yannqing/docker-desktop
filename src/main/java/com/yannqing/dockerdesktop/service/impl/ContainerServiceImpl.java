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
import com.github.dockerjava.api.command.RemoveConfigCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
     * @param containerId 容器id
     * @return 返回容器信息
     */
    @Override
    public ContainerInfoVo getContainerInfo(String containerId) {
        Container container = containerMapper.selectById(containerId);
        User createUser = userMapper.selectById(container.getUser_id());
        List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

        long runTime = 0;

        for (StartLogVo startLogVo : start_log) {
            String startTime = startLogVo.getStart_time();
            String endTime = startLogVo.getEnd_time();
            if (endTime == null) {
                continue;
            } else {
                runTime += getRunTime(startTime, endTime);
            }
        }
        ContainerInfoVo containerInfoVo = new ContainerInfoVo(container, createUser.getUsername(), runTime);
        log.info("获取容器信息{}成功！", containerInfoVo);
        return containerInfoVo;
    }

    /**
     * 查询容器日志
     * @param containerId 容器id
     * @return 容器日志
     */
    @Override
    public List<RunLogVo> getLog(String containerId) {

        Container container = containerMapper.selectById(containerId);
        return JSON.parseArray(JSON.parseObject(container.getRun_log()).getString("run_log"), RunLogVo.class);
    }

    /**
     * 创建并运行容器
     * @param containerName 要创建的容器名称
     * @param token 查询登录用户信息
     * @return 返回创建的容器id
     * @throws JsonProcessingException
     */
    @Override
    public String createContainer(String containerName, String token) throws JsonProcessingException {
        //获取到登录者的信息
        String userInfoFromToken = JwtUtils.getUserInfoFromToken(token);
        User loginUser = objectMapper.readValue(userInfoFromToken, User.class);

        // 创建容器的配置
//        Map<String, String> storageOpts = new HashMap<>();
//        storageOpts.put("size", loginUser.getDisk_size()+"G");
//
//        String internet = loginUser.getInternet() == 1 ? null : "none";



        // 定义暴露端口
        ExposedPort tcp80 = ExposedPort.tcp(6080);

        // 定义端口绑定
        PortBinding portBinding = PortBinding.parse("5501:6080");
        Ports portBindings = new Ports();
        portBindings.bind(tcp80, portBinding.getBinding());


        // 构造创建容器命令
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd("x11vnc/docker-desktop")
//                .withCmd("echo", "Create Container Success!")
                .withPrivileged(true)
//                .withCmd("/usr/sbin/init")
                .withExposedPorts(tcp80)
                .withPortBindings(portBindings)
                .withName(containerName);

//                .withHostConfig(HostConfig.newHostConfig()
//                    .withStorageOpt(storageOpts)
//                    .withNetworkMode(internet));

        // 执行创建容器命令
        String containerId = createContainerCmd.exec().getId();
        dockerClient.startContainerCmd(containerId).exec();
        // 存入数据库
        Container container = new Container();
        container.setId(containerId);
        container.setName(containerName);
        container.setCreate_time(new Date());
        container.setInternet(1);
        container.setDisk_size(50);
        container.setStatus(1);
        container.setUser_id(loginUser.getUser_id());
        //格式化容器日志，并存入数据库
        List<RunLogVo> logs = new ArrayList<>();
        RunLogVo runLogVo = new RunLogVo();
        runLogVo.setTime(DateFormat.getCurrentTime());
        runLogVo.setAction("用户"+ loginUser.getUsername() + "创建并启动了容器: "+containerName);
        logs.add(runLogVo);
        container.setRun_log(getRunLogString(logs));
        //格式化容器启动记录，存入数据库
        List<StartLogVo> startLogVos = new ArrayList<>();
        StartLogVo startLogVo = new StartLogVo(DateFormat.getCurrentTime(), null);
        startLogVos.add(startLogVo);
        container.setStart_log(getStartLogString(startLogVos));
        container.setIsDelete(0);
        containerMapper.insert(container);
        //所有容器的启动历史，存入redis
        addStartLogsMessage(containerId, new StartLogVo(DateFormat.getCurrentTime(), null));

        log.info("{}创建并运行容器{}成功", loginUser.getUsername(), containerName);
        return containerId;
    }

    /**
     * 销毁容器
     * @param containerId 要销毁的容器id
     * @return
     */
    @Override
    public boolean deleteContainer(String containerId, String token) throws JsonProcessingException {
        //获取登录信息
        User loginUser = getUserByToken(token);
        //先判断容器是否在运行
        InspectContainerResponse containerResponse = dockerClient.inspectContainerCmd(containerId).exec();
        Boolean isRunning = containerResponse.getState().getRunning();
        if (Boolean.TRUE.equals(isRunning)) {
            return false;
        }
        //1. 销毁容器
        dockerClient.removeContainerCmd(containerId).exec();
        //2. 修改数据库字段
        Container container = containerMapper.selectById(containerId);
        //3. 查询日志，并新增
        List<RunLogVo> run_logs = getRunLog(container.getRun_log());
        RunLogVo runLogVo = new RunLogVo();
        runLogVo.setAction("用户" + loginUser.getUsername() + "销毁了容器：" + container.getName());
        runLogVo.setTime(DateFormat.getCurrentTime());
        run_logs.add(runLogVo);
        //4. 序列化存入数据库
        String runLogs = getRunLogString(run_logs);
        containerMapper.update(new UpdateWrapper<Container>()
                .eq("id", containerId).set("status", -1)
                .set("run_log", runLogs));
        return true;
    }

    /**
     * 启动容器
     * @param containerId
     * @param token
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public boolean runContainer(String containerId, String token) throws JsonProcessingException {
        //判断容器状态
        InspectContainerResponse containerResponse = dockerClient.inspectContainerCmd(containerId).exec();
        if (Boolean.TRUE.equals(containerResponse.getState().getRunning())) {
            return false;
        }
        //启动容器
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch (Exception e) {
            throw new IllegalArgumentException("启动容器失败，请重试！");
        }
        //存入三个日志中
        User loginUser = getUserByToken(token);
        Container container = containerMapper.selectById(containerId);
        List<RunLogVo> runLogs = getRunLog(container.getRun_log());
        List<StartLogVo> startLogs = getStartLog(container.getStart_log());
        runLogs.add(new RunLogVo(DateFormat.getCurrentTime(), "用户"+loginUser.getUsername()+"启动容器："+container.getName()));
        startLogs.add(new StartLogVo(DateFormat.getCurrentTime(), null));
        containerMapper.update(new UpdateWrapper<Container>()
                .eq("id", containerId)
                .set("status", 1)
                .set("run_log", getRunLogString(runLogs))
                .set("start_log", getStartLogString(startLogs)));

        addStartLogsMessage(containerId, new StartLogVo(DateFormat.getCurrentTime(), null));


        return true;
    }

    /**
     * 重启容器
     * @param containerId
     * @param token
     * @return
     */
    @Override
    public boolean resetContainer(String containerId, String token) throws JsonProcessingException {
        User loginUser = getUserByToken(token);
        dockerClient.restartContainerCmd(containerId).exec();

        Container container = containerMapper.selectById(containerId);
        List<RunLogVo> runLog = getRunLog(container.getRun_log());
        List<StartLogVo> startLog = getStartLog(container.getStart_log());
        runLog.add(new RunLogVo(DateFormat.getCurrentTime(), "用户"+loginUser.getUsername()+"重启了容器"));
        startLog.add(new StartLogVo(DateFormat.getCurrentTime(), null));

        containerMapper.update(new UpdateWrapper<Container>()
                .eq("id", containerId).set("status", 1)
                .set("run_log", getRunLogString(runLog))
                .set("start_log", getStartLogString(startLog)));
        //添加到redis
        addStartLogsMessage(containerId, new StartLogVo(DateFormat.getCurrentTime(), null));
        return true;
    }

    /**
     * 停止容器
     * @param containerId
     * @throws JsonProcessingException
     */
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
        Map<String, StartLogVo> startLogs = getRedisStartLog(containerStartLogs);
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
     * 登录的用户查看自己的容器信息
     * @param token
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public List<ContainerInfoVo> getMyContainerInfo(String token) throws JsonProcessingException {
        User loginUser = getUserByToken(token);

        List<Container> myContainers = containerMapper.selectList(new QueryWrapper<Container>().eq("user_id", loginUser.getUser_id()));

        List<ContainerInfoVo> containerInfoVos = new ArrayList<>();
        myContainers.forEach(myContainer -> {
            ContainerInfoVo containerInfoVo = new ContainerInfoVo(myContainer, loginUser.getUsername(), getRunTimeByRunTime(getStartLog(myContainer.getStart_log())));
            containerInfoVos.add(containerInfoVo);
        });


        return containerInfoVos;
    }

    public long getRunTimeByRunTime(List<StartLogVo> startLogVos) {
        long hours = 0;
        for (int i = 0; i < startLogVos.size(); i++) {
            hours += getRunTime(startLogVos.get(i).getStart_time(), startLogVos.get(i).getEnd_time());
        }
        return hours;
    }

    /**
     * 自定义方法：获取容器在两个时间段内运行的时间
     * @param startTime
     * @param endTime
     * @return
     */
    public long getRunTime(String startTime, String endTime) {
        if (startTime == null || endTime == null || startTime.equals("无") || endTime.equals("无")) {
            return 0;
        }
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
     * 自定义方法：解析json，返回处理后的容器启动日志--Redis
     * @param startLogs 待解析的String
     * @return Map<key, StartLogVo> 
     */
    public Map<String, StartLogVo> getRedisStartLog(String startLogs) {
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

    /**
     * 解析数据库中的run_log（反序列化）
     * @param runLogs
     * @return
     */
    public List<RunLogVo> getRunLog(String runLogs) {
        return JSON.parseArray(JSON.parseObject(runLogs).getString("run_log"), RunLogVo.class);
    }

    /**
     * 序列化run_logs
     * @param runLogs
     * @return
     */
    public String getRunLogString(List<RunLogVo> runLogs) throws JsonProcessingException {
        JSONArray jsonArray = new JSONArray();
        for (RunLogVo runLogVo : runLogs) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", runLogVo.getAction());
            jsonObject.put("time", runLogVo.getTime());
            jsonArray.add(jsonObject);
        }
        JSONObject runLog = new JSONObject();
        runLog.put("run_log", jsonArray);
        return objectMapper.writeValueAsString(runLog);
    }

    /**
     * 解析数据库中的start_log（反序列化）
     * @param startLogs
     * @return
     */
    public List<StartLogVo> getStartLog(String startLogs) {
        return JSON.parseArray(JSON.parseObject(startLogs).getString("start_log"), StartLogVo.class);
    }

    /**
     * 序列化start_log
     * @param startLogs
     * @return
     * @throws JsonProcessingException
     */
    public String getStartLogString(List<StartLogVo> startLogs) throws JsonProcessingException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < startLogs.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("start_time", startLogs.get(i).getStart_time());
            jsonObject.put("end_time", startLogs.get(i).getEnd_time());
            jsonArray.add(jsonObject);
        }
        JSONObject startLog = new JSONObject();
        startLog.put("start_log", jsonArray);
        return objectMapper.writeValueAsString(startLog);
    }

    /**
     * 自定义方法：根据token返回用户
     * @param token
     * @return
     * @throws JsonProcessingException
     */
    public User getUserByToken(String token) throws JsonProcessingException {
        String userInfo = JwtUtils.getUserInfoFromToken(token);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(userInfo, User.class);
    }
}




