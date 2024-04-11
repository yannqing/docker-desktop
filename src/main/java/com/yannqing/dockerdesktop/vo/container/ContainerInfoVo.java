package com.yannqing.dockerdesktop.vo.container;

import com.alibaba.fastjson.JSON;
import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.List;

/**
 * 获取容器详细信息--接口
 */
@Data
public class ContainerInfoVo {
    private String id;
    private String name;
    private String username;
    private Integer user_id;
    private Integer internet;
    private Integer disk_size;
    private Integer status;
    private String last_end_time;
    private long run_time;
    private String create_time;
    private List<RunLogVo> run_log;
    public ContainerInfoVo(Container container, String author, long run_time) {
        this.id = container.getId();
        this.name = container.getName();
        this.username = author;
        this.user_id = container.getUser_id();
        this.internet = container.getInternet();
        this.disk_size = container.getDisk_size();
        this.status = container.getStatus();
        this.create_time = String.valueOf(container.getCreate_time());
        this.run_time = run_time;
        String runLog = container.getRun_log();
        this.run_log = JSON.parseArray(JSON.parseObject(runLog).getString("run_log"), RunLogVo.class);

        List<StartLogVo> start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);

        this.last_end_time = start_log.get(start_log.size()-1).getEnd_time();
    }
}
