package com.yannqing.dockerdesktop.vo.container;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 容器日志Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunLogVo {
    private String time;
    private String action;
}
