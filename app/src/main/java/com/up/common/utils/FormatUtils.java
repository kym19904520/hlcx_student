package com.up.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/5/17.
 */

public class FormatUtils {

    public static boolean phone(Context context, String phone){
        if (TextUtils.isEmpty(phone)|| !PhoneUtils.is(phone)){
            Toast.makeText(context,"请输入正确的手机号码！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean register(Context context, String phone,String password,String passwordAgain,String code){
        if (TextUtils.isEmpty(phone)|| !PhoneUtils.is(phone)){
            Toast.makeText(context,"请输入正确的手机号码！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(context,"请输入密码！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length()<8||password.length()>16){
            Toast.makeText(context,"8-16位的大小写字母、数字、字符的组合！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(passwordAgain)){
            Toast.makeText(context,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(code)){
            Toast.makeText(context,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 将list里的数据用逗号隔开，生成字符串
     * @return
     */
    public static String List2String(List<String> list){
        String str = "";
        if (list==null){
            return str;
        }
        for (int i = 0;i<list.size();i++){
            str += list.get(i)+",";
        }
        if (str.length()>0){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }

    public static String double1(double str) {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(str);
    }

}
