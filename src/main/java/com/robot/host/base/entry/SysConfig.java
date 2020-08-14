package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("系统默认配置信息")
@TableName("robot_sys_config")
public class SysConfig {

    @TableId(type = IdType.AUTO)
    private Long sysConfigId;

    @ApiModelProperty("系统配置名称")
    private String configName;

    @ApiModelProperty("系统配置值")
    private String configValue;

    @ApiModelProperty("备注")
    private String configRemark;
}
