package com.yannqing.dockerdesktop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yannqing.dockerdesktop.domain.User;
import com.yannqing.dockerdesktop.mapper.UserMapper;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.mapper.ContainerMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public ContainerInfoVo getContainerInfo(Integer containerId) {
        Container container = containerMapper.selectById(containerId);
        User createUser = userMapper.selectById(container.getUser_id());
        ContainerInfoVo containerInfoVo = new ContainerInfoVo(container, createUser.getUsername());
        log.info("获取容器信息{}成功！", containerInfoVo.toString());
        return containerInfoVo;
    }
}




