package com.yannqing.dockerdesktop.vo.container;

import com.alibaba.fastjson.JSON;
import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.List;

/**
 * 获取所有容器的启动记录--接口
 */
@Data
public class ContainerStartVo {
    private String id;
    private String name;
    private Integer internet;
    private Integer diskSize;
    private Integer status;
    private String username;
    private String startTime;
    private String endTime;

    public ContainerStartVo(Container container, String author, String startTime, String endTime){
        this.id = container.getId();
        this.name = container.getName();
        this.diskSize = container.getDisk_size();
        this.internet = container.getInternet();
        this.status = container.getStatus();
        this.username = author;
        this.startTime = startTime;
        this.endTime = endTime;
//        this.startLogs = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

    }
}
