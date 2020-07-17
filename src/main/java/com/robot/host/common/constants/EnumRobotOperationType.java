package com.robot.host.common.constants;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: com.face.ele.smart.modules.netty.utils
 * @description: 机器人传输协议的消息类型----操控机器人
 * @author: dadi
 * @create: 2020-05-07 16:17
 */
public enum EnumRobotOperationType {
    //<251>: = 系统消息
  /*  <2>: = 机器人车体
            <1>: = 前进
            <2>: = 后退
            <3>: = 左转
            <4>: = 右转
            <5>: = 转弯
            <6>: = 停止
            <3>: = 机器人云台
            <1>: = 上仰
            <2>: = 下俯
            <3>: = 左转
            <4>: = 右转

    //机器人云台
    /**
     * //            <1>: = 上仰
     * //            <2>: = 下俯
     * //            <3>: = 左转
     * //            <4>: = 右转
     * //            <5>: = 上升
     * //            <6>: = 下降
     * //            <7>: = 预置位调用
     * //            <8>: = 停止
     * //            <9>: = 复位
     */

    ////            <4>: = 机器人辅助
//            设备
//            <1>: = 红外电源
//            <2>: = 雨刷
//            <3>: = 超声
//            <4>: = 红外射灯

    //机器人车体
    qianjin("2-1", 2, 1, "前进"),
    houtui("2-2", 2, 2, "后退"),
    zuozhuan("2-3", 2, 3, "左转"),
    youzhuan("2-4", 2, 4, "右转"),
    zhuanwan("2-5", 2, 5, "转弯"),
    tingzhi("2-6", 2, 6, "停止"),



    //机器人本体
    yijianfanhang("1-3", 1, 3, "一键返航"),
    shoudongchongdian("1-4", 1, 4, "手动充电"),
    KongZhiMoShiQieHuan("1-5", 1, 5, "控制模式切换"),


    shangyang("3-1", 3, 1, "上仰"),
    xiafu("3-2", 3, 2, "下俯"),
    shangsheng("3-3", 3, 3, "上升"),
    xiajiang("3-4", 3, 4, "下降"),
    YunTaiTingZhi("3-8", 3, 8, "云台停止"),
    YunTaiFuWei("3-9", 3, 9, "云台复位"),
    YuZhiWeiTiaoZheng("3-7", 3, 7, "预置位调整"),

    hongwaishedeng("4-4", 4, 4, "红外射灯"),
    YuShua("4-2", 4, 2, "雨刷"),


//可见光摄像


    JingTouLaJin("21-1",21,1,"镜头拉近"),

    JingTouLaYuan("21-2",21,2,"镜头拉远"),

    JingTouTingZhi("21-3",21,3,"镜头拉焦停止"),

    JiaoJuZengJia("21-4",21,4,"焦距增加"),

    JiaoJuJianShao("21-5",21,5,"焦距减少"),

    ZhuaTu("21-7",21,7,"抓图"),
    QiDongLuXiang("21-9",21,9,"启动录像"),
    TingZhiLuXiang("21-10",21,10,"停止录像"),

    BeiLvSheZhi("21-11",21,11,"倍率值设置"),

    JiaoJuSheZhi("21-12",21,12,"聚焦值设置"),

    // 红外热像仪
//            红外热像仪
//            <5>: = 设定焦距值
//            <6>: = 自动聚焦
    SheDingJiaoJu("22-5",22,5,"设定焦距值"),
    HongWaiZhuaTu("22-7",22,7,"抓图"),

    ZiDongJiaoJu("22-6",22,6,"自动聚焦");


    private String fullCode;//大类加子类
    private Integer command;//子类
    private Integer type;//大类
    private String text;

    private static Map<String, EnumRobotOperationType> pool = new HashMap<String, EnumRobotOperationType>();

    static {
        for (EnumRobotOperationType each : EnumRobotOperationType.values()) {
            EnumRobotOperationType defined = pool.get(each.getFullCode());
            if (null != defined) {
                pool.put(null, null);
            }
            pool.put(each.fullCode, each);
        }
    }


    EnumRobotOperationType(String code, Integer type, Integer command, String message) {
        this.fullCode = code;
        this.type = type;
        this.command = command;
        this.text = message;
    }

    public static EnumRobotOperationType getEnum(String code) {
        return pool.get(code);
    }


    public String getText() {
        return text;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fullCode", fullCode);
        jsonObject.put("command", command);
        jsonObject.put("type", type);
        jsonObject.put("text", text);
        return jsonObject.toString();
    }
}
