package com.robot.host.common.constants;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.face.ele.smart.modules.netty.utils
 * @description: 在机器人状态发生变化时，机器人主机主动向巡视主机上报状态数据并接收响应消息
 * @author: dadi
 * @create: 2020-05-07 16:17
 */
@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumRobotComplusStatusDataType {
    /**
     <1>: = 电池电量低 <0>: = 正常
                          <1>: = 低
     <2>: = 通信状态异常 <0>: = 正常
                      <1>: = 异常
     <3>: = 超声停障 <0>: = 正常
                    <1>: = 停障
     <4>: = 驱动异常 <0>: = 正常
                  <1>: = 异常
     <21>: = 故障报警 <0>: = 正常
                      <1>: = 报警
     <41>: = 运行状态 <1>: = 空闲状态
                     <2>: = 巡视状态
                     <3>: = 充电状态
                     <4>: = 检修状态
     <61>: = 控制模式 <1>: = 任务模式
                 <2>: = 紧急定位
                 模式
                 <3>: = 后台遥控
                 模式
                 <4>: = 手持遥控
                 模式
     <81>: = 控制权状态 <0>: = 空闲
                     <1>: = 获得
     <101>: = 轮转状态 <0>: = 空闲
                     <1>: = 值班
     */
    DianChiDianLiangDi_ZhengChang("1-0", 1, 0, "电池电量低", "正常"),
    DianChiDianLiangDi_Di("1-1", 1, 1, "电池电量低", "低"),
    ChaoShengTingZhang_TingZhang("3-1", 3, 1, "超声停障", "停障"),
    ChaoShengTingZhang_ZhengChang("3-0", 3, 1, "超声停障", "正常"),
    QuDongYiChang_ZhengChang("4-0", 4, 0, "驱动异常", "正常"),
    QuDongYiChang_YiChang("4-1", 4, 1, "驱动异常", "异常"),

    KongZhiMoShi_RenWuMoShi("61-1", 61, 1, "控制模式", "任务模式"),
    KongZhiMoShi_JinJiDingWei("61-2", 61, 2, "控制模式", "紧急定位"),
    KongZhiMoShi_HouTaiYaoKong("61-3", 61, 3, "控制模式", "后台遥控"),
    KongZhiMoShi_ShouChiYaoKong("61-4", 61, 4, "控制模式", "手持遥控"),


    YunXingZhuangTai_KongXian("41-1", 41, 1, "运行状态", "空闲状态"),
    YunXingZhuangTai_XunShi("41-2", 41, 2, "运行状态", "巡视状态"),
    YunXingZhuangTai_ChongDian("41-3", 41, 3, "运行状态", "充电状态"),
    YunXingZhuangTai_JianXiu("41-4", 41, 4, "运行状态", "检修状态");


    private String fullCode;//大类加子类
    private Integer type;//类型
    private Integer value;//值
    private String typeName;
    private String valueName;

    private static Map<String, EnumRobotComplusStatusDataType> pool = new HashMap<String, EnumRobotComplusStatusDataType>();

    static {
        for (EnumRobotComplusStatusDataType each : EnumRobotComplusStatusDataType.values()) {
            EnumRobotComplusStatusDataType defined = pool.get(each.getFullCode());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.fullCode, each);
        }
    }


    EnumRobotComplusStatusDataType(String code, Integer type, Integer value, String typeName, String valueName) {
        this.fullCode = code;
        this.type = type;
        this.value = value;
        this.typeName = typeName;
        this.valueName = valueName;
    }

    public static EnumRobotComplusStatusDataType getEnum(String code) {
        return pool.get(code);
    }
    public static EnumRobotComplusStatusDataType getEnum(String type, String value) {
        return getEnum(type.toString()+"-"+value.toString());
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
