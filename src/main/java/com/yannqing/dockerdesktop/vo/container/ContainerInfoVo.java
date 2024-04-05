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
    private String name;
    private String author;
    private Date create_time;
    private Integer internet;
    private Integer disk_size;
    private List<RunLogVo> run_log;
    private List<StartLogVo> start_log;

    public ContainerInfoVo(Container container, String author) {
        this.name = container.getName();
        this.author = author;
        this.create_time = container.getCreate_time();
        this.internet = container.getInternet();
        this.disk_size = container.getDisk_size();
        String runLog = container.getRun_log();
        this.run_log = JSON.parseArray(JSON.parseObject(runLog).getString("run_log"), RunLogVo.class);
        this.start_log = JSON.parseArray(JSON.parseObject(container.getStart_log()).getString("start_log"), StartLogVo.class);
    }
}
