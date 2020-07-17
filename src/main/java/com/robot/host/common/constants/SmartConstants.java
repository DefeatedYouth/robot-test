package com.robot.host.common.constants;

public interface SmartConstants {

    //一个巡检任务中所有的场景信息  key 值：
    public static final String redis_key_patrol_task_robot_set ="patrolTaskRobotSet:";

    //巡检定时任务
    String patrol_task_bean_name = "patrolTask";

    String CRON_SPACE = " ";

    String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

}
