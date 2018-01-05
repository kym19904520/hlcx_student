package com.up.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class ImageUtil {

	/*
	 * 以最省内存的方式读取本地资源的图片
	 *
	 * @param context
	 *
	 * @param resId
	 *
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 缩放图片
	 *
	 * @param context
	 * @param bitmap
	 * @param size
	 * @return
	 */
	public static Bitmap postScale(Context context, Bitmap bitmap, int size) {
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		int dimen = (int) context.getResources().getDimension(size);// 缩放图片
		float scaleWidth = ((float) dimen) / bitmapWidth;
		float scaleHeight = ((float) dimen) / bitmapHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
				bitmapHeight, matrix, true);

		return newbm;
	}

	/**
	 * 通过id获取图片并缩放
	 *
	 * @param context
	 *            所在的activity
	 * @param resId
	 *            图片id
	 * @param size
	 *            缩放尺寸
	 * @return
	 */
	public static Bitmap getImageById(Context context, int resId, int size) {
		return postScale(context, readBitMap(context, resId), size);
	}

	/**
	 * 图片压缩
	 * @return
	 */
    public static Bitmap compressIimg(String photoPath){
        Logger.i(Logger.TAG,"文件大于200K，开始压缩");
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, option);
        final int height = option.outHeight;
        final int width = option.outWidth;
        int ratio = 1;
        if (height > 1280 || width > 300) {
            final int heightRatio = Math.round((float) height / (float) 1280);
            final int widthRation = Math.round((float) width / (float) 720);
            ratio = heightRatio > widthRation ? heightRatio : widthRation;
        }
        option.inSampleSize = ratio;
        option.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, option);
        int degree = 0;
        //图片属性
        try {
            ExifInterface exifInterface = new ExifInterface(new File(photoPath).getAbsolutePath());
            int oreentaton = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (oreentaton) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (degree == 0) {
            int sourceWidth = bitmap.getWidth();
            int sourceHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            // 当进行的不只是平移操作的时候，最后的参数为true，可以进行滤波处理，有助于改善新图像质量
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);

            //对图片进行存储
            /*File resultFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "study", UUID.randomUUID() + ".png");
            try {
                FileOutputStream fos = new FileOutputStream(resultFile);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Logger.i(Logger.TAG,resultFile.length() + "");*/
            return newBitmap;
        }
        return  null;
    }

	/*public static Bitmap compress(String photoPath){
        Logger.i(Logger.TAG,"文件大于200K，开始压缩");
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, option);
        final int height = option.outHeight;
        final int width = option.outWidth;
        int ratio = 1;
        if (height > 1280 || width > 300) {
            final int heightRatio = Math.round((float) height / (float) 1280);
            final int widthRation = Math.round((float) width / (float) 720);
            ratio = heightRatio > widthRation ? heightRatio : widthRation;
        }
        option.inSampleSize = ratio;
        option.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, option);
        int degree = 0;
        //图片属性
        try {
            ExifInterface exifInterface = new ExifInterface(new File(photoPath).getAbsolutePath());
            int oreentaton = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (oreentaton) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (degree == 0) {
            int sourceWidth = bitmap.getWidth();
            int sourceHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            // 当进行的不只是平移操作的时候，最后的参数为true，可以进行滤波处理，有助于改善新图像质量
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);

            //对图片进行存储
            *//*File resultFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "study", UUID.randomUUID() + ".png");
            try {
                FileOutputStream fos = new FileOutputStream(resultFile);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Logger.i(Logger.TAG,resultFile.length() + "");*//*
            return newBitmap;
        }
        return  null;
	}*/

    /**
     * 存储图片
     * @param bitmap
     * @return
     */
	public static String saveImg(Bitmap bitmap){
        File appDir = new File(Environment.getExternalStorageDirectory(), "study");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File resultFile = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultPath = appDir+ File.separator+ fileName;
        Logger.i(Logger.TAG, "经过保存后的图片地址："+resultPath);
        return resultPath;
    }
}
