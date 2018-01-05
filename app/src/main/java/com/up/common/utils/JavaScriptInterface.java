package com.up.common.utils;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

/**
 * Created by dell on 2017/7/26.
 */

public class JavaScriptInterface {

    private Context context;

    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    //点击图片回调方法
    //必须添加注解,否则无法响应
    @JavascriptInterface
    public void openImage(String img) {
        Logger.i(Logger.TAG, "响应点击事件!------"+img);
        /*Intent intent = new Intent();
        intent.putExtra("image", img);
        intent.setClass(context, BigImageActivity.class);//BigImageActivity查看大图的类，自己定义就好
        context.startActivity(intent);*/
    }
}
