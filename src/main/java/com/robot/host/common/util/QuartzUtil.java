package com.robot.host.common.util;

import cn.hutool.core.date.DateUtil;
import com.robot.host.common.constants.EnumTaskPeriodType;
import com.robot.host.common.constants.SmartConstants;

public class QuartzUtil {

    /**
     * 根据执行周期类型和周期值、执行时间获取cron表达式
     *
     * @param result
     * @param periodType
     * @param periodVal
     * @param periodExecuteTime
     * @param fixedTime
     * @return
     */
    public static String getCronExpression(StringBuffer result, Integer periodType, String periodVal, String periodExecuteTime, String fixedTime) {
        StringBuilder cronExpression = new StringBuilder(56);
        if (EnumTaskPeriodType.DAY.getValue() == periodType) {
            //按天间隔
            String[] periodExecuteTimeArr = periodExecuteTime.split(":");
            //时：分：秒
            if (periodExecuteTimeArr.length != 3) {
                result.append("执行时间格式异常");
                return null;
            }
            //秒 分 时
            cronExpression.append(periodExecuteTimeArr[2]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[1]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[0]).append(SmartConstants.CRON_SPACE)
                    .append("*/1").append(SmartConstants.CRON_SPACE)
                    .append("*").append(SmartConstants.CRON_SPACE)
                    .append("?").append(SmartConstants.CRON_SPACE);

        } else if (EnumTaskPeriodType.WEEK.getValue() == periodType) {
            //按天间隔
            String[] periodExecuteTimeArr = periodExecuteTime.split(":");
            //时：分：秒
            if (periodExecuteTimeArr.length != 3) {
                result.append("执行时间格式异常");
                return null;
            }
            //周
            String[] weekArr = periodVal.split(",");
            if (weekArr == null || weekArr.length < 1) {
                result.append("按周间隔，每周的执行日格式异常");
                return null;
            }
            //秒 分 时
            cronExpression.append(periodExecuteTimeArr[2]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[1]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[0]).append(SmartConstants.CRON_SPACE)
                    .append("?").append(SmartConstants.CRON_SPACE)
                    .append("*").append(SmartConstants.CRON_SPACE)
                    .append(periodVal);

        } else if (EnumTaskPeriodType.MONTH.getValue() == periodType) {
            //按天间隔
            String[] periodExecuteTimeArr = periodExecuteTime.split(":");
            //时：分：秒
            if (periodExecuteTimeArr.length != 3) {
                result.append("执行时间格式异常");
                return null;
            }
            String[] monthArr = periodVal.split(",");
            if (monthArr == null || monthArr.length < 1) {
                result.append("按月间隔，每月的执行日格式异常");
                return null;
            }
            cronExpression.append(periodExecuteTimeArr[2]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[1]).append(SmartConstants.CRON_SPACE)
                    .append(periodExecuteTimeArr[0]).append(SmartConstants.CRON_SPACE)
                    .append(periodVal).append(SmartConstants.CRON_SPACE).append("*").append(SmartConstants.CRON_SPACE)
                    .append("?");
        } else {
            return DateUtil.format(DateUtil.parseDateTime(fixedTime), SmartConstants.CRON_DATE_FORMAT);
        }
        String cron = cronExpression.toString();
        if (!RobotDateUtil.isValidExpression(cron)) {
            result.append("任务时间设置异常");
            return null;
        }
        return cron;
    }
}
