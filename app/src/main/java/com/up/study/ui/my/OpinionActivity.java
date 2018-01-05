package com.up.study.ui.my;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 意见反馈
 */
public class OpinionActivity extends BaseFragmentActivity {
    private EditText et_opinion;
    @Override
    protected int getContentViewId() {
        return R.layout.act_opinion;
    }

    @Override
    protected void initView() {
        et_opinion = bind(R.id.et_opinion);
    }

    @Override
    protected void initEvent() {}

    @Override
    protected void initData() {
        bind(R.id.btn_sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_sure){
            bindingStudent();
        }
    }
    /**
     * 绑定学生
     */
    private void bindingStudent() {
        String opinion =  et_opinion.getText().toString();
        if(TextUtils.isEmpty(opinion)){
            showToast("请填写您的意见");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("content", opinion);
        params.put("position", 1);
        J.http().post(Urls.FEEDBACK_SUBMIT, ctx, params, new HttpCallback< Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("反馈成功");
                    OpinionActivity.this.finish();
                }
            }
        });
    }
}
