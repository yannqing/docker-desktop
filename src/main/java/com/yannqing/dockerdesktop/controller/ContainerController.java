package com.yannqing.dockerdesktop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yannqing.dockerdesktop.common.Code;
import com.yannqing.dockerdesktop.service.ContainerService;
import com.yannqing.dockerdesktop.utils.ResultUtils;
import com.yannqing.dockerdesktop.vo.BaseResponse;
import com.yannqing.dockerdesktop.vo.container.ContainerInfoVo;
import com.yannqing.dockerdesktop.vo.container.ContainerStartVo;
import com.yannqing.dockerdesktop.vo.container.RunLogVo;
import com.yannqing.dockerdesktop.vo.container.RunningContainerVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
     * 获取容器详细信息（TODO：管理员限制）
     * @param containerId
     * @return
     */
    @GetMapping("/getInfo")
    public BaseResponse<ContainerInfoVo> getContainerInfo(String containerId) {
        ContainerInfoVo containerInfo = containerService.getContainerInfo(containerId);
        return ResultUtils.success(Code.SUCCESS, containerInfo, "获取容器信息成功！");
    }

    @GetMapping("/getAllContainer")
    public BaseResponse<List<ContainerInfoVo>> getAllContainers(HttpServletRequest request) {
        return null;
    }

    /**
     * 修改容器的数据盘大小
     * @param containerId
     * @param size
     * @return
     */
    @PostMapping("/updateDataSize")
    public BaseResponse<Object> changeDataSize(String containerId, Integer size) {
        return ResultUtils.success(Code.SUCCESS, null, "修改数据盘大小成功！");
    }

    /**
     * 获取容器日志
     * @param containerId
     * @return
     */
    @GetMapping("/getLog")
    public BaseResponse<List<RunLogVo>> getLog(String containerId) {
        List<RunLogVo> log = containerService.getLog(containerId);
        return ResultUtils.success(Code.SUCCESS, log, "获取容器日志成功");
    }

    /**
     * 创建容器
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<List<String>> createContainer(String containerName, HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("token");
        List<String> containerId = containerService.createContainer(containerName, token);
        return ResultUtils.success(Code.SUCCESS, containerId, "创建容器成功!");
    }

    /**
     *删除容器
     * @param containerId
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Object> delete(String containerId, HttpServletRequest request) throws JsonProcessingException {
        boolean result = containerService.deleteContainer(containerId, request.getHeader("token"));
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "删除容器成功！");
        }else {
            return ResultUtils.failure(Code.DELETE_CONTAINER_FAILURE, null, "容器在运行中，无法进行销毁");
        }
    }

    /**
     * 启动容器
     * @param containerId
     * @return
     */
    @PostMapping("/run")
    public BaseResponse<Object> run(String containerId, HttpServletRequest request) throws JsonProcessingException {
        boolean result = containerService.runContainer(containerId, request.getHeader("token"));
        if (result) {
            return ResultUtils.success(Code.SUCCESS, null, "启动容器成功！");
        } else {
            return ResultUtils.failure(Code.RUN_CONTAINER_FAILURE, null, "容器正在运行，无法启动");
        }
    }

    /**
     * 停止容器
     * @param containerId
     * @return
     */
    @PostMapping("/stop")
    public BaseResponse<Object> stop(String containerId) throws JsonProcessingException {
        containerService.stopContainer(containerId);
        return ResultUtils.success(Code.SUCCESS, null, "停止容器成功！");
    }

    /**
     * 重启容器
     * @param containerId
     * @return
     */
    @PostMapping("/reset")
    public BaseResponse<Object> reset(String containerId) {
        return ResultUtils.success(Code.SUCCESS, null, "重启容器成功！");
    }

    /**
     * 查询所有容器的启动历史记录
     * @return
     */
    @GetMapping("/runLogs")
    public BaseResponse<List<ContainerStartVo>> runLogs() {
        List<ContainerStartVo> containerStarts = containerService.getContainerStart();
        return ResultUtils.success(Code.SUCCESS, containerStarts, "查询容器启动记录成功！");
    }

    /**
     * 查询正在运行的容器
     * @return
     */
    @GetMapping("/getRunning")
    public BaseResponse<List<RunningContainerVo>> getRunning() {
        List<RunningContainerVo> running = containerService.getRunning();

        return ResultUtils.success(Code.SUCCESS, running, "查询正在运行的容器成功！");
    }

    /**
     * 登录的用户查看自己的容器信息
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/getMyContainer")
    public BaseResponse<List<ContainerInfoVo>> getMyContainerInfo(HttpServletRequest request) throws JsonProcessingException {
        List<ContainerInfoVo> myContainerInfo = containerService.getMyContainerInfo(request.getHeader("token"));
        return ResultUtils.success(Code.SUCCESS, myContainerInfo, "查看我的容器成功");
    }
}
