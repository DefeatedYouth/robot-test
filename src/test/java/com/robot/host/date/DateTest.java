package com.robot.host.date;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

    private static final String STRING_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Test
    public void dateFormatTest(){
        String date =  "2020-07-10 00:00:00";
//        Date date1 = new Date(date);
        SimpleDateFormat cronSdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        SimpleDateFormat StringSdf = new SimpleDateFormat(STRING_DATE_FORMAT);
        Date format = null;
        String cronDate = null;
        try {
            format = StringSdf.parse(date);
            cronDate = cronSdf.format(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(format);
        System.out.println(cronDate);

    }

    @Test
    public void taskProgressTest() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String format = sdf.format(new Date());
        Date realBeginTime = sdf.parse(format);
        Date taskEndTime = DateUtils.addDays(realBeginTime, 1);
//        long startTime = realBeginTime.getTime();
//        long endTime = taskEndTime.getTime();
//        long currentTime = System.currentTimeMillis();
//        double taskProgress = (currentTime - startTime) / (endTime - startTime) * 1.0;
        BigDecimal beginTime = new BigDecimal(realBeginTime.getTime());
        BigDecimal endTime = new BigDecimal(taskEndTime.getTime());
        BigDecimal currentTime = new BigDecimal(System.currentTimeMillis());
        BigDecimal taskProgress = currentTime.subtract(beginTime).divide(endTime.subtract(beginTime)).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        System.out.println(currentTime.subtract(beginTime));
        System.out.println(endTime.subtract(beginTime));
        System.out.println(taskProgress);
    }
}
