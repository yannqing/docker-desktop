package com.yannqing.dockerdesktop.controller;

import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.BaseResponse;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/container")
public class ContainerController {

    @Resource
    private ContainerService containerService;

    /**
     * 获取容器信息（TODO：管理员限制）
     * @param containerId
     * @return
     */
    @GetMapping("/getInfo")
    public BaseResponse<ContainerInfoVo> getContainerInfo(Integer containerId) {
        ContainerInfoVo containerInfo = containerService.getContainerInfo(containerId);
        return ResultUtils.success(Code.SUCCESS, containerInfo, "获取容器信息成功！");
    }
}
