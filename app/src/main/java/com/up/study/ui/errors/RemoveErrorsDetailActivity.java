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
import com.up.study.model.HasRemoveErrorsModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 已移除错题详情
 */
public class RemoveErrorsDetailActivity extends BaseFragmentActivity {

    private ViewPager viewPager;
    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private int source;
    private TextView tv_title,tv_num;
    private int totalNum;

    private int currentIndex=0;//当前页

    @Override
    protected int getContentViewId() {
        return R.layout.act_remove_error;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_title = bind(R.id.tv_title);
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initData() {
        //source = getIntent().getIntExtra(Constants.KEY,0);
        HasRemoveErrorsModel hasRemoveErrorsModel = (HasRemoveErrorsModel)getIntent().getSerializableExtra("bean1");
        source = hasRemoveErrorsModel.getSource();
        showLog("source="+source);
        tv_title.setText("已移除-"+hasRemoveErrorsModel.getSourceName());
        getHasRemoveErrorList(false);
    }

    private void initViewPager() {

        for(int i = 1;i<=totalNum;i++){
            SolutionFragment newFragment = new SolutionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("source", source);
            bundle.putInt("seqType", SolutionFragment.SEQ_TYPE_YYC);
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }

        homePageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(currentIndex);
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
    public void clear(int index){
        currentIndex = index;
        showLog("清除全部，重新加载,并跳转到："+index);
        homePageAdapter = null;
        fragmentList.clear();
        getHasRemoveErrorList(true);

        /*homePageAdapter.clearItem(index);
        tv_num.setText((index+1) + "/" + --totalNum);*/
    }


    /**
     * 只为了获取总题量
     */
    private void getHasRemoveErrorList(final boolean isClear) {
        showLog("----------获取已移除错题库总数--------------");
        //mSeqList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", TApplication.getInstant().getMarjorId());
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("student_status", 1);
        params.put("source", source);
        J.http().post(Urls.HAS_REMOVE_ERROR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum  = res.getData().getTotalCount();
                    if(totalNum<=0){
                        if (!isClear) {
                            showToast("无题目");
                        }
                        RemoveErrorsDetailActivity.this.finish();
                        return;
                    }
                    tv_num.setText(1 + "/" + totalNum);
                    initViewPager();
                   /* mSeqList.addAll(res.getData().getSubList());
                    initSeqCardData();*/
                }
            }
        });
    }

    /*private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();

    private TextView tv_title,tv_num;

    @Override
    protected int getContentViewId() {
        return R.layout.act_errors_detail;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_title = bind(R.id.tv_title);
        tv_title.setText("已移除真实试卷");
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        int source = getIntent().getIntExtra(Constants.KEY,0);
        showLog("source="+source);

        mInflater = getLayoutInflater();
        for (int i = 0; i < 10; i++) {
            View view = mInflater.inflate(R.layout.view_error_remove, null);
            initInflaterView(view);
            mViews.add(view);
        }

        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/10");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initInflaterView(View view) {
         TextView tv_answer_b = (TextView) view.findViewById(R.id.tv_answer_b);
        tv_answer_b.setBackgroundResource(R.drawable.round_blue_circle_background);

    }

    @Override
    public void onClick(View v) {
    }*/


}
