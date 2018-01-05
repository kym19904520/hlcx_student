package com.up.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/6/28.
 */

public class DateUtils {
    /**
     *
     * @param num 把日期往后增加一天.整数往后推,负数往前移动
     * @return
     */
    public static String getDateFormat(int num) {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,num);//
        date = calendar.getTime(); //这个时间就是日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String today = formatter.format(date);
        return today;
    }
}
