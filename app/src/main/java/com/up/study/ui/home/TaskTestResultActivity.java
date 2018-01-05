package com.up.study.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.widget.MyListView;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.AnalysisModel;
import com.up.study.model.ErrorCause;
import com.up.study.model.Know;
import com.up.study.model.Options;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 任务试卷提交结果
 */
public class TaskTestResultActivity extends BaseFragmentActivity {
    MyListView mylv_know,mylv_error;
    CommonAdapter<Know> adapter_know;
    CommonAdapter<ErrorCause> adapter_error;
    private List<Know> knowList = new ArrayList<Know>();
    private List<ErrorCause> errorList = new ArrayList<ErrorCause>();

    private TextView tv_score,tv_des,tv_goto_error,tv_goto_home;
    @Override
    protected int getContentViewId() {
        return R.layout.act_test_result;
    }

    @Override
    protected void initView() {
        mylv_know = bind(R.id.mylv_know);
        mylv_error = bind(R.id.mylv_error);
        tv_score = bind(R.id.tv_score);
        tv_des = bind(R.id.tv_des);
        tv_goto_error = bind(R.id.tv_goto_error);
        tv_goto_home = bind(R.id.tv_goto_home);
    }

    @Override
    protected void initEvent() {
        tv_goto_error.setOnClickListener(this);
        tv_goto_home.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        adapter_know = new CommonAdapter<Know>(ctx, knowList, R.layout.item_result_know) {
            @Override
            public void convert(ViewHolder vh, Know item, int position) {
                TextView tv_result_title = vh.getView(R.id.tv_result_title);
                TextView tv_result_num = vh.getView(R.id.tv_result_num);
                TextView tv_result_class_right = vh.getView(R.id.tv_result_class_right);
                TextView tv_result_my_right = vh.getView(R.id.tv_result_my_right);
                ImageView iv_result_right = vh.getView(R.id.iv_result_right);
                tv_result_title.setText(item.getKnowName());
                tv_result_num.setText(item.getKnowTotal());
                tv_result_class_right.setText(item.getClassCorrectRate()+"%");
                tv_result_my_right.setText(item.getMyCorrectRate()+"%");
                if (item.getClassCorrectRate()<=item.getMyCorrectRate()){
                    iv_result_right.setImageResource(R.mipmap.sy_35);
                }
                else{
                    iv_result_right.setImageResource(R.mipmap.sy_36);
                }
            }
        };
        mylv_know.setAdapter(adapter_know);

        adapter_error = new CommonAdapter<ErrorCause>(ctx, errorList, R.layout.item_result_error) {
            @Override
            public void convert(ViewHolder vh, ErrorCause item, int position) {
                TextView tv_result_error = vh.getView(R.id.tv_result_error);
                TextView tv_result_title = vh.getView(R.id.tv_result_title);
                TextView tv_result_num = vh.getView(R.id.tv_result_num);
                tv_result_title.setText(item.getErrorName());
                tv_result_error.setText((item.getErrorRate()==null?0:item.getErrorRate())+"%");
                tv_result_num.setText(item.getErrorTotal());
            }
        };
        mylv_error.setAdapter(adapter_error);

        getResult();
    }

    @Override
    public void onClick(View v) {
        if (v==tv_goto_error){
            showLog("回到错题库");
            gotoActivity(MainActivity.class,0);
        }
        else if(v==tv_goto_home){
            showLog("回到首页");
            gotoActivity(MainActivity.class,1);
        }
    }
    /**
     * 获取提交试卷结果
     */
    private void getResult() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.GET_PAPER_ANALYSIS, ctx, params, new HttpCallback<Respond<AnalysisModel>>(ctx) {
            @Override
            public void success(Respond<AnalysisModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    //showToast(res.getData().getKnow().get(0).getKnowName());
                    AnalysisModel analysisModel= res.getData();
                    List<Know> knows = analysisModel.getKnow();
                    List<ErrorCause> errors = analysisModel.getErrorCause();
                    knowList.addAll(knows);
                    errorList.addAll(errors);
                    adapter_know.notifyDataSetChanged();
                    adapter_error.notifyDataSetChanged();

                    tv_score.setText(analysisModel.getPoint());
                    tv_des.setText("在本班中，排名第"+analysisModel.getRankNum()+"名");
                }
            }
        });
    }
}
