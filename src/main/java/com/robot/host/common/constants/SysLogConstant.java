package com.robot.host.common.constants;

import java.text.SimpleDateFormat;

public interface SysLogConstant {

    //日志内容状态 0 ERROR 1、2 INFO
    String SYS_CONTENT_SUCCESS_STATUS = "INFO";

    String SYS_CONTENT_ERROR_STATUS = "ERROR";
    //日志状态 0 异常  1 输入  2  输出  3 本地
    int SYS_ERROR_STATUS = 0;
    int SYS_INPUT_STATUS = 1;
    int SYS_OUTPUT_STATUS = 2;
    int SYS_LOCAL_STATUS = 3;

    //日志类型
    int ROBOT_ROBOT_OPERATION = 1;
    int ROBOT_PATROL_TASK = 2;
    int ROBOT_OTHER = 3;


    //日志时间格式
    SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

    //日志前缀： 时间 + 状态 + 类名
    String SYS_LOG_PREFIX = "%s  %s  %s  :";


    //巡检主机  to 机器人主机
    String OUT_INPUT_MESSAGE = "[机器人主机---%s]接受消息：%s";
    //机器人主机  to  巡检主机
    String IN_OUTPUT_MESSAGE = "[机器人主机---%s]发送消息：%s";
    String IN_OUTPUT_RESP_MESSAGE = "[机器人主机---%s]响应信息：%s";

    //机器人主机  联动任务
    String ROBOT_TASK_START = "[%s]%s:开始执行任务,任务编码为:%s";
    String ROBOT_TASK_END = "[%s]%s:任务执行结束，任务编码为:%s";
    String ROBOT_TASK_ING = "[%s]%s:正在巡检%s";

    //巡检任务
    String ROBOT_PATROL_TASK_START = "[%s]开始执行任务，jobId为：%s";
    String ROBOT_PATROL_TASK_END = "[%s]任务执行结束，jobId为：%s";
    String ROBOT_PATROL_TASK_ING = "[%s]%s:正在巡检%s";

    //机器人控制
    String ROBOT_OPERATION_COORDINATE = "%s坐标点位发生改变，当前点位为： %s,%s";
}
