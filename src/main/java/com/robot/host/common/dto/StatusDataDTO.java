package com.robot.host.common.dto;

import com.robot.host.common.constants.EnumRobotComplusStatusDataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("状态数据")
public class StatusDataDTO {

    @ApiModelProperty("机器人编码")
    private String robotCode;

    private EnumRobotComplusStatusDataType statusDataType;

}
