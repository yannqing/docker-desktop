package com.yannqing.dockerdesktop.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

    ContainerInfoVo getContainerInfo(String containerId);

    List<RunLogVo> getLog(String containerId);

    List<ContainerStartVo> getContainerStart();

    List<RunningContainerVo> getRunning();
}
