package com.yannqing.dockerdesktop.service.impl;

import ch.qos.logback.core.util.COWArrayList;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.vo.container.*;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.mapper.ContainerMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * 获取容器信息（TODO：管理员限制）
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

    @Override
    public List<RunLogVo> getLog(String containerId) {

        Container container = containerMapper.selectById(containerId);
        return JSON.parseArray(JSON.parseObject(container.getRun_log()).getString("run_log"), RunLogVo.class);
    }

    @Override
    public ContainerStartVo getContainerStart(String containerId) {
        Container container = containerMapper.selectById(containerId);

        return new ContainerStartVo(container);
    }

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


}




