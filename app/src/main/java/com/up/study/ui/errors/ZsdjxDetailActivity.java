package com.up.study.ui.errors;

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
import com.up.study.weight.treeee.Node;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 知识点解析-题目列表
 */
public class ZsdjxDetailActivity extends BaseFragmentActivity {

    private ViewPager viewPager;
    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private TextView tv_title,tv_num;
    private int totalNum;

    private Node node;
    @Override
    protected int getContentViewId() {
        return R.layout.act_zsdjx_detail;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_title = bind(R.id.tv_title);
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initData() {
        node = (Node)getIntent().getSerializableExtra("bean1");
        //tv_title.setText("知识点-"+node.getName());
        getZsdList();
    }

    private void initViewPager() {

        for(int i = 1;i<=totalNum;i++){
            SolutionFragment newFragment = new SolutionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("knowId", node.getId());
            bundle.putInt("seqType", SolutionFragment.SEQ_TYPE_ZSDJX);
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }

        homePageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter);
        viewPager.setOffscreenPageLimit(3);
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
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 只为了获取总题量
     */
    private void getZsdList() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("knowledge_id", node.getId());
        J.http().post(Urls.ZSDFX_SEQ_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum  = res.getData().getTotalCount();
                    if(totalNum<=0){
                        showToast("无题目");
                        ZsdjxDetailActivity.this.finish();
                        return;
                    }
                    tv_num.setText(1 + "/" + totalNum);
                    initViewPager();
                }
            }
        });
    }


}
