package com.robot.host.common.constants;

import com.google.common.collect.Sets;

import java.text.SimpleDateFormat;
import java.util.Set;

public interface NettyConstants {

    //文件时间格  年月日时分秒
    SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    int SESSION_ID_LENGTH = 8;
    byte[] magicNumber = new byte[]{(byte) 0xEB, (byte) 0x90};

    int RESPONSE_CODE_SUCCESS = 200;
    int RESPONSE_CODE_ERROR = 500;

    /**
     * 机器人主机操作日志
     */
    int ROBOT_OPERATION = 1;//机器人控制

    int PATROL_TASK = 2;//巡检任务
    /**
     * 机器人状态
     */
    int ROBOT_FREE = 1;
    int ROBOT_PATROL = 2;
    int ROBOT_CHARGE = 3;
    int ROBOT_FAULT = 4;

    /**
     * 联动任务响应code
     */
    int TASK_LINK_SUCCESS = 0;
    int TASK_LINK_ROBOT_FAULT = 1;
    int TASK_LINK_NO_PERMISSION = 2;
    int TASK_LINK_OTHER_ERROR = 3;


    String PATROL_HOST_CODE = "Server01";//巡视主机
    String MASTER_HOST_CODE = "Master001";//主站主机
    String ROBOT_HOST_CODE = "Client01";//机器人主机

    /**
     * 机器人主机to 巡检主机
     */
    int IN_OUT_CODE_SYSTEM_TYPE = 251;//机器人发送给巡检主机的系统消息  返程也是这个code

    int IN_CODE_SYSTEM_TYPE_COMMAND_REG = 1;//系统消息--注册 指令

    int IN_CODE_STATUS = 1;//机器人发送给巡检主机的状态数据

    int IN_CODE_RUN = 2;//机器人发送给巡检主机的运行数据

    int IN_CODE_RUN_SPEED = 1;//运行数据：速度

    int IN_CODE_RUN_MILEAGE = 2;//运行数据：总里程

    int IN_CODE_RUN_QUANTITY = 3;//运行数据： 电量

    int IN_CODE_WEATHER = 21;//机器人发送给巡检主机的气象数据

    int IN_CODE_WEATHER_TEMP = 1;//气象数据： 温度

    int IN_CODE_WEATHER_HUMIDITY = 2;//气象数据：湿度

    int IN_CODE_WEATHER_WIND_SPEED = 3;//气象数据：风速

    int IN_CODE_Coordinate = 3;//机器人发送给巡检主机的坐标信息
    int IN_CODE_PATROL_ROUTE = 4;//机器人发送给巡检主机的巡视路线
    int IN_CODE_ABORMAL_WARN = 5;//机器人发送给巡检主机的异常告警
    int IN_CODE_TASK_STATUS = 41;//机器人发送给巡检主机的任务状态
    int IN_CODE_TASK_RESULT = 61;//机器人发送给巡检主机巡视结果


    /**
     * 巡检主机to 主站
     */
    int IN_CODE_SYSTEM_TYPE_COMMAND_HB = 2;//系统消息--响应心跳指令

    /**
     * 巡检主机to 机器人主机
     */
    int OUT_CODE_SYSTEM_TYPE_COMMAND_HB = 3;//系统消息--响应心跳指令
    int OUT_CODE_SYSTEM_TYPE_COMMAND_REG = 4;//系统消息--响应注册指令
    int OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_NO_ITEMS = 3;//系统消息--通用相应无item
    int OUT_CODE_SYSTEM_TYPE_COMMAND_COMMON_ITEMS = 4;//--通用相应有item
    int OUT_CODE_TASK_CONTROL = 41;//任务控制
    int OUT_CODE_TASK_CONTROL_COMMAND_START = 1;//任务控制-任务启动
    int OUT_CODE_TASK_CONTROL_COMMAND_PAUSE = 2;//任务控制-任务暂停
    int OUT_CODE_TASK_CONTROL_COMMAND_CONTINUE = 3;//任务控制-任务继续
    int OUT_CODE_TASK_CONTROL_COMMAND_STOP = 4;//任务控制-任务停止

    int OUT_CODE_TASK_DOWN = 101;//任务下发
    int OUT_CODE_TASK_DOWN_COMMAND = 1;//任务下发-任务配置


    int OUT_CODE_TASK_DOWN_INSTRUCT = 102;//紧急任务下发
    int OUT_CODE_TASK_DOWN_INSTRUCT_COMMAND = 1;//紧急任务下发-任务配置

    int OUT_CODE_MODEL_SYN = 61;//模型同步
    int OUT_CODE_MODEL_SYN_COMMAND = 1;//模型同步-任务配置



    Set<Integer> ROBOT_send_instructions_type = Sets.newHashSet(1, 2, 3, 4, 21, 22);//发送指令


    String LOG_CONTENT_OUT_COMMON_NO_ITEM = "通用机器人主机应答无内容";
    String LOG_CONTENT_OUT_COMMON_ITEM = "通用机器人主机应答有内容";


    //发送给主站的心跳时间的key
    String  REDIS_HEART_BEAT_SECOND_KEY = "REDIS_HEART_BEAT_SECOND_KEY";
    //主站返回的注册内容
    String  REDIS_REGIST_RESP_KEY = "REDIS_REGIST_RESP_KEY";
    String  REDIS_REGIST_SESSIONID_KEY = "REDIS_REGIST_SESSIONID_KEY";
}
