package com.up.common.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by dell on 2017/7/2.
 */

public class GsonUtils {

    /**
     * string json数组转化为list
     * @param jsonArray
     */
    public static List<String> toList(String jsonArray){
        if (TextUtils.isEmpty(jsonArray)){
            return null;
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> list = new Gson().fromJson(jsonArray, type);
        return list;
    }
}
