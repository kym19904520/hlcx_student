package com.up.common.utils;

import java.io.IOException;

/**
 * Created by dell on 2017/7/8.
 */

public class OcrUtils {

   /* private static TessBaseAPI mTess;

    public static TessBaseAPI getTassBase(){
        if(mTess == null){
            mTess = new TessBaseAPI();
            String language = "chi_sim";
            mTess.init("/mnt/sdcard/tesseract", language);
            Logger.i(Logger.TAG,"OCR初始化完成===================================");
        }
        return mTess;
    }
    *//**
     * 拷贝语言包从Assert到Sd卡
     * @param ctx
     * @param strOutFileName
     * @throws IOException
     *//*
    public static void copyBigDataToSD(Context ctx, String strOutFileName) throws IOException
    {
        File sd= Environment.getExternalStorageDirectory();
        String path1=sd.getPath()+"/tesseract";
        File file=new File(path1);
        if(!file.exists()){
            Logger.i(Logger.TAG,"创建tesseract");
            file.mkdir();
        }
        String path2=sd.getPath()+"/tesseract/tessdata";
        File file2=new File(path2);
        if(!file2.exists()){
            Logger.i(Logger.TAG,"创建tessdata");
            file2.mkdir();
        }

        InputStream myInput;
        OutputStream myOutput = new FileOutputStream("/mnt/sdcard/tesseract/tessdata/chi_sim.traineddata");
        myInput = ctx.getAssets().open("chi_sim.traineddata");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
//            Log.i("tag","length="+length);
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        Logger.i(Logger.TAG,"拷贝OCR中文包完毕");
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }*/


}
