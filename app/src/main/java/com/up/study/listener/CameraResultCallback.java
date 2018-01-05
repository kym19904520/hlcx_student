package com.up.study.listener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.up.common.utils.Logger;
import com.up.study.ui.home.DoHomeworkActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by dell on 2017/6/19.
 */

public class CameraResultCallback implements GalleryFinal.OnHanlderResultCallback{
    ImageView iv;
    CameraSucCallBack sucCallBack;
    public CameraResultCallback(ImageView iv,CameraSucCallBack suc){
        this.iv = iv;
        this.sucCallBack = suc;
    }
    @Override
    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
        Logger.i(Logger.TAG, "选择照片成功：reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
        if(resultList.size()==1){
            Logger.i(Logger.TAG, "照片地址："+resultList.get(0).getPhotoPath());
            File file = new File(resultList.get(0).getPhotoPath());
            Bitmap bitmap1 = BitmapFactory.decodeFile(resultList.get(0).getPhotoPath());
            Logger.i(Logger.TAG,"文件大小："+file.length() + "");
            //如果图片大小大于200kb 进行压缩
            if (file.length() > 200 * 1024) {
                Logger.i(Logger.TAG,"文件大于200K，开始压缩");
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(resultList.get(0).getPhotoPath(), option);
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
                Bitmap bitmap = BitmapFactory.decodeFile(resultList.get(0).getPhotoPath(), option);
                int degree = 0;
                //图片属性
                try {
                    ExifInterface exifInterface = new ExifInterface(new File(resultList.get(0).getPhotoPath()).getAbsolutePath());
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
                    //matrix.postRotate(90);
                    // 当进行的不只是平移操作的时候，最后的参数为true，可以进行滤波处理，有助于改善新图像质量
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
                   /* String savePath = Environment.getExternalStorageDirectory().getPath() + File.separator+ "study"+File.separator+UUID.randomUUID()+".png";
//                    File resultFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "study", UUID.randomUUID() + ".png");
                    File resultFile = new File(savePath);
                    try {
                        FileOutputStream fos = new FileOutputStream(resultFile);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    File appDir = new File(Environment.getExternalStorageDirectory(), "study");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".png";
                    File resultFile = new File(appDir, fileName);
                    try {
                        FileOutputStream fos = new FileOutputStream(resultFile);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Logger.i(Logger.TAG,resultFile.length() + "");
                    iv.setImageBitmap(newBitmap);
                    String resultPath = appDir+ File.separator+ fileName;
                    Logger.i(Logger.TAG, "经过压缩后的图片地址："+resultPath);
                    sucCallBack.success(resultPath);
                }
            }
            else{
                iv.setImageBitmap(bitmap1);
                Logger.i(Logger.TAG, "未经过压缩的图片地址："+resultList.get(0).getPhotoPath());
                sucCallBack.success(resultList.get(0).getPhotoPath());
            }
        }
        else {
            Logger.i(Logger.TAG, "照片选多了");
        }
    }
    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {
        Logger.i(Logger.TAG, "选择图片失败：requestCode: " + requestCode + "  " + errorMsg);

    }
    public interface CameraSucCallBack{
        void success(String filePath);
    }
}
