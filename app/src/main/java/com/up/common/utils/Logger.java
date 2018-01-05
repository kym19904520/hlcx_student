package com.up.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Environment;
import android.util.Log;

/**
 * Log封装，可输出日志到SdCard
 * @author yy_cai
 */
public class Logger {
	public final static String TAG = "okgo";
	private static File file = null;
	private static boolean debug = true;
	
	public static void v(String TAG,String msg){
		if (debug) {
			Log.v(TAG, msg);
		}
	}
	
	public static void i(String TAG,String msg){
		if (debug) {
			Log.i(TAG, msg);
		}
	}
	public static void d(String TAG,String msg){
		if (debug) {
			Log.d(TAG, msg);
		}
	}
	public static void w(String TAG,String msg){
		if (debug) {
			Log.w(TAG, msg);
		}
	}
	public static void e(String TAG,String msg){
		if (debug) {
			Log.e(TAG, msg);
		}
	}
	public static void e(String TAG,String msg,Throwable tr){
		if (debug) {
			Log.e(TAG, msg,tr);
		}
	}
	
	/**
	 * 默认打印log.i日志并输出log到SdCard根目录上的geihoo_log.txt日志文件上
	 */
	public static void iAndFile(String TAG,String msg){
		if (debug) {
			Log.i(TAG, msg);
		}
		write(msg);	
	}
	
	private static void write(String log) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.i("cyy", "SdCard故障");
			return;
		}
		try {
			if (file == null) {
				file = new File(Environment.getExternalStorageDirectory(),"geihoo_log.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
			}

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "gbk"));
			writer.write(log + "\r\n");
			writer.close();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
