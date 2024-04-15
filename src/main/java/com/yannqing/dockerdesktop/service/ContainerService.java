package com.yannqing.dockerdesktop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.vo.container.ContainerStartVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import com.yannqing.dockerdesktop.vo.container.RunningContainerVo;

import java.util.List;

/**
* @author 67121
* @description 针对表【yan_container】的数据库操作Service
* @createDate 2024-03-17 01:15:32
*/
public interface ContainerService extends IService<Container> {
    ContainerInfoVo getConInfo(String containerId);
    ContainerInfoVo getContainerInfo(String containerId);
    List<ContainerInfoVo> getAllContainers();
    List<RunLogVo> getLog(String containerId);
    List<String> createContainer(String containerName, String token) throws JsonProcessingException;
    boolean deleteContainer(String containerId, String token) throws JsonProcessingException;
    boolean runContainer(String containerId, String token) throws JsonProcessingException;
    boolean resetContainer(String containerId, String token) throws JsonProcessingException;
    void stopContainer(String containerId, String token) throws JsonProcessingException;
    List<ContainerStartVo> getContainerStart();
    List<RunningContainerVo> getRunning();
    List<ContainerInfoVo> getMyContainerInfo(String token) throws JsonProcessingException;
}
