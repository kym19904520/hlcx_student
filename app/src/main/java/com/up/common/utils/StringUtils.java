package com.up.common.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/7/8.
 */

public class StringUtils {
    /**
     * 判断数组里是否每个值都为空
     */
    public static boolean isArrayEmpty(String[] strs){
        for (int i=0;i<strs.length;i++){
            if (!TextUtils.isEmpty(strs[i])){
                return false;
            }
        }
        return true;
    }
    public static void Log(String[] strs){
        String s ="";
        for (int i = 0;i<strs.length;i++){
            s += "index=["+i+"],value="+strs[i]+"-----";
        }
        Logger.i(Logger.TAG,"字符串数组各个值："+s);
    }

    /**
     * 非空的才转换
     */
    public static List<String> ArrayToList(String[] strs){
        List<String> list = new ArrayList<>();
        for (int i = 0;i<strs.length;i++){
            if (!TextUtils.isEmpty(strs[i])){
                list.add(strs[i]);
            }
        }
        return list;
    }

    public static int dip2Px(Context context, int dip) {
        //dp转成px
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + .5f);
        return px;
    }

    public static int px2Dip(Context context,int px) {
        //px转成dp
        float density = context.getResources().getDisplayMetrics().density;

        int dip = (int) (px / density + .5f);
        return dip;
    }
}
