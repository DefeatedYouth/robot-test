package com.robot.host.common.util;

import org.quartz.CronExpression;

import java.math.BigDecimal;
import java.util.Date;

public class RobotDateUtil {


    /**
     * 计算任务进度
     * 公式：（当前时间 - 任务开始时间） / （任务计划结束时间 - 任务开始时间） * 100（unit:%）
     * @param realTaskTime
     * @param taskEndTime
     * @return
     */
    public static BigDecimal countTaskProgress(Date realTaskTime, Date taskEndTime){
        BigDecimal taskProgress = null;
        if(realTaskTime != null && taskEndTime != null){
            //获取时间戳
            BigDecimal realTime = new BigDecimal(realTaskTime.getTime());
            BigDecimal endTime = new BigDecimal(taskEndTime.getTime());
            BigDecimal currentTime = new BigDecimal(System.currentTimeMillis());
            //计算
            taskProgress = currentTime.subtract(realTime)   //任务执行时长
                    .divide(endTime.subtract(realTime))     //除以 总时长
                    .multiply(new BigDecimal(100))
                    .setScale(0,BigDecimal.ROUND_HALF_UP);//保留小数，四舍五入
        }
        return taskProgress;
    }

    public static Boolean isValidExpression(String cron) {
        return CronExpression.isValidExpression(cron);
    }
}
