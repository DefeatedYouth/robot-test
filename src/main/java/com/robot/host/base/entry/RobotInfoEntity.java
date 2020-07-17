package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *  机器人模型
 *
 *
 */
@Data
@TableName("robot_robot_info")
public class RobotInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 机器人id
     */

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "机器人id", dataType = "Long")
    private Long robotInfoId;
    /**
     * 名称
     */

    @ApiModelProperty(value = "名称", dataType = "String")
    private String name;
    /**
     * 编码
     */

    @ApiModelProperty(value = "编码", dataType = "String")
    private String code;

    /**
     * 速度
     */

    @ApiModelProperty(value = "速度", dataType = "String")
    private String speed;
    /**
     * 总里程
     */

    @ApiModelProperty(value = "总里程", dataType = "String")
    private String mileage;
    /**
     * 状态：0=空闲，1=巡检中，2=充电中，3=故障
     */

    @ApiModelProperty(value = "状态：0=空闲，1=巡检中，2=充电中，3=故障", dataType = "Integer")
    private Integer status;
    /**
     * 电量
     */

    @ApiModelProperty(value = "电量", dataType = "String")
    private String quantity;

    /**
     * 变电站id
     */

    @ApiModelProperty(value = "实时信息", dataType = "String")
    private String jsonStr;

    /**
     * 变电站id
     */

    @ApiModelProperty(value = "当前机器人在地图上的X坐标", dataType = "String")
    private String posX;

    /**
     * 变电站id
     */

    @ApiModelProperty(value = "当前机器人在地图上的Y坐标", dataType = "String")
    private String posY;
    @ApiModelProperty(value = "图片宽度", dataType = "String")
    private String spacePicWidth;
    @ApiModelProperty(value = "图片高度", dataType = "String")
    private String spacePicHeight;

    @ApiModelProperty(value = "变电站室内平面地图图片", dataType = "String")
    private String spacePic;


    /**
     * 变电站id
     */

    @ApiModelProperty(value = "变电站id", dataType = "Long")
    private Long siteId;
    /**
     * 创建人
     */

    @ApiModelProperty(value = "创建人", dataType = "String")
    private String createUser;
    /**
     * 创建时间
     */

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度", dataType = "String")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度", dataType = "String")
    private String latitude;


    private String manufacturer;

    private Integer istransport;

    private Integer type;

    private String mappath;

    private String robotInfo;


}
