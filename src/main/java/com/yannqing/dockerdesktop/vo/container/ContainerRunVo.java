package com.yannqing.dockerdesktop.vo.container;

import lombok.Data;

import java.util.List;

@Data
public class ContainerRunVo {
    private long containerId;
    private List<String> runLog;
}
