package com.yannqing.dockerdesktop.vo.container;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 容器的启动和关闭日志Vo
 */
@Data
@AllArgsConstructor
public class StartLogVo {
    private String startTime;
    private String endTime;
}
