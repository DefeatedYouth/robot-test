package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("微气象数据")
@TableName("robot_weather_info")
public class WeatherInfoEntry {

    @TableId
    private Long weatherInfoId;

    @ApiModelProperty("机器人编码")
    private String robotCode;

    @ApiModelProperty("温度")
    private String temp;

    @ApiModelProperty("湿度")
    private String humidity;

    @ApiModelProperty("风速")
    private String windSpeed;

}
