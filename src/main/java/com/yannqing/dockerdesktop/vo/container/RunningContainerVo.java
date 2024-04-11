package com.yannqing.dockerdesktop.vo.container;

import com.alibaba.fastjson.JSON;
import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.List;

/**
 * 查询所有正在运行的容器--接口
 */
@Data
public class RunningContainerVo {
    private String id;
    private String name;
    private Integer disk_size;
    private Integer internet;
    private String username;
    private Integer user_id;
    private String last_start_time;
    private long run_time;

    public RunningContainerVo(Container container, String author, long run_time) {
        this.id = container.getId();
        this.name = container.getName();
        this.disk_size = container.getDisk_size();
        this.internet = container.getInternet();
        this.user_id = container.getUser_id();
        this.username = author;
        List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);
        this.last_start_time = start_log.get(start_log.size()-1).getStart_time();
        this.run_time = run_time;
    }
}
