package com.yannqing.dockerdesktop.vo.container;

import com.alibaba.fastjson.JSON;
import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 容器信息
 */
@Data
public class ContainerInfoVo {
    private String id;
    private String name;
    private String username;
    private Integer user_id;
    private Integer internet;
    private Integer disk_size;
    private String last_start_time;
//    private List<RunLogVo> run_log;

    public ContainerInfoVo(Container container, String author) {
        this.id = container.getId();
        this.name = container.getName();
        this.username = author;
        this.user_id = container.getUser_id();
        this.internet = container.getInternet();
        this.disk_size = container.getDisk_size();
//        String runLog = container.getRun_log();
//        this.run_log = JSON.parseArray(JSON.parseObject(runLog).getString("run_log"), RunLogVo.class);

        List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

        this.last_start_time = start_log.get(start_log.size()-1).getStartTime();
    }
}
