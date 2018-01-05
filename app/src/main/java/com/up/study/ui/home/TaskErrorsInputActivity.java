package com.up.study.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.StudyUtils;
import com.up.common.widget.MyGridView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.listener.ImgSeletorGalleryFinalListener;
import com.up.study.model.AnswerSheetModel;
import com.up.study.model.ImgUrl;
import com.up.study.model.SeqModel;
import com.up.study.weight.showimages.ImagePagerActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 任务-错题录入
 */
public class TaskErrorsInputActivity extends BaseFragmentActivity {
    private AlertDialog dialog;
    private TextView tv_title,tv_right,tv_submit;

    private LinearLayout ll_A,ll_B,ll_C,ll_D;
    private TextView tv_A,tv_B,tv_C,tv_D;
    private EditText et_D;

    //private ImageView error_iv_1,error_iv_2,error_iv_3;
    //private ImageView error_iv_clear_1,error_iv_clear_2,error_iv_clear_3;

    WheelView wheelView_line,wheelView_section;
    ArrayList<String> lineList;
    ArrayList<String> sectionList;
    ArrayList<String> sectionList2;

    private SeqModel seqModel;
    private AnswerSheetModel answerSheetModel;
    private String errType;//错误原因

    private MyGridView mgv;
    private ArrayList<String> imageList = new ArrayList<>();
    private CommonAdapter<String> imgAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.act_task_error_input;
    }

    @Override
    protected void initView() {
        tv_right = bind(R.id.tv_right);
        tv_title = bind(R.id.tv_title);
        tv_submit = bind(R.id.tv_submit);

        ll_A = bind(R.id.ll_A);
        ll_B = bind(R.id.ll_B);
        ll_D = bind(R.id.ll_D);
        ll_C = bind(R.id.ll_C);
        tv_A = bind(R.id.tv_A);
        tv_B = bind(R.id.tv_B);
        tv_C = bind(R.id.tv_C);
        tv_D = bind(R.id.tv_D);

        et_D = bind(R.id.et_D);
        et_D.setEnabled(false);

        //mgv = bind(R.id.mgv);
        StudyUtils.initImgSelector(this,imageList,9);
        /*error_iv_1 = bind(R.id.error_iv_1);
        error_iv_2 = bind(R.id.error_iv_2);
        error_iv_3 = bind(R.id.error_iv_3);
        error_iv_clear_1 = bind(R.id.error_iv_clear_1);
        error_iv_clear_2 = bind(R.id.error_iv_clear_2);
        error_iv_clear_3 = bind(R.id.error_iv_clear_3);*/
    }

    private void initImgSelector(){
        imageList.add("add");
        imgAdapter = new CommonAdapter<String>(ctx, imageList, R.layout.item_gv_image) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                final int index = position;
                ImageView ivDel = vh.getView(R.id.iv_del);
                if (item.equals("add")) {
                    ivDel.setVisibility(View.GONE);
                    vh.setImageResource(R.id.iv, R.mipmap.sy_add_img);
                } else {
                    ivDel.setVisibility(View.VISIBLE);
                    vh.setImageByUrl(R.id.iv, item);
                }
                ivDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageList.remove(index);
                        if (!imageList.get(imageList.size() - 1).equals("add")) {
                            imageList.add("add");
                        }
                        imgAdapter.NotifyDataChanged(imageList);
                    }
                });
            }
        };
        mgv.setAdapter(imgAdapter);

        mgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                if (imageList.get(i).equals("add")) {//点击的是默认的添加图标，则去添加图片
                    FunctionConfig functionConfig  = StudyUtils.getConfigForGalleryMuti(9 - (imageList.size() - 1));
                    GalleryFinal.openGalleryMuti(1, functionConfig, new ImgSeletorGalleryFinalListener(imageList,imgAdapter,9));
                } else {//查看图片
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.addAll(imageList);
                    if (temp.get(temp.size() - 1).equals("add")) {
                        temp.remove(temp.size() - 1);
                    }
                    Intent intent = new Intent(ctx, ImagePagerActivity.class);
                    intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(intent);
                }

            }
        });

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
            Toast.makeText(TaskErrorsInputActivity.this, "requestCode: " + requestCode + "  " + errorMsg, Toast.LENGTH_LONG).show();

        }
    };

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        ll_A.setOnClickListener(this);
        ll_B.setOnClickListener(this);
        ll_C.setOnClickListener(this);
        ll_D.setOnClickListener(this);

        /*error_iv_1.setOnClickListener(this);
        error_iv_2.setOnClickListener(this);
        error_iv_3.setOnClickListener(this);
        error_iv_clear_1.setOnClickListener(this);
        error_iv_clear_2.setOnClickListener(this);
        error_iv_clear_3.setOnClickListener(this);*/
    }

    @Override
    protected void initData() {
        seqModel = (SeqModel) getIntent().getSerializableExtra("bean1");
        answerSheetModel = (AnswerSheetModel) getIntent().getSerializableExtra("bean2");

        tv_title.setText("第"+seqModel.getSeq()+"题  错误原因");
        //初始化数据
        lineList = new ArrayList<>();
        for (int i = 0;i<=(int)seqModel.getMark();i++){
            lineList.add(i+"");
        }
        sectionList = new ArrayList<>();
        sectionList.add(".5");
        sectionList.add(".0");

        sectionList2 = new ArrayList<>();
        sectionList2.add(".0");

        //设置数据
         wheelView_line =bind(R.id.wheel_line);
         wheelView_section = bind(R.id.wheel_section);

        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(getResources().getColor(R.color.text_white));//设置分割线颜色
        wheelView_line.setLineConfig(config);
        wheelView_line.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_line.setTextSize(24);
        wheelView_line.setVisibleItemCount(5);
        wheelView_line.setItems(lineList);//设置第一个WheelView
        wheelView_line.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                if (index==(int)seqModel.getMark()){
                    wheelView_section.setItems(sectionList);
                }
                else{
                    wheelView_section.setItems(sectionList);
                }

            }
        });
        wheelView_line.setSelectedIndex((int)seqModel.getMark()); //默认选中项
        //wheelView_line.setCycleDisable(true); //选项不循环滚动

        wheelView_section.setLineConfig(config);
        wheelView_section.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));
        wheelView_section.setTextSize(24);
        wheelView_section.setItems(sectionList);
        wheelView_section.setVisibleItemCount(5);
       // wheelView_section.setCycleDisable(true); //选项不循环滚动

        //最后可以得到两个WheelView选择的值
        //String content = wheelView_line.getSelectedItem() + wheelView_section.getSelectedItem();
    }

    @Override
    public void onClick(View v) {
        if(v==tv_right){
            gotoActivity(TaskTestAnalysisActivity.class,seqModel.getSubjectId());
        }
        else if (v==tv_submit){
            //showDialog();
            submit();
        }
        else if(v==ll_A){
            errType = "A";
            tv_A.setBackgroundResource(R.drawable.round_blue_circle_background);
            tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
            et_D.setEnabled(false);
            et_D.setText(null);
        }
        else if(v==ll_B){
            errType = "B";
            tv_B.setBackgroundResource(R.drawable.round_blue_circle_background);
            tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
            et_D.setEnabled(false);
            et_D.setText(null);
        }
        else if(v==ll_C){
            errType = "C";
            tv_C.setBackgroundResource(R.drawable.round_blue_circle_background);
            tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_D.setBackgroundResource(R.drawable.round_gray_circle_background);
            et_D.setEnabled(false);
            et_D.setText(null);
        }
        else if(v==ll_D){
            errType = "D";
            tv_D.setBackgroundResource(R.drawable.round_blue_circle_background);
            tv_B.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_C.setBackgroundResource(R.drawable.round_gray_circle_background);
            tv_A.setBackgroundResource(R.drawable.round_gray_circle_background);
            et_D.setEnabled(true);
            et_D.setFocusable(true);
            et_D.setFocusableInTouchMode(true);
        }

        /*else if(v==error_iv_1){
            *//*showLog("1111111111111111");
            GalleryFinal.openGallerySingle(1, new GalleryFinal.OnHanlderResultCallback() {
                @Override
                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                    showLog("22222222222222222");
                }

                @Override
                public void onHanlderFailure(int requestCode, String errorMsg) {
                    showLog("333333333333333333");
                }
            });*//*
            GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_1, new CameraResultCallback.CameraSucCallBack() {
                @Override
                public void success(String filePath) {
                    error_iv_clear_1.setVisibility(View.VISIBLE);
                }
            }));
        }
        else if(v==error_iv_2){
            GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_2, new CameraResultCallback.CameraSucCallBack() {
                @Override
                public void success(String filePath) {
                    error_iv_clear_2.setVisibility(View.VISIBLE);
                }
            }));
        }
        else if(v==error_iv_3){
            GalleryFinal.openGallerySingle(1, new CameraResultCallback(error_iv_3, new CameraResultCallback.CameraSucCallBack() {
                @Override
                public void success(String filePath) {
                    error_iv_clear_3.setVisibility(View.VISIBLE);
                }
            }));
        }
        else if(v==error_iv_clear_1){
            error_iv_1.setImageBitmap(ImageUtil.readBitMap(this,R.mipmap.sy_add_img));
            error_iv_clear_1.setVisibility(View.GONE);
        }
        else if(v==error_iv_clear_2){
            error_iv_2.setImageBitmap(ImageUtil.readBitMap(this,R.mipmap.sy_add_img));
            error_iv_clear_2.setVisibility(View.GONE);

        }
        else if(v==error_iv_clear_3){
            error_iv_3.setImageBitmap(ImageUtil.readBitMap(this,R.mipmap.sy_add_img));
            error_iv_clear_3.setVisibility(View.GONE);
        }*/
    }
    /**
     * 错题录入
     */
    private void submit() {
        if (TextUtils.isEmpty(errType)){
            showToast("请选择错误原因");
            return;
        }
        if (errType.equals("D")&& TextUtils.isEmpty(et_D.getText().toString())){
            showToast("请填写错误描述");
            return;
        }
        List<String> uploadImgs = StudyUtils.getUploadImgs(imageList);
        if(uploadImgs.size()==0){
            showToast("请选择错题图片");
            return;
        }
        //提交图片到阿里云后再提交错题
        showLoading("提交中...",false);
        StudyUtils.uploadImgUrl(uploadImgs, TaskErrorsInputActivity.this, new CallBack<List<ImgUrl>>() {
            @Override
            public void suc(List<ImgUrl> obj) {
                submitErrorSeq(obj);
            }
        });


    }

    private void submitErrorSeq(List<ImgUrl> imgList) {
        String lineValue = lineList.get(wheelView_line.getSelectedIndex());
        String sectionValue = sectionList.get(wheelView_section.getSelectedIndex());
        String point = lineValue+sectionValue;
        showLog("point="+point);
        HttpParams params = new HttpParams();
        params.put("cuid", answerSheetModel.getCuid());//	创建者或老师ID
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("mark", seqModel.getMark());
        params.put("point", point);//扣除分数
        params.put("errType", errType);//错误原因，ＡＢＣＤ。
        params.put("errDescribe", et_D.getText().toString());//错误描述，如果选择Ｄ则输入。
        params.put("errAttached", new Gson().toJson(imgList));//图片地址，用json数组组合地址路径。
        J.http().post(Urls.SAVE_ERROR_PAPERS, ctx, params, new HttpCallback<Respond<String>>(null) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("错题已提交");
                    hideLoading();
                    TaskErrorsInputActivity.this.finish();
                }
            }
        });

    }

    private void showDialog(){
        dialog = new AlertDialog.Builder(ctx).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_submit);
        //初始化数据
        ArrayList<String> lineList = new ArrayList<>();
        lineList .add("0");
        lineList .add("1");
        lineList .add("2");
        lineList .add("3");
        lineList .add("4");
        lineList .add("5");
        lineList .add("6");
        lineList .add("7");
        lineList .add("8");
        lineList .add("9");
        TextView tv_right = (TextView) window.findViewById(R.id.tv_right);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(TaskTestResultActivity.class,null);
                dialog.cancel();
            }
        });
        //设置数据
        WheelView wheelView_line =(WheelView) window.findViewById(R.id.wheel_line);
        WheelView wheelView_section =(WheelView) window.findViewById(R.id.wheel_section);
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(getResources().getColor(R.color.text_white));//设置分割线颜色
        wheelView_line.setLineConfig(config);
        wheelView_line.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_line.setTextSize(24);
        wheelView_section.setLineConfig(config);
        wheelView_section.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));
        wheelView_section.setTextSize(24);
        wheelView_line.setItems(lineList);//设置第一个WheelView
        wheelView_section.setItems(lineList);

    }
}
