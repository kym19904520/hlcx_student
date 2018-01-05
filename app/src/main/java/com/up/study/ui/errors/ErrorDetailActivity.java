package com.up.study.ui.errors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.HomePageAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ErrorClearnModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题首页列表点击进来----错题详情
 */
public class ErrorDetailActivity extends BaseFragmentActivity {

    private ViewPager viewPager;

    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private TextView tv_title;
    private TextView tv_num;

    private int totalNum;

    String date;//时间
    String source;//来源
    String majorId;//学科id


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast("onNewIntent");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_errors_clear;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);
        tv_title = bind(R.id.tv_title);
        tv_title.setText("错题详情");
    }
    @Override
    protected void initData() {
        date = getIntent().getStringExtra("date");//时间
        source = getIntent().getStringExtra("source");//来源
        majorId = getIntent().getStringExtra("majorId");//来源
        getErrorCleanList(date,source,majorId);
    }

    private void initViewPager(){
        for(int i = 1;i<=totalNum;i++){
            SolutionFragment newFragment = new SolutionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("seqType", SolutionFragment.SEQ_TYPE_CTXQ);
            bundle.putString("date", date);
            bundle.putString("majorId", majorId);
            bundle.putInt("source", Integer.parseInt(source));
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }

        homePageAdapter = new HomePageAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(homePageAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position+1 + "/" + totalNum);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    protected void initEvent() {}

    @Override
    public void onClick(View v) {}


    /**
     * 只为了获取总题量
     * @param date
     * @param source
     */
    private void getErrorCleanList(String date, String source,String majorId) {
        //mSeqList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("date", date);
        params.put("source", source);
        params.put("page", 1);
        params.put("rows",1);

        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum  = res.getData().getTotalCount();
                    if(totalNum==0){
                        tv_num.setText(0 + "/" + totalNum);
                        showToast("暂无错题");
                    }
                    else{
                        tv_num.setText(1 + "/" + totalNum);
                    }
                    showLog(totalNum + TAG);
                    initViewPager();

                }
            }
        });
    }
}
