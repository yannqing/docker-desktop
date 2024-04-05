package com.yannqing.dockerdesktop.service.impl;

import ch.qos.logback.core.util.COWArrayList;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.mapper.ContainerMapper;
import com.yannqing.dockerdesktop.vo.container.ContainerStartVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import com.yannqing.dockerdesktop.vo.container.StartLogVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        ContainerInfoVo containerInfoVo = new ContainerInfoVo(container, createUser.getUsername());
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
    public List<ContainerInfoVo> getRunning() {
        QueryWrapper<Container> query = new QueryWrapper<>();
        query.eq("status", 1);
        List<Container> containers = containerMapper.selectList(query);
        List<ContainerInfoVo> containerInfos = new ArrayList<>();
        for (Container container : containers) {
            User author = userMapper.selectById(container.getUser_id());
            ContainerInfoVo containerInfo = new ContainerInfoVo(container, author.getUsername());
            containerInfos.add(containerInfo);
        }
        log.info("查询正在运行的容器: {}", containerInfos);
        return containerInfos;
    }


}




