package com.up.common.base;

import android.app.Application;

/**
 * TODO:
 * Created by 王剑洪
 * on 2016/9/21 0021.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;

    public BaseApplication() {
        instance = this;
    }

    public static synchronized BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        registerUncaughtExceptionHandler();
    }
//    // 注册App异常崩溃处理器
//    private void registerUncaughtExceptionHandler() {
//        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
//    }
}
