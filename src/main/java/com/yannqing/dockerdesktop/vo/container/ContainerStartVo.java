package com.yannqing.dockerdesktop.vo.container;

import com.alibaba.fastjson.JSON;
import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.List;

@Data
public class ContainerStartVo {
    private long containerId;
    private String name;
    private Integer internet;
    private Integer diskSize;
    private Integer status;
    private List<StartLogVo> startLogs;

    public ContainerStartVo(Container container){
        this.containerId = container.getUser_id();
        this.name = container.getName();
        this.diskSize = container.getDisk_size();
        this.internet = container.getInternet();
        this.status = container.getStatus();
        this.startLogs = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

    }
}
