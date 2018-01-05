package com.up.study.weight.camera;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.up.common.utils.ImageUtil;
import com.up.common.utils.Logger;
import com.up.study.R;
import com.up.study.ui.home.SearchResultActivity;
import com.up.study.weight.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @Class: TakePhotoActivity----拍照界面
 */
public class TakePhotoActivity extends Activity implements CameraPreview.OnCameraStatusListener,
        SensorEventListener {
    private static final String TAG = "TakePhotoActivity";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String PATH = Environment.getExternalStorageDirectory()
            .toString() + "/AndroidMedia/";
    private CameraPreview mCameraPreview;
    private CropImageView mCropImageView;
    private RelativeLayout mTakePhotoLayout;
    private LinearLayout mCropperLayout;
    private boolean isRotated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_phote);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        FocusView focusView = (FocusView) findViewById(R.id.view_focus);
        mTakePhotoLayout = (RelativeLayout) findViewById(R.id.take_photo_layout);
        mCropperLayout = (LinearLayout) findViewById(R.id.cropper_layout);

        mCameraPreview.setFocusView(focusView);
        mCameraPreview.setOnCameraStatusListener(this);
        mCropImageView.setGuidelines(2);

        mSensorManager = (SensorManager) getSystemService(Context.
                SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRotated) {
            TextView hint_tv = (TextView) findViewById(R.id.hint);
            ObjectAnimator animator = ObjectAnimator.ofFloat(hint_tv, "rotation", 0f, 90f);
            animator.setStartDelay(800);
            animator.setDuration(1000);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
            View view = findViewById(R.id.crop_hint);
            AnimatorSet animSet = new AnimatorSet();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "rotation", 0f, 90f);
            ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", 0f, -50f);
            animSet.play(animator1).before(moveIn);
            animSet.setDuration(10);
            animSet.start();
            isRotated = true;
        }
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    public void takePhoto(View view) {
        if (mCameraPreview != null) {
            mCameraPreview.takePicture();
        }
    }

    public void close(View view) {
        finish();
    }

    /**
     * 关闭截图界面
     *
     * @param view
     */
    public void closeCropper(View view) {
        showTakePhotoLayout();
    }

    /**
     * 开始截图，并保存图片
     *
     * @param view
     */
    public void startCropper(View view) {
        Logger.i(Logger.TAG, "startCropper");
        //获取截图并旋转90度
        CropperImage cropperImage = mCropImageView.getCroppedImage();
        Log.e(TAG, cropperImage.getX() + "," + cropperImage.getY());
        Log.e(TAG, cropperImage.getWidth() + "," + cropperImage.getHeight());
        Bitmap bitmap = cropperImage.getBitmap();
        bitmap = Utils.rotate(bitmap, -90);
        String imgPath = ImageUtil.saveImg(bitmap);
        Logger.i(Logger.TAG, "OCR处理===================================");
        //OCR处理
        /*try {
            String path = "/mnt/sdcard/tesseract/tessdata/chi_sim.traineddata";
            File file = new File(path);
            //判断文件夹是否存在,如果不存在则创建文件夹
            if (!file.exists()) {
                Logger.i(Logger.TAG,"OCR语言包不存在，正在拷贝===================================");
                Toast.makeText(this,"OCR语言包不存在，正在拷贝",Toast.LENGTH_SHORT).show();
                OcrUtils.copyBigDataToSD(this,path);
            }
            else{
                Logger.i(Logger.TAG,"OCR语言包已存在===================================");
                Toast.makeText(this,"OCR语言包已存在",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("imgPath",imgPath);
        startActivity(intent);
        bitmap.recycle();
        finish();
        super.overridePendingTransition(R.anim.fade_in,
                R.anim.fade_out);
    }

    /**
     * 拍照成功后回调
     * 存储图片并显示截图界面
     *
     * @param data
     */
    @Override
    public void onCameraStopped(byte[] data) {
        Log.i("TAG", "==onCameraStopped==");
        // 创建图像
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 系统时间
        long dateTaken = System.currentTimeMillis();
        // 图像名称
        String filename = DateFormat.format("yyyy-MM-dd kk.mm.ss", dateTaken)
                .toString() + ".jpg";
        // 存储图像（PATH目录）
        Uri source = insertImage(getContentResolver(), filename, dateTaken, PATH,
                filename, bitmap, data);
        //准备截图
        try {
            mCropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), source));
//            mCropImageView.rotateImage(90);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        showCropperLayout();
    }

    public void selectPhoto(View view) {
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)
                .setEnableEdit(false)
                .setEnableCrop(false)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();
        GalleryFinal.openGallerySingle(1, functionConfig, mOnHanlderResultCallback);
    }

    private void cropImage(Bitmap bitmap) {
        //准备截图
        mCropImageView.setImageBitmap(bitmap);
//      mCropImageView.rotateImage(90);
        showCropperLayout();
    }

    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            Logger.i(Logger.TAG, "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            Logger.i(Logger.TAG, "选择照片成功：reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            if (resultList.size() == 1) {
                Logger.i(Logger.TAG, "照片地址：" + resultList.get(0).getPhotoPath());
                File file = new File(resultList.get(0).getPhotoPath());
                Bitmap bitmap1 = BitmapFactory.decodeFile(resultList.get(0).getPhotoPath());
                Logger.i(Logger.TAG, "文件大小：" + file.length() + "");
                //如果图片大小大于200kb 进行压缩
                /*if (file.length() > 200 * 1024) {
                    Bitmap bitmap2 = ImageUtil.compressIimg(resultList.get(0).getPhotoPath());
                    if (bitmap2==null){
                        Toast.makeText(TakePhotoActivity.this,"图片压缩失败",Toast.LENGTH_SHORT).show();
                        cropImage(bitmap1);
                    }
                    else{
                        Logger.i(Logger.TAG,"图片压缩成功");
                        cropImage(bitmap2);
                    }
                }
                else{
                    int sourceWidth = bitmap1.getWidth();
                    int sourceHeight = bitmap1.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    // 当进行的不只是平移操作的时候，最后的参数为true，可以进行滤波处理，有助于改善新图像质量
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap1, 0, 0, sourceWidth, sourceHeight, matrix, true);
                    cropImage(newBitmap);
                }
                //为了提高识别率，不压缩
                //不做90度旋转，存储的时候已经旋转了
                int sourceWidth = bitmap1.getWidth();
                int sourceHeight = bitmap1.getHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                // 当进行的不只是平移操作的时候，最后的参数为true，可以进行滤波处理，有助于改善新图像质量
                Bitmap newBitmap = Bitmap.createBitmap(bitmap1, 0, 0, sourceWidth, sourceHeight, matrix, true);*/
                cropImage(bitmap1);
            } else {
                Logger.i(Logger.TAG, "照片选多了");
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(TakePhotoActivity.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private Uri insertImage(ContentResolver cr, String name, long dateTaken,
                            String directory, String filename, Bitmap source, byte[] jpegData) {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(jpegData);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        return cr.insert(IMAGE_URI, values);
    }

    private void showTakePhotoLayout() {
        mTakePhotoLayout.setVisibility(View.VISIBLE);
        mCropperLayout.setVisibility(View.GONE);
    }

    private void showCropperLayout() {
        mTakePhotoLayout.setVisibility(View.GONE);
        mCropperLayout.setVisibility(View.VISIBLE);
        mCameraPreview.start();   //继续启动摄像头
    }


    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private boolean mInitialized = false;
    private SensorManager mSensorManager;
    private Sensor mAccel;

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > 0.8 || deltaY > 0.8 || deltaZ > 0.8) {
            mCameraPreview.setFocus();
        }
        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
