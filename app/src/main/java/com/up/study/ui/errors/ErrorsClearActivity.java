package com.up.study.ui.errors;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.up.study.weight.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题扫除
 */
public class ErrorsClearActivity extends BaseFragmentActivity {

    private ViewPager viewPager;
    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private TextView tv_num, tv_right, tv_title;
    private LinearLayout ll_no_error;
    private String type;    //错题类型
    private String majorId;//学科id

    private int currentIndex = 0;//当前页
    private String[] errorType;
    private int totalNum,totalNum01,totalNum02,totalNum03; //题目的个数

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
        tv_right = bind(R.id.tv_right);
        tv_right.setVisibility(View.VISIBLE);
        ll_no_error = bind(R.id.ll_no_error);

        Resources res = getResources();
        errorType = res.getStringArray(R.array.error_select_array);
    }

    @Override
    protected void initData() {
        tv_title.setText(R.string.error_clear);
        majorId = getIntent().getStringExtra("majorId");//来源
        type = getIntent().getStringExtra("type");
        switch (type) {
            case "1":
                majorId = "2";
                tv_right.setText(R.string.home_01);
                getErrorNotCleanList();
                break;
            case "2":
                majorId = "2";
                tv_right.setText(R.string.home_02);
                getErrorAlreadyCleanList();
                break;
            case "3":
                majorId = "2";
                tv_right.setText(R.string.home_03);
                getErrorAlreadyGraspList();
                break;
            default:
                getErrorCleanList();   //全部（暂时没有全部已隐藏）
                break;
        }
    }

    /**
     * 清除错题（重做本题）
     * @param index
     */
    public void clear(int index) {
        currentIndex = index;
        getReformErrorCleanList();
    }

    /**
     * 获取全部错题该重做该题目的数据（已隐藏）
     */
    private void getReformErrorCleanList() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum = res.getData().getTotalCount();
                    loadFragment();
                    HomePageAdapter adapter = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
                    viewPager.setAdapter(adapter);
                    viewPager.setOffscreenPageLimit(fragmentList.size());
                    viewPager.setCurrentItem(currentIndex);
                    adapter.notifyDataSetChanged();
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            tv_num.setText(position + 1 + "/" + totalNum);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }
        });
    }

    private void loadFragment() {
        fragmentList.clear();
        for (int i = 1; i <= totalNum; i++) {
            ErrorsClearFragment newFragment = new ErrorsClearFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("seqType", ErrorsClearFragment.SEQ_TYPE_CTSC);
            bundle.putString("majorId", majorId);
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }
    }

    private void loadNotCleanFragment() {
        fragmentList.clear();
        for (int i = 1; i <= totalNum01; i++) {
            ErrorsClearFragment newFragment = new ErrorsClearFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("seqType", ErrorsClearFragment.SEQ_TYPE_WSC);
            bundle.putString("majorId", majorId);
            bundle.putString("err_status", "1");
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }
    }

    /**
     * 下一题
     */
    public void next(int index) {
        currentIndex = index;
        if (index >= fragmentList.size()){
            new CommonDialog.Builder(this)
                    .setTitle(R.string.dialog_hint)
                    .setMessage(R.string.dialog_next)
                    .setPositiveButton(R.string.dialog_confirm, null, R.color.colorPrimaryDark)
                    .show();
            return;
        }
        viewPager.setCurrentItem(index);
    }

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_right) {        //选择错题类型
            showSelectDialog();
        }
    }

    /**
     * 显示错题类型的弹窗
     */
    private void showSelectDialog() {
        View popupView = getLayoutInflater().inflate(R.layout.view_popupwindow,null);
        ListView lv = (ListView) popupView.findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(ErrorsClearActivity.this, android.R.layout.simple_list_item_1, errorType));
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setAnimationStyle(R.style.popup_window_anim);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.showAsDropDown(tv_right, 0, 10);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_right.setText(errorType[position]);
                showView(errorType[position]);
                popupWindow.dismiss();
            }
        });
    }

    private void showView(String string) {
        if (string.equals("全部")) {     //已隐藏全部
            getErrorCleanList();
        } else if (string.equals("未扫除")) {
            getErrorNotCleanList();
        } else if (string.equals("已扫除")) {
            getErrorAlreadyCleanList();
        } else if (string.equals("已掌握")) {
            getErrorAlreadyGraspList();
        }
    }

    /**-------------------------------------start----------------------------------------------*/

    /**
     * 只为了获取所有题目总题量
     */
    private void getErrorCleanList() {
        totalNum = 0;
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum = res.getData().getTotalCount();
                    if (totalNum == 0) {
                        ll_no_error.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.GONE);
                        tv_num.setText(0 + "/" + totalNum);
                    } else {
                        viewPager.setVisibility(View.VISIBLE);
                        ll_no_error.setVisibility(View.GONE);
                        tv_num.setText(1 + "/" + totalNum);
                    }
                    initViewPager();
                }
            }
        });
    }

    /**
     * 初始化所有题目的view
     */
    private void initViewPager() {
        loadFragment();
        homePageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        homePageAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/" + totalNum);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    /**-------------------------------------end----------------------------------------------*/

    /**
     * 初始化未扫除的view
     */
    private void initErrorNotCleanViewPager() {
        loadNotCleanFragment();
        HomePageAdapter homePageAdapter01 = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter01);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        homePageAdapter01.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/" + totalNum01);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 只为了获取未扫除总题量
     */
    private void getErrorNotCleanList() {
        totalNum01 = 0;
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("err_status", "1");
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum01 = res.getData().getTotalCount();
                    if (totalNum01 == 0) {
                        ll_no_error.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.GONE);
                        tv_num.setText(0 + "/" + totalNum01);
                    } else {
                        viewPager.setVisibility(View.VISIBLE);
                        ll_no_error.setVisibility(View.GONE);
                        tv_num.setText(1 + "/" + totalNum01);
                    }
                    showLog(totalNum01 + TAG);
                    initErrorNotCleanViewPager();
                }
            }
        });
    }

    /**------------------------------------------start ---------------------------------------*/

    /**
     * 初始化已扫除的view
     */
    private void initAlreadyCleanViewPager() {
        fragmentList.clear();
        for (int i = 1; i <= totalNum02; i++) {
            ErrorsClearFragment newFragment = new ErrorsClearFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("seqType", ErrorsClearFragment.SEQ_TYPE_WSC);
            bundle.putString("majorId", majorId);
            bundle.putString("err_status", "2");
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }
        HomePageAdapter homePageAdapter02 = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter02);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        homePageAdapter02.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/" + totalNum02);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 只为了获取已扫除总题量
     */
    private void getErrorAlreadyCleanList() {
        totalNum02 = 0;
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("err_status", "2");
        params.put("page", 1);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum02 = res.getData().getTotalCount();
                    if (totalNum02 == 0) {
                        ll_no_error.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.GONE);
                        tv_num.setText(0 + "/" + totalNum02);
                    } else {
                        viewPager.setVisibility(View.VISIBLE);
                        ll_no_error.setVisibility(View.GONE);
                        tv_num.setText(1 + "/" + totalNum02);
                    }
                    showLog(totalNum02 + TAG);
                    initAlreadyCleanViewPager();
                }
            }
        });
    }

    /**------------------------------------------ end ---------------------------------------*/

    /**
     * 初始化已掌握的view
     */
    private void initAlreadyGraspViewPager() {
        fragmentList.clear();
        for (int i = 1; i <= totalNum03; i++) {
            ErrorsClearFragment newFragment = new ErrorsClearFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putString("err_status", "3");
            bundle.putInt("seqType", ErrorsClearFragment.SEQ_TYPE_WSC);
            bundle.putString("majorId", majorId);
            newFragment.setArguments(bundle);
            fragmentList.add(newFragment);
        }
        HomePageAdapter homePageAdapter03 = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter03);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        homePageAdapter03.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/" + totalNum03);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 只为了获取已掌握总题量
     */
    private void getErrorAlreadyGraspList() {
        totalNum03 = 0;
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("err_status", "3");
        params.put("page", 1);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum03 = res.getData().getTotalCount();
                    if (totalNum03 == 0) {
                        ll_no_error.setVisibility(View.VISIBLE);
                        viewPager.setVisibility(View.GONE);
                        tv_num.setText(0 + "/" + totalNum03);
                    } else {
                        viewPager.setVisibility(View.VISIBLE);
                        ll_no_error.setVisibility(View.GONE);
                        tv_num.setText(1 + "/" + totalNum03);
                    }
                    showLog(totalNum03 + TAG);
                    initAlreadyGraspViewPager();
                }
            }
        });
    }
}
