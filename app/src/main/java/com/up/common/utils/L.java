package com.up.common.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Todo
 * Created by 王剑洪
 * on 2016/6/6.
 */
public class L {

    public static int VERBOSE = 0;
    public static int DEBUG = 1;
    public static int INFO = 2;
    public static int WARN = 3;
    public static int ERROR = 4;
    public static int NOTHING = 5;
    public static int level = VERBOSE;

    /**
     * 获取当前时间
     *
     * @return
     */
    private static String getCurTime() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS", Locale.getDefault()).format(new Date());
    }

    /**
     * verbose日志
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, Object msg) {
        if (level <= VERBOSE) {
            String message;
            if (null != msg) {
                message = msg.toString();
            } else {
                message = "打印内容为空！";
            }
            Log.v(tag, message);
        }
    }


    /**
     * verbose日志
     *
     * @param context
     * @param msg
     */
    public static void v(Context context, Object msg) {
        v(context.getClass().getSimpleName(), msg);
    }

    /**
     * debug日志
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, Object msg) {
        if (level <= DEBUG) {
            String message;
            if (null != msg) {
                message = msg.toString();
            } else {
                message = "打印内容为空！";
            }
            Log.d(tag, message);
        }
    }

    /**
     * debug日志
     *
     * @param context
     * @param msg
     */
    public static void d(Context context, Object msg) {
        d(context.getClass().getSimpleName(), msg);
    }

    /**
     * info日志
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, Object msg) {
        if (level <= INFO) {
            String message;
            if (null != msg) {
                message = msg.toString();
            } else {
                message = "打印内容为空！";
            }

            Log.i(tag, message);
        }
    }

    /**
     * info日志
     *
     * @param context
     * @param msg
     */
    public static void i(Context context, Object msg) {
        i(context.getClass().getSimpleName(), msg);
    }

    /**
     * warn日志
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, Object msg) {
        if (level <= WARN) {
            String message;
            if (null != msg) {
                message = msg.toString();
            } else {
                message = "打印内容为空！";
            }

            Log.w(tag, message);
        }
    }

    /**
     * warn日志
     *
     * @param context
     * @param msg
     */
    public static void w(Context context, Object msg) {
        w(context.getClass().getSimpleName(), msg);
    }

    /**
     * error日志
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, Object msg) {
        if (level <= ERROR) {
            String message;
            if (null != msg) {
                message = msg.toString();
                if (msg instanceof Throwable) {
                    StackTraceElement[] stackArray = ((Throwable) msg).getStackTrace();
                    for (StackTraceElement stack : stackArray) {
                        message += "\n  " + stack.toString();
                    }
                }
            } else {
                message = "打印内容为空！";
            }
            Log.e(tag, message);
        }
    }

    /**
     * error日志
     *
     * @param context
     * @param msg
     */
    public static void e(Context context, Object msg) {
        e(context.getClass().getSimpleName(), msg);
    }


    public static void exc(Exception exc) {
        if (level <= ERROR) {


            e("Exception", exc);
            String errorlog = "errorlog.txt";
            String savePath = "";
            String logFilePath = "";
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                //判断是否挂载了SD卡
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Log/";
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    logFilePath = savePath + errorlog;
                }
                //没有挂载SD卡，无法写文件
                if (logFilePath == "") {
                    return;
                }
                File logFile = new File(logFilePath);
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                fw = new FileWriter(logFile, true);
                pw = new PrintWriter(fw);
                pw.println("--------------------" + (new Date().toString()) + "---------------------");
                exc.printStackTrace(pw);
                pw.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (pw != null) {
                    pw.close();
                }
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
