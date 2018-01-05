package com.up.study.weight.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.up.common.conf.Urls;
import com.up.common.utils.Logger;
import com.up.common.widget.LoadingDialog;
import com.up.study.R;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @Class: ShowCropperedActivity
 * @Description: 显示截图结果界面
 * @author: lling(www.cnblogs.com/liuling)
 * @Date: 2015/10/25
 */
public class ShowCropperedActivity extends Activity {
    ImageView imageView;
    protected LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_croppered);
        imageView = (ImageView) findViewById(R.id.image);
        String path = getIntent().getStringExtra("imgPath");
        Bitmap bm = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bm);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setText("正在寻找相近的题目，请稍后...");
        loadingDialog.show();

        uploadImg(path);
        //new ParseImageAsync(bm).execute("");
    }

    private void uploadImg(String filePath){
        Logger.i(Logger.TAG, "url="+Urls.IMG_SEARCH_SEQ);
        OkGo.post(Urls.IMG_SEARCH_SEQ)//
                .tag(this)//
                // .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("file", new File(filePath))   // 可以添加文件上传
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Logger.i(Logger.TAG, s);
                        //上传成功
                        /*Logger.i(Logger.TAG, "图片上传到阿里云成功！图片地址：" + reqImgUrl.getReadhost() + "/" + key);*/
                        Logger.i(Logger.TAG, response.message());
//                        imgCallBack.suc("http://" + reqImgUrl.getReadhost() + "/" + key);
                        //未返回图片地址

                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        Logger.i(Logger.TAG, currentSize+"===========================");
                    }
                });
    }
    /**
     * 异步任务，识别图片
     *
     * @author duanbokan
     *
     */
    /*public class ParseImageAsync extends AsyncTask<String, Integer, String> {
        Bitmap bitmap;
        public ParseImageAsync(Bitmap bitmap){
            this.bitmap = bitmap;
        }
        @Override
        protected void onPreExecute()
        {
            //txtView.setText("正在识别，请稍等");
            Logger.i(Logger.TAG,"OCR正在识别，请稍等===================================");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String result = "";

            try
            {
                TessBaseAPI mTess = OcrUtils.getTassBase();
                mTess.setImage(bitmap);
                result = mTess.getUTF8Text();
                // result = ParseImage.getInstance().parseImageToString(params[0]);
            }
            catch (Exception e)
            {
                result = "";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (null != loadingDialog) {
                loadingDialog.dismiss();
            }
            if (result != null && !result.equals(""))
            {
                Logger.i(Logger.TAG,"OCR识别完毕，结果为： \n" + result);
                if (TextUtils.isEmpty(result)){
                    Toast.makeText(ShowCropperedActivity.this,"未识别出文字,请重新拍照",Toast.LENGTH_SHORT).show();
                    ShowCropperedActivity.this.finish();
                }
                else{
                    Intent intent = new Intent();
                    intent.setClass(ShowCropperedActivity.this, SearchResultActivity.class);
                    intent.putExtra("search",result);
                    startActivity(intent);
                    ShowCropperedActivity.this.finish();
                }

            }
            else
            {
                //txtView.setText("识别失败");
                Logger.i(Logger.TAG,"识别失败");
                Toast.makeText(ShowCropperedActivity.this,"识别失败,请重新拍照",Toast.LENGTH_SHORT).show();
                ShowCropperedActivity.this.finish();
            }
            super.onPostExecute(result);
        }
    }*/
}
