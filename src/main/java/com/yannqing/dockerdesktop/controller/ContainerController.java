package com.yannqing.dockerdesktop.controller;

import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.domain.Container;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.BaseResponse;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.vo.container.ContainerStartVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public BaseResponse<ContainerInfoVo> getContainerInfo(String containerId) {
        ContainerInfoVo containerInfo = containerService.getContainerInfo(containerId);
        return ResultUtils.success(Code.SUCCESS, containerInfo, "获取容器信息成功！");
    }

    @PostMapping("/updateDataSize")
    public BaseResponse<Object> changeDataSize(String containerId, Integer size) {
        return ResultUtils.success(Code.SUCCESS, null, "修改数据盘大小成功！（未实现）");
    }

    @GetMapping("/getLog")
    public BaseResponse<List<RunLogVo>> getLog(String containerId) {
        List<RunLogVo> log = containerService.getLog(containerId);
        return ResultUtils.success(Code.SUCCESS, log, "获取容器日志成功");
    }

    @PostMapping("/add")
    public BaseResponse<Object> add() {
        return ResultUtils.success(Code.SUCCESS, null, "创建容器成功（未实现）");
    }

    @PostMapping("/delete")
    public BaseResponse<Object> delete(String containerId) {
        return ResultUtils.success(Code.SUCCESS, null, "删除容器成功！（未实现）");
    }

    @PostMapping("/run")
    public BaseResponse<Object> run(String containerId) {
        return ResultUtils.success(Code.SUCCESS, null, "启动容器成功！（未实现）");
    }

    @PostMapping("/stop")
    public BaseResponse<Object> stop(String containerId) {
        return ResultUtils.success(Code.SUCCESS, null, "停止容器成功！（未实现）");
    }

    @PostMapping("/reset")
    public BaseResponse<Object> reset(String containerId) {
        return ResultUtils.success(Code.SUCCESS, null, "重置容器成功！（未实现）");
    }

    /**
     * 查询容器的启动历史记录
     * @param containerId
     * @return
     */
    @GetMapping("/runLogs")
    public BaseResponse<ContainerStartVo> runLogs(String containerId) {
        ContainerStartVo containerStart = containerService.getContainerStart(containerId);
        return ResultUtils.success(Code.SUCCESS, containerStart, "查询容器启动记录成功！");
    }

    /**
     * 查询正在运行的容器
     * @return
     */
    @GetMapping("/getRunning")
    public BaseResponse<List<ContainerInfoVo>> getRunning() {
        List<ContainerInfoVo> running = containerService.getRunning();

        return ResultUtils.success(Code.SUCCESS, running, "查询所有正在运行的容器成功！");
    }
}
