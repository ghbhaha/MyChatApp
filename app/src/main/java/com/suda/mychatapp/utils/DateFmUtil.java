package com.suda.mychatapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Suda on 2015/7/24.
 */
public class DateFmUtil {

    public static String fmDate(Date date){
        DateFormat format = new SimpleDateFormat(SIMPLE_TIME);
        return format.format(date);
    }

    public static String fmDate1(Date date){
        DateFormat format = new SimpleDateFormat(SIMPLE_TIME_1);
        return format.format(date);
    }

    private final static String SIMPLE_TIME_1 = "yyyy-MM-dd";

    private final static String SIMPLE_TIME = "MM-dd HH:mm";
}
