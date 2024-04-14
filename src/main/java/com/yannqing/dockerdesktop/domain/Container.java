package com.yannqing.dockerdesktop.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName yan_container
 */
@TableName(value ="yan_container")
@Data
public class Container implements Serializable {
    /**
     * 容器id（uuid）
     */
    @TableId(value = "id")
    private String id;

    /**
     * 容器名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date create_time;

    /**
     * 1有网络，0无
     */
    @TableField(value = "internet")
    private Integer internet;

    /**
     * 磁盘大小（单位g）
     */
    @TableField(value = "disk_size")
    private Integer disk_size;

    /**
     * 0停止运行，1启动中，-1已销毁
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建用户id
     */
    @TableField(value = "user_id")
    private Integer user_id;

    /**
     * 日志信息（json）
     */
    @TableField(value = "run_log")
    private String run_log;

    /**
     * 启动日志信息（json）
     */
    @TableField(value = "start_log")
    private String start_log;

    /**
     * 0未删除，1已删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 容器端口
     */
    @TableField(value = "port")
    private Integer port;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Container other = (Container) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()))
            && (this.getInternet() == null ? other.getInternet() == null : this.getInternet().equals(other.getInternet()))
            && (this.getDisk_size() == null ? other.getDisk_size() == null : this.getDisk_size().equals(other.getDisk_size()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getRun_log() == null ? other.getRun_log() == null : this.getRun_log().equals(other.getRun_log()))
            && (this.getStart_log() == null ? other.getStart_log() == null : this.getStart_log().equals(other.getStart_log()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getPort() == null ? other.getPort() == null : this.getPort().equals(other.getPort()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        result = prime * result + ((getInternet() == null) ? 0 : getInternet().hashCode());
        result = prime * result + ((getDisk_size() == null) ? 0 : getDisk_size().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getRun_log() == null) ? 0 : getRun_log().hashCode());
        result = prime * result + ((getStart_log() == null) ? 0 : getStart_log().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getPort() == null) ? 0 : getPort().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", create_time=").append(create_time);
        sb.append(", internet=").append(internet);
        sb.append(", disk_size=").append(disk_size);
        sb.append(", status=").append(status);
        sb.append(", user_id=").append(user_id);
        sb.append(", run_log=").append(run_log);
        sb.append(", start_log=").append(start_log);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", port=").append(port);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}