package com.up.study.ui.home;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.StudyUtils;
import com.up.common.widget.MyGridView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.ImgSelectorAdpter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.ImgUrl;
import com.up.study.model.StuTestModel;
import com.up.study.callback.ImgCallBack;
import com.up.study.weight.showimages.ImagePagerActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 任务-学生试卷
 */
public class StuTestActivity extends BaseFragmentActivity {
    private LinearLayout ll_no_img;
    private RelativeLayout rl_has_img;
    private TextView tv_next;
    private Button btn_camera;
    private ListView lv ;
    private MyGridView mgv;
    private CommonAdapter<ImgUrl> adapter;
    private List<ImgUrl> dataList = new ArrayList<ImgUrl>();

    //private List<ImgUrl> localSelectImgUrls = new ArrayList<ImgUrl>();//本地已选的图片的地址

    private ArrayList<String> imageList = new ArrayList<>();
    private ImgSelectorAdpter imgAdapter;
    @Override
    protected int getContentViewId() {
        return R.layout.act_task_stu_test;
    }

    @Override
    protected void initView() {
        tv_next = bind(R.id.tv_next);
        btn_camera = bind(R.id.btn_camera);
        lv = bind(R.id.lv);
        ll_no_img = bind(R.id.ll_no_img);
        rl_has_img = bind(R.id.rl_has_img);
        mgv = bind(R.id.mgv);
    }

    @Override
    protected void initEvent() {
        tv_next.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        int relationId = getIntent().getIntExtra(Constants.KEY,0);
        if(relationId==0){
            showToast("请传递试卷id");
            finish();
            return;
        }
        getTest(relationId+"");
        adapter = new CommonAdapter<ImgUrl>(ctx, dataList, R.layout.item_img) {
            @Override
            public void convert(ViewHolder vh, ImgUrl item, int position) {
                ImageView tv_title = vh.getView(R.id.iv_img);
                showLog("图片地址："+item.getUrl());
                J.image().load(ctx, item.getUrl(), tv_title);
            }
        };
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> temp = StudyUtils.imgUrl2String(dataList);
                Intent intent = new Intent(ctx, ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                ctx.startActivity(intent);
            }
        });

        imgAdapter  = StudyUtils.initImgSelector2(this,imageList,9);

    }

    @Override
    public void onClick(View v) {
        if(v==tv_next){
            if (imageList.size()>1){
                Map<String, Object> map = new HashMap<String,Object>();
                map.put("imageList",imageList);
                gotoActivity(TaskTestErrorInputActivity.class,map);
               /* List<String> uploadImgs = StudyUtils.getUploadImgs(imageList);
                //提交图片到阿里云后再提交错题
                showLoading("提交中...",false);
                StudyUtils.uploadImgUrl(uploadImgs, StuTestActivity.this, new CallBack<List<ImgUrl>>() {
                    @Override
                    public void suc(List<ImgUrl> obj) {
                        testUpload(obj);
                    }
                });*/
            }
            else if(dataList.size()==0&&imageList.size()==1){
                showToast("请至少选择一张图片");
                return;
            }
            else{
                gotoActivity(TaskTestErrorInputActivity.class,null);
            }

        }
        else if(v==btn_camera){
            FunctionConfig functionConfig  = StudyUtils.getConfigForGalleryMuti(9);
            GalleryFinal.openGalleryMuti(1, functionConfig, mOnHanlderResultCallback);
        }
    }


    GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
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
                        if (imageList.size() < 9) {
                            imageList.add("add");
                        }
                        imgAdapter.NotifyDataChanged(imageList);

                        ll_no_img.setVisibility(View.GONE);
                        rl_has_img.setVisibility(View.VISIBLE);
                        mgv.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    }
                }
            });

            /*Logger.i(Logger.TAG, "reqeustCode: " + reqeustCode + "  resultList.size" + resultList.size());
            final int total = resultList.size();
            localSelectImgUrls.clear();
            for (int i = 0;i<resultList.size();i++){
                final int index = i;
                switch (reqeustCode) {
                    case 1:
                        //选择完直接上传到阿里云
                        StudyUtils.getImgRequsetUrl(StuTestActivity.this,resultList.get(i).getPhotoPath(),i+"img.png",new ImgCallBack(){
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
            Toast.makeText(StuTestActivity.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();

        }
    };
    /**
     * 获取学生试卷
     * relationId 试卷ID、任务ID
     */
    private void getTest(String relationId) {
        dataList.clear();
        HttpParams params = new HttpParams();
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("relationId", TApplication.getInstant().getRelationId());
        J.http().post(Urls.GET_STU_PAPERS, ctx, params, new HttpCallback<Respond<StuTestModel>>(ctx) {
            @Override
            public void success(Respond<StuTestModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    StuTestModel  model = res.getData();
                    if (model!=null){
                        List<ImgUrl> list = null;
                        try{
                            String imgUrls = model.getAttached();
                            Type type=new TypeToken<List<ImgUrl>>(){}.getType();
                            list = new Gson().fromJson(imgUrls,type);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        if(list!=null&&list.size()>0){
                            ll_no_img.setVisibility(View.GONE);
                            rl_has_img.setVisibility(View.VISIBLE);
                            dataList.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            ll_no_img.setVisibility(View.VISIBLE);
                            rl_has_img.setVisibility(View.GONE);
                            /*ll_no_img.setVisibility(View.GONE);
                            rl_has_img.setVisibility(View.VISIBLE);*/
                        }


                    }
                    else{
                        ll_no_img.setVisibility(View.VISIBLE);
                        rl_has_img.setVisibility(View.GONE);
//                        ll_no_img.setVisibility(View.GONE);
//                        rl_has_img.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }


    /**
     * 图片上传到阿里云
     */
    private void imgUploadAliyun() {

    }

    /**
     * 试卷上传
     * relationId 试卷ID、任务ID
     */
    private void testUpload(List<ImgUrl> obj) {
        HttpParams params = new HttpParams();
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("attached", new Gson().toJson(obj));
        J.http().post(Urls.UPLOAD_PAPERS, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("上传试卷成功");
                    StuTestActivity.this.hideLoading();
                    gotoActivity(TaskTestErrorInputActivity.class,null);
                }
            }
        });
    }

}
