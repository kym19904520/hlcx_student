package com.up.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/5/17.
 */

public class PhoneUtils {
    /**
     * 是否是正确的手机号码
     * @param phone
     * @return
     */
    public static boolean is(String phone) {
        String phoneRegular = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(phoneRegular);
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
