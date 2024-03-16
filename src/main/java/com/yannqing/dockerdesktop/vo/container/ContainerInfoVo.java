package com.yannqing.dockerdesktop.vo.container;

import com.yannqing.dockerdesktop.domain.Container;
import lombok.Data;

import java.util.Date;

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
    private String run_log;

    public ContainerInfoVo(Container container, String author) {
        this.name = container.getName();
        this.author = author;
        this.create_time = container.getCreate_time();
        this.internet = container.getInternet();
        this.disk_size = container.getDisk_size();
        this.run_log = container.getRun_log();
    }
}
