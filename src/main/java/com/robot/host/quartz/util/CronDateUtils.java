package com.robot.host.quartz.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CronDateUtils {

    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

    private static final String STRING_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * date --> cron
     *
     */
    public static String getCron(String date){
        SimpleDateFormat cronSdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        SimpleDateFormat stringSdf = new SimpleDateFormat(STRING_DATE_FORMAT);
        String formatTimeStr = "";
        try {
            if(StringUtils.isNotBlank(date)){
                Date parse = stringSdf.parse(date);
                formatTimeStr = cronSdf.format(parse);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatTimeStr;
    }
}
