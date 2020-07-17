package com.robot.host.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("坐标信息")
@Data
public class CoordinateDTO {

    @ApiModelProperty("机器人编码")
    private String robotCode;

}
