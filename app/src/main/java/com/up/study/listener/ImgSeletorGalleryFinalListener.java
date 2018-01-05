package com.up.study.listener;

import android.widget.Toast;

import com.up.common.base.CommonAdapter;
import com.up.common.utils.Logger;
import com.up.study.ui.home.TaskErrorsInputActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by dell on 2017/7/30.
 */

public class ImgSeletorGalleryFinalListener implements GalleryFinal.OnHanlderResultCallback {
    private List<String> imageList = new ArrayList<>();
    private CommonAdapter<String> imgAdapter;
    private int maxSize;

    public ImgSeletorGalleryFinalListener(List<String> imageList,CommonAdapter<String> imgAdapter,int maxSize){
        this.imageList = imageList;
        this.imgAdapter = imgAdapter;
        this.maxSize = maxSize;//图片最大数
    }
    @Override
    public void onHanlderSuccess(int reqeustCode, final List<PhotoInfo> resultList) {
        Logger.i(Logger.TAG, "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
        String [] filePaths = new String[resultList.size()];
        for (int i = 0;i<resultList.size();i++){
            filePaths[i] = resultList.get(i).getPhotoPath();
        }
        //压缩图片
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Tiny.getInstance().source(filePaths).batchAsFile().withOptions(options).batchCompress(new FileBatchCallback() {
            @Override
            public void callback(boolean isSuccess, String[] outfile) {
                List<String> list = Arrays.asList(outfile);
                if (!list.isEmpty()) {
                    if (imageList.get(imageList.size() - 1).equals("add")) {
                        imageList.remove(imageList.size() - 1);
                    }
                    imageList.addAll(list);
                    if (imageList.size() < maxSize) {
                        imageList.add("add");
                    }
                    imgAdapter.NotifyDataChanged(imageList);
                }
            }
        });

            /*for (int i = 0;i<resultList.size();i++){
                File file = new File(resultList.get(i).getPhotoPath());
                Logger.i(Logger.TAG,"压缩前文件大小："+file.length() + "");
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                Tiny.getInstance().source(resultList.get(i).getPhotoPath()).asFile().withOptions(options).compress(new FileCallback() {
                    @Override
                    public void callback(boolean isSuccess, String outfile) {
                        File file = new File(outfile);
                        Logger.i(Logger.TAG,"压缩后的文件大小："+file.length() + "");
                        //return the compressed file path
                    }
                });
            }*/

            /*final int total = resultList.size();
            localSelectImgUrls.clear();
            for (int i = 0;i<resultList.size();i++){
                final int index = i;
                switch (reqeustCode) {
                    case 1:
                        //选择完直接上传到阿里云
                        StudyUtils.getImgRequsetUrl(TaskErrorsInputActivity.this,resultList.get(i).getPhotoPath(),i+"img.png",new ImgCallBack(){
                            @Override
                            public void suc(String imgPath) {
                                //返回的是阿里云的图片地址
                                ImgUrl imgUrl = new ImgUrl();
                                imgUrl.setUrl(imgPath);
                                showLog("上次图片张数："+index+","+(total-1));
                                if(index==total-1){//全部上传完成
                                    ll_no_img.setVisibility(View.GONE);
                                    rl_has_img.setVisibility(View.VISIBLE);
                                }
                                dataList.add(imgUrl);
                                localSelectImgUrls.add(imgUrl);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        Logger.i(Logger.TAG,"已选图片地址："+resultList.get(i).getPhotoPath());
                        break;
                    case 2:
                        break;
                }
            }*/
    }

    @Override
    public void onHanlderFailure(int requestCode, String errorMsg) {
        Logger.i(Logger.TAG, "reqeustCode: " + requestCode + "  errorMsg" + errorMsg);
    }
}
