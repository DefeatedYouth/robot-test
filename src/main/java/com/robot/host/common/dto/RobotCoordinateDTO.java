package com.robot.host.common.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("机器人实时坐标信息")
public class RobotCoordinateDTO {

    @ApiModelProperty("机器人名称")
    private String robotName;
    @ApiModelProperty("文件名称")
    private String filePath;
    @ApiModelProperty("机器人编码")
    private String robotCode;
    @ApiModelProperty("时间")
    private String time;
    @ApiModelProperty("坐标框（像素点）")
    private String coordinatePixel;
    @ApiModelProperty("坐标框（经纬度）")
    private String coordinateGeography;
}
