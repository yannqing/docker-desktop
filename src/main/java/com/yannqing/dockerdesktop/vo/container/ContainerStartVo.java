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
    private Integer disk_size;
    private Integer status;
    private Integer user_id;
    private String username;
    private String start_time;
    private String end_time;

    public ContainerStartVo(Container container, String author, String startTime, String endTime){
        this.id = container.getId();
        this.name = container.getName();
        this.disk_size = container.getDisk_size();
        this.internet = container.getInternet();
        this.status = container.getStatus();
        this.user_id = container.getUser_id();
        this.username = author;
        this.start_time = startTime;
        this.end_time = endTime;
//        this.startLogs = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

    }
}
