package com.robot.host.base.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 场景=监控对象表：来源1=区域、间隔，来源2=主设备，来源3=辅助设备
 *
 * @author dadi
 * @email
 * @date 2020-04-30 15:45:39
 */
@Data
@TableName("robot_scene_info")
@ApiModel(" 场景=监控对象表：来源1=区域、间隔，来源2=主设备，来源3=辅助设备")
public class SceneInfoEntity extends SiteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主设备监控点id
     */

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "场景id", dataType = "Long")
    private Long sceneInfoId;

    @ApiModelProperty(value = "父场景id", dataType = "Long")
    private Long parentSceneId;
    /**
     * 场景名称
     */

    @ApiModelProperty(value = "场景名称", dataType = "String")
    private String sceneName;
    @ApiModelProperty(value = "场景编码", dataType = "String")
    @NotNull(message = "场景编码不能为空", groups = { AddGroup.class})
    private String sceneCode;
    /**
     * 场景备注
     */

    @ApiModelProperty(value = "场景备注", dataType = "String")
    private String sceneRemark;
    @ApiModelProperty(value = "场景来源", dataType = "Integer")
    private  Integer infoSource;
    /**
     * 设备类型观察点全路径
     */

    @ApiModelProperty(value = "设备类型观察点全路径", dataType = "String")
    private String deviceTypeObservePath;
    /**
     * 设备类型关联观察点id
     */

    @ApiModelProperty(value = "设备类型关联观察点id", dataType = "Long")
    private Long deviceTypeObserveId;
    /**
     * 场地观察点全路径
     */

    @ApiModelProperty(value = "场地观察点全路径", dataType = "String")
    private String placeObservePath;
    /**
     * 场地观察点id
     */

    @ApiModelProperty(value = "场地观察点id", dataType = "Long")
    private Long placeObserveId;



    @ApiModelProperty(value = "识别类型（<1>: = 表计读取<2>: = 位置状态识别<3>: = 设备外观查看<4>: = 红外测温<5>: = 声音检测<6>: = 闪烁检测）", dataType = "Integer",required = true)
    @NotNull(message = "识别类型不能为空", groups = { AddGroup.class})
    private Integer identifyType;
    /**
     * 表计类型（油位表、液压表、油温表、档位表、气压表）
     */

    @ApiModelProperty(value = "表计类型（<1>: = 油位表<2>: = 避雷器动作次数表<3>: = 泄漏电流表<4>: = SF 6 压力表<5>: = 液压表<6>: = 开关动作次数表<7>: = 油温表<8>: = 档位表<9>: = 气压表）", dataType = "Integer",required = true)
    /***
     * <1>: = 油位表
     <3>: = 泄漏电流表
     <4>: = SF 6 压力表
     <5>: = 液压表
     <6>: = 开关动作次数表
     <7>: = 油温表
     <8>: = 档位表
     <9>: = 气压表
     */
    @NotNull(message = "表计类型不能为空", groups = { AddGroup.class})
    private Integer meterType;
    @ApiModelProperty(value = "告警值，大于注意值小于告警值为注意，大于告警值为告警", dataType = "Double")
    private Double warnVal;
    @ApiModelProperty(value = "注意值（小于注意值为正常）", dataType = "Double")
    private Double attentionVal;
    /**
     * 区域或间隔id
     */

    @ApiModelProperty(value = "区域或间隔id", dataType = "Long")
    private Long fromPlaceId;
    /**
     * 区域或间隔路径
     */

    @ApiModelProperty(value = "区域或间隔路径", dataType = "String")
    private String fromPlacePath;
    /**
     * 辅助设备id
     */

    @ApiModelProperty(value = "辅助设备id", dataType = "Long")
    private Long fromAssistDeviceId;
    /**
     * 辅助设备路径
     */

    @ApiModelProperty(value = "辅助设备路径", dataType = "String")
    private String fromAssistDevicePath;
    /**
     * 主设备id
     */

    @ApiModelProperty(value = "主设备id", dataType = "Long")
    private Long fromParimaryDeivceId;
    /**
     * 主设备路径
     */

    @ApiModelProperty(value = "主设备路径", dataType = "String")
    private String fromParimaryDevicePath;

    @ApiModelProperty(value = "来源类型", dataType = "来源1=区域、间隔，来源2=主设备，来源3=辅助设备")
    private Integer fromType;
    @ApiModelProperty(value = "主副设备或者场地 对应的场地", dataType = "Long")
    private Long placeId;
    @ApiModelProperty(value = "场地路径", dataType = "String")
    private String placePath;
    @ApiModelProperty(value = "场地路径", dataType = "String")
    private String placeNamePath;
    /**
     * 国网18位统一地址编码
     */

    @ApiModelProperty(value = "国网18位统一地址编码", dataType = "String")
    private String eleCode;
//    @TableField(exist = false)
//    @ApiModelProperty(value = "场景下关联的摄像头预置位", dataType = "List")
//    private List<SceneCameraPresetRelaEntity> cameraList;
    @TableField(exist = false)
    @ApiModelProperty(value = "subSceneList", dataType = "List")
    private List<SceneInfoEntity> subSceneList;

    private String posX;

    private String posy;
}
