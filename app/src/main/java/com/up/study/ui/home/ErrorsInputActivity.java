package com.up.study.ui.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.ImgUrl;
import com.up.study.model.SeqModel;
import com.up.study.weight.BottomScrollView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题录入（针对已有的题目）
 */
public class ErrorsInputActivity extends BaseFragmentActivity {
    private BottomScrollView bottomScrollView;
    private TextView tv_pull_to_load,tv_submit,tv_show_answer;
    private LinearLayout ll_load_info;

    private EditText et_D;
    private String errType;//错误原因选项
//    List<String> imgList;
    private SeqModel curSeqModel;

    private ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.act_error_input;
    }

    @Override
    protected void initView() {
        bottomScrollView = bind(R.id.bottomScrollView);
        tv_pull_to_load = bind(R.id.tv_pull_to_load);
        ll_load_info = bind(R.id.ll_load_info);
        tv_submit = bind(R.id.tv_submit);
        tv_show_answer = bind(R.id.tv_show_answer);
    }

    @Override
    protected void initEvent() {
        tv_submit.setOnClickListener(this);
        tv_show_answer.setOnClickListener(this);
        bottomScrollView.setmListViewListener(new BottomScrollView.IXListViewListener() {
            @Override
            public void onRefresh() {
                showLog("onRefresh");
            }
            @Override
            public void onPullToLoadMore() {
                showLog("onPullToLoadMore");
                /*tv_pull_to_load.setVisibility(View.GONE);
                ll_load_info.setVisibility(View.VISIBLE);*/
            }
        });
    }

    @Override
    protected void initData() {
//        imgList = new ArrayList<String>();
        curSeqModel = (SeqModel) getIntent().getSerializableExtra("bean1");
        ImgUrl imgUrl = (ImgUrl)getIntent().getSerializableExtra("bean2");
        //将查询的照片作为第一张错图
        /*if(!TextUtils.isEmpty(imgUrl.getUrl())){
            imageList.add(imgUrl.getUrl());
        }*/
        StudyUtils.initSeqActivity(curSeqModel,this);
        StudyUtils.initAnswerAndAnalysis(this,curSeqModel);
        et_D = bind(R.id.et_D);
        StudyUtils.initErrorCause(this, et_D, new CallBack<String>() {
            @Override
            public void suc(String str) {
                errType = str;
            }
        });

        StudyUtils.initImgSelector(this,imageList,9);

        /*StudyUtils.initImgActivity(this,
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                },
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                },
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                }
        );*/
    }

    @Override
    public void onClick(View v) {
        if(v==tv_submit){
            errorInput();
        }
        else if(v==tv_show_answer){
            tv_pull_to_load.setVisibility(View.GONE);
            ll_load_info.setVisibility(View.VISIBLE);
            tv_show_answer.setVisibility(View.GONE);
        }
    }


    /**
     * 错题录入
     */
    private void errorInput() {
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
        StudyUtils.uploadImgUrl(uploadImgs, ErrorsInputActivity.this, new CallBack<List<ImgUrl>>() {
            @Override
            public void suc(List<ImgUrl> obj) {
                submitErrorSeq(obj);
            }
        });
    }

    private void submitErrorSeq(List<ImgUrl> imgList) {
        HttpParams params = new HttpParams();
        params.put("user_id", TApplication.getInstant().getUserId());
        params.put("subject_id", curSeqModel.getSubjectId());
        params.put("student_id", TApplication.getInstant().getStudentId());
        params.put("err_type", errType);
        params.put("err_describe", et_D.getText().toString());
        params.put("err_attached", new Gson().toJson(imgList));
        J.http().post(Urls.ERROR_INPUT, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("录入成功");
                    ErrorsInputActivity.this.finish();
                }
            }
        });

    }
}
