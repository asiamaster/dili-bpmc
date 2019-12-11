package com.dili.bpmc.sdk.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author: WangMi
 * @Date: 2019/12/3 15:17
 * @Description:
 */
public class DateUtils {
    /**
     * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
     * @param date
     * @return
     */
    public static String getISO8601TimeDate(Date date){
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        //TimeZone tz = TimeZone.getTimeZone("GMT+:08:00");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");//输出格式:2019-11-10T10:13:47.861Z
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }
}
