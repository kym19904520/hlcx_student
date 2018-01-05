package com.up.study.ui.errors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.HomePageAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ErrorClearnModel;
import com.up.study.model.LcSeqModel;
import com.up.study.weight.dialog.CommonDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 相似题练习(一题题获取)
 */
public class ErrorsSameActivity1 extends BaseFragmentActivity {

    @Bind(R.id.tv_error_text)
    TextView tvErrorText;
    @Bind(R.id.ll_no_error)
    LinearLayout llNoError;
    private ViewPager viewPager;
    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private TextView tv_num;
    private int totalNum;
    private String subId;  //题目id
    private String majorId;//学科id
    private int currentIndex = 0;//当前页
    private int kgtTotalNum;//客观题总题量
    private int zgtTotalNum;//主观题总题量
    private int zgtCorrectNumber;   //主观题答题正确的个数
    private int kgtCorrectNumber;   //客观题答题正确的个数
    private int kgtErrorNumber;     //客观题答题错误的个数
    private List<Integer> zgtCorrectNumberList = new ArrayList<>(); //保存主观题答题正确的集合
    private List<Integer> kgtCorrectNumberList = new ArrayList<>(); //保存客观题答题正确的集合
    private List<Integer> numberList = new ArrayList<>();           //所有题目的个数
    private List<LcSeqModel> mSeqList = new ArrayList<>();
    private List<Integer> kgtErrorNumberList = new ArrayList<>();  //保存客观题错误个数的集合
    private String nextNumber="";       //记录下一题
    private List<String> nextList;     //保存下一题的集合
    private List<String> list = new ArrayList<>();


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast("onNewIntent");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_errors_same;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initData() {
        subId = (String) getMap().get("subId");
        majorId = (String) getMap().get("majorId");
        getSameSeqList();
    }

    /**
     * 获取客观题的总个数
     *
     * @param isKgt
     */
    public void kgtNumber(boolean isKgt) {
        if (isKgt) {
            kgtTotalNum++;
            numberList.add(kgtTotalNum);
        }
    }

    /**
     * 获取主观题的总个数
     *
     * @param isKgt
     */
    public void zgtNumber(boolean isKgt) {
        if (isKgt) {
            zgtTotalNum++;
            numberList.add(zgtTotalNum);
        }
    }

    /**
     * 客观题题目答题正确的个数
     *
     * @param isKgt
     */
    public void kgtTotal(boolean isKgt) {
        if (isKgt) {
            kgtCorrectNumber++;
            kgtCorrectNumberList.add(kgtCorrectNumber);
        }
    }

    /**
     * 客观题题目答题错误的个数
     */
    public void kgtError(boolean isKgt) {
        if (isKgt) {
            kgtErrorNumber++;
            kgtErrorNumberList.add(kgtErrorNumber);
        }
    }

    /**
     * 主观题题目答题正确的个数
     *
     * @param isKgt
     */
    public void zgtTotal(boolean isKgt) {
        if (isKgt) {
            zgtCorrectNumber++;
            zgtCorrectNumberList.add(zgtCorrectNumber);
        }
    }

    /**
     * 题目全部做完的提示
     */
    public void accomplishAnswer(){
        String[] str = null;
        if (!TextUtils.isEmpty(nextNumber)) {
            str = nextNumber.split(",");
            for (int i = 0;i < str.length;i++){
                list.add(str[i]);
            }
            removeDuplicate(list);
        }
        if (totalNum == zgtCorrectNumberList.size() + kgtCorrectNumberList.size() +
                kgtErrorNumberList.size() + list.size()){
            showToast("全部做完了,点击提交结束相似题练习");
        }else if (totalNum == zgtCorrectNumberList.size() + kgtCorrectNumberList.size()){
            showToast("全部做对了,点击提交结束相似题练习");
        }
    }

    /**
     * 下一题
     */
    public void next(int index) {
        nextNumber+=index + ",";
        currentIndex = index;
        if (index >= fragmentList.size()) {
            new CommonDialog.Builder(this)
                    .setTitle(R.string.dialog_hint)
                    .setMessage(R.string.dialog_next)
                    .setPositiveButton(R.string.dialog_confirm, null, R.color.colorPrimaryDark)
                    .show();
            return;
        }
        viewPager.setCurrentItem(index);
    }

    private void initViewPager() {
        fragmentList.clear();
        for (int i = 1; i <= totalNum; i++) {
            AnswerFragment newFragment = new AnswerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pageNo", i);
            bundle.putInt("seqType", AnswerFragment.SEQ_TYPE_XST);
            bundle.putString("subId", subId);
            bundle.putString("majorId", majorId);
            bundle.putSerializable("mSeqList", (Serializable) mSeqList);
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
                tv_num.setText(position + 1 + "/" + totalNum);
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
     * 就是为了获取总量
     */
    private void getSameSeqList() {
        HttpParams params = new HttpParams();
        params.put("subject_id", subId);//题目id
        params.put("page", 1);
        params.put("rows", 1);
        J.http().post(Urls.SAME_SEQ, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum = res.getData().getTotalCount();
                    mSeqList = res.getData().getSubList();
                    if (totalNum == 0) {
                        viewPager.setVisibility(View.GONE);
                        llNoError.setVisibility(View.VISIBLE);
                        tvErrorText.setText("未找到对应的相似题 ！");
                        return;
                    }
                    tv_num.setText(1 + "/" + totalNum);
                    initViewPager();
                }
            }
        });
    }

    /**
     * 已扫除的题目如果回答正确更改状态为已掌握
     *
     * @param state 需要更改的状态
     */
    private void changeCorrectState(String state) {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("subject_id", subId);
        params.put("err_status", state);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.FINISH_ERROR);
                    sendBroadcast(intent);   //发送广播
                    ErrorsSameActivity1.this.finish();
                }
            }
        });
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        xstSubmit();
    }

    /**
     * 集合去重
     */
    public List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 相似题提交
     */
    private void xstSubmit() {
        String dialogMessage = "";
        String[] str = nextNumber.split(",");
        nextList = new ArrayList<>();
        for (int i = 0;i < str.length;i++){
            nextList.add(str[i]);
        }
        removeDuplicate(nextList);
        if (zgtCorrectNumberList.size() + kgtCorrectNumberList.size() + kgtErrorNumberList.size() + nextList.size() != totalNum){
            dialogMessage = "您做错了" + (totalNum - (zgtCorrectNumberList.size() + kgtCorrectNumberList.size() +
                    kgtErrorNumberList.size())) + "题,是否结束相似题练习？";
        }else {
            dialogMessage = "全部做完了,您做错了" + (kgtErrorNumberList.size() + nextList.size()) + "题,是否结束相似题练习？";
        }
        if (totalNum == zgtCorrectNumberList.size() + kgtCorrectNumberList.size()) {
            dialogMessage = getString(R.string.dialog_message_01);
        }
        new CommonDialog.Builder(this)
                .setTitle(R.string.dialog_hint)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (numberList.size() == zgtCorrectNumberList.size() + kgtCorrectNumberList.size()){
                            changeCorrectState("3");
                        }else {
                            ErrorsSameActivity1.this.finish();
                        }
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
    }
}
