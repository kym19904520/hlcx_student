package com.up.study.ui.errors;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.ImgCallBack;
import com.up.study.model.ErrorClearnModel;
import com.up.study.model.LcSeqModel;
import com.up.study.model.Options;
import com.up.study.weight.dialog.CommonDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 相似题练习（直接获取全部的5道题）（暂时不用）
 */
public class ErrorsSameActivity extends BaseFragmentActivity {

    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();
    private TopicPagerAdapter adapter;
    private List<LcSeqModel> mSeqList = new ArrayList<LcSeqModel>();

    private TextView tv_num;
    private String subId;       //题目id
    private String majorId;//学科id
    private int totalNum;
    private int currentIndex = 0;//当前页
    private int index;
    private int kgtTotalNum;//客观题总题量
    private int zgtTotalNum;//主观题总题量
    private int zgtCorrectNumber;   //主观题答题正确的个数
    private int kgtCorrectNumber;   //客观题答题正确的个数
    private List<Integer> zgtList = new ArrayList<>();
    private List<Integer> kgtList = new ArrayList<>();
    private List<Integer> numberList = new ArrayList<>();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_errors_same;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initEvent() {}

    @Override
    protected void initData() {
        subId = (String) getMap().get("subId");
        majorId = (String) getMap().get("majorId");
        getSameSeqList();
    }

    /**
     * 下一题
     */
    public void next() {
        if (index > totalNum){
            showToast("没有下一题");
            return;
        }
        viewPager.setCurrentItem(index+1);
    }

    private void getSameSeqList() {
        HttpParams params = new HttpParams();
        params.put("subject_id", subId);//题目id
        params.put("page", 1);
        params.put("rows",1);
        J.http().post(Urls.SAME_SEQ, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(ctx) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    totalNum = res.getData().getTotalCount();
                    if (totalNum == 0) {
                        showToast("未找到对应的相似题！");
                        return;
                    }
                    mSeqList = res.getData().getSubList();
                    initSeqCardData(0);
                }
            }
        });
    }

    /**
     * 主观题答对了的dialog
     */
    private void showAnswerRightDialog() {
        new CommonDialog.Builder(this)
                .setTitle("提示")
                .setMessage("做对了！")
                .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //如果3道题全部答对该题目的状态改为已掌握
                        zgtCorrectNumber++;
                        zgtList.add(zgtCorrectNumber);
                        showLog(numberList.size() + "---答对答错的数量"+ zgtList.size());
                        if (zgtList.size() == numberList.size()){
                            changeCorrectState("3");
                        }
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
    }

    /**
     * 主观题答错了的dialog
     */
    private void showAnswerErrorDialog() {
        new CommonDialog.Builder(this)
                .setTitle("提示")
                .setMessage("再接再厉！")
                .setPositiveButton(R.string.next, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳转到下一题并且保存图片
                        next();
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_reform, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initSeqCardData(1);
                    }
                }, R.color.colorPrimaryDark).show();
    }


    private void initSeqCardData(int state) {
        mInflater = getLayoutInflater();
        for (int i = 0; i < mSeqList.size(); i++) {
            View view = mInflater.inflate(R.layout.fra_errors_not_clear, null);
            initInflaterView(view, mSeqList.get(i),state);
            mViews.add(view);
        }
        tv_num.setText(1 + "/" + mSeqList.size());

        adapter = new TopicPagerAdapter(mViews);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.setCurrentItem(currentIndex);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                index = position + 1;
                tv_num.setText(index + "/" + mSeqList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void initInflaterView(final View view, final LcSeqModel seqModel, final int state) {
        TextView tv_subject_type = (TextView) view.findViewById(R.id.tv_subject_type);
        final LinearLayout ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        if (state == 1){
            ll_bottom.setVisibility(View.GONE);
        }
        final TextView tv_error = (TextView) view.findViewById(R.id.tv_error);
        final TextView tv_right = (TextView) view.findViewById(R.id.tv_right);
        tv_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswerErrorDialog();
                ll_bottom.setVisibility(View.GONE);
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswerRightDialog();
                ll_bottom.setVisibility(View.GONE);
            }
        });
        tv_subject_type.setText(seqModel.getSubjectType());
        final TextView tv_submit = (TextView) view.findViewById(R.id.tv_submit);//提交答案
        if (state == 1){
            tv_submit.setVisibility(View.VISIBLE);
        }
        //需要提交按钮吗
        if (StudyUtils.getNeedSumitBtn(seqModel.getSubjectType())) {
            tv_submit.setVisibility(View.VISIBLE);
        }
        //tv_submit.bringToFront();
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            List<String> list2 = Arrays.asList(knowName.split(","));
            //找到这个Listview
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recylist);
            //设置线性管理器
            LinearLayoutManager ms = new LinearLayoutManager(ctx);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            mRecyclerView.setLayoutManager(ms);
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(list2);
            mRecyclerView.setAdapter(myAdapter);
        }
        //需要提交按钮吗
        if (StudyUtils.getNeedSumitBtn(seqModel.getSubjectType())) {
            tv_submit.setVisibility(View.VISIBLE);
        }
        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {
            kgtTotalNum++;
            numberList.add(kgtTotalNum);
            WebView wv_title = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());
            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
            final MyListView lv = bind(R.id.lv);
            if (optionsList == null || optionsList.size() == 0) {
                showLog("该题无选项");
                return;
            }
            final CommonAdapter<Options> adapter = new CommonAdapter<Options>(ctx, optionsList, R.layout.item_topic_kgt) {
                @Override
                public void convert(ViewHolder vh, Options item, int position) {
                    TextView tv_answer = vh.getView(R.id.tv_answer);
                    TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                    tv_answer.setText(item.getOpt());
                    tv_answer_text.setText(item.getText());
                    if (item.getStatus() == 0) {
                        tv_answer.setBackgroundResource(R.drawable.round_gray_circle_background);
                    }
                    if (item.getStatus() == 1) {
                        tv_answer.setBackgroundResource(R.drawable.round_blue_circle_background);
                    } else if (item.getStatus() == 2) {
                        tv_answer.setBackgroundResource(R.drawable.round_pink_circle_background);
                    } else if (item.getStatus() == 3) {
                        tv_answer.setBackgroundResource(R.drawable.round_gray_s_circle_background);
                    } else if (item.getStatus() == 4) {
                        tv_answer.setBackgroundResource(R.drawable.round_blue_q_circle_background);
                    }
                }
            };
            lv.setAdapter(adapter);
            //点击选项
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                    if (seqModel.getSubjectType().contains("多选")) {
                        if (optionsList.get(position).getStatus() == 3) {
                            optionsList.get(position).setStatus(0);
                        } else {
                            optionsList.get(position).setStatus(3);
                        }
                        adapter.notifyDataSetChanged();
                    } else {//单选和判断
                        if (lv.getTag() == null) {//只能点击一次
//                            submitAnswer();
                            lv.setTag(0);
                            LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                            WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                            ImageView iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
                            ImageView iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);
                            ll_anwser.setVisibility(View.VISIBLE);
                            String answer = "";
                            try {
                                String answerjson = seqModel.getAnswer();
                                Type type1 = new TypeToken<List<String>>() {
                                }.getType();
                                List<String> answerList = new Gson().fromJson(answerjson, type1);

                                if (answerList != null && answerList.size() > 0) {
                                    answer = answerList.get(0);
                                }
                            } catch (Exception e) {
                                showLog("答案格式异常");
                            }
                            WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                            WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                            if (answer.equals(optionsList.get(position).getOpt())) {//回答正确
                                optionsList.get(position).setStatus(1);
                                adapter.notifyDataSetChanged();
                                iv_answer_right.setVisibility(View.VISIBLE);
                                kgtCorrectNumber++;
                                kgtList.add(kgtCorrectNumber);
                                showLog(numberList.size() + "---答对答错的数量"+ kgtList.size());
                                if (kgtList.size() == numberList.size()){
                                    changeCorrectState("3");
                                }
                            } else {//回答错误
                                optionsList.get(position).setStatus(2);
                                for (int i = 0; i < optionsList.size(); i++) {
                                    if (answer.equals(optionsList.get(i).getOpt())) {
                                        optionsList.get(i).setStatus(1);
                                        break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                iv_answer_error.setVisibility(View.VISIBLE);
                                tv_submit.setVisibility(View.GONE);
                                showErrorDialog();
                            }
                        }
                    }
                }
            });
            //提交答案
            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    submitAnswer();
                    if (seqModel.getSubjectType().contains("多选")) {//多选题提交答案
                        tv_submit.setVisibility(View.GONE);
                        LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                        ll_bottom.setVisibility(View.VISIBLE);
                        WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                        WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                        ImageView iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
                        ImageView iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);
                        if (state == 1){
                            iv_answer_error.setVisibility(View.GONE);
                            iv_answer_right.setVisibility(View.GONE);
                            ll_anwser.setVisibility(View.GONE);
                        }
                        ll_anwser.setVisibility(View.VISIBLE);
                        String answerjson = seqModel.getAnswer();
                        Type type1 = new TypeToken<List<String>>() {
                        }.getType();
                        List<String> answerList = new Gson().fromJson(answerjson, type1);
                        if (answerList == null) {
                            answerList = new ArrayList<>();
                        }
                        String answer = "";
                        for (int i = 0; i < answerList.size(); i++) {
                            answer += answerList.get(i) + ",";
                        }
                        if (answer.length() > 0) {
                            WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                            WidgetUtils.initWebView(wv_answer, answer.substring(0, answer.length() - 1));
                        }
                        //该题错与对逻辑：回答正确数目和答案数目一直、并且选中数目和答案数目一致，就是对的。
                        //选项错与对逻辑：选了要么错要么对，不选要么错，要么无状态
                        int rightNum = 0;//回答正确的数目
                        int selectNum = 0;//选中数目
                        for (int i = 0; i < optionsList.size(); i++) {
                            if (optionsList.get(i).getStatus() == 3) {//选中状态
                                selectNum++;
                                boolean isHaving = false;//判断是否有对应的答案
                                for (int j = 0; j < answerList.size(); j++) {
                                    if (answerList.get(j).equals(optionsList.get(i).getOpt())) {
                                        isHaving = true;
                                        break;
                                    }
                                }
                                if (isHaving) {//选了要么错要么对
                                    rightNum++;
                                    optionsList.get(i).setStatus(1);
                                } else {
                                    optionsList.get(i).setStatus(2);
                                }
                            } else {//未选中状态
                                boolean isHaving = false;//判断是否有对应的答案
                                for (int j = 0; j < answerList.size(); j++) {
                                    if (answerList.get(j).equals(optionsList.get(i).getOpt())) {
                                        isHaving = true;
                                        break;
                                    }
                                }
                                if (isHaving) {//不选要么错，要么无状态
                                    optionsList.get(i).setStatus(4);//未选中却在正确答案里，所以这个选项错了
                                } else {
                                    optionsList.get(i).setStatus(0);
                                }
                            }
                        }
                        if (rightNum == answerList.size() && rightNum == selectNum) {//回答正确  回答正确数目和答案数目一直，并且选中数目和答案数目一致,就是对的。
                            adapter.notifyDataSetChanged();
                            iv_answer_right.setVisibility(View.VISIBLE);
                        } else {//回答错误
                            adapter.notifyDataSetChanged();
                            iv_answer_error.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
        //主观题初始化内容
        else {
            zgtTotalNum++;
            numberList.add(zgtTotalNum);
            final List<String> imgList = new ArrayList<String>();
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);

            LinearLayout ll_camera = (LinearLayout) view.findViewById(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);
            if (state == 1){
                ImageView error_iv_1 = (ImageView) view.findViewById(R.id.error_iv_1);
                ImageView error_iv_2 = (ImageView) view.findViewById(R.id.error_iv_2);
                ImageView error_iv_3 = (ImageView) view.findViewById(R.id.error_iv_3);
                ImageView error_iv_clear_1 = (ImageView) view.findViewById(R.id.error_iv_clear_1);
                ImageView error_iv_clear_2 = (ImageView) view.findViewById(R.id.error_iv_clear_2);
                ImageView error_iv_clear_3 = (ImageView) view.findViewById(R.id.error_iv_clear_3);
                error_iv_1.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_2.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                error_iv_3.setImageBitmap(ImageUtil.readBitMap(ctx, R.mipmap.sy_add_img));
                imgList.clear();
            }

            StudyUtils.initImgView(view, imgList, this,
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            // showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            // showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            // showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    }
            );
            tv_submit.setOnClickListener(new View.OnClickListener() {//都要传图片的，填空，计算，应用，解答
                @Override
                public void onClick(View v) {
                    if (imgList.size() <= 0) {
                        showToast("请选择答题图片");
                        return;
                    }
//                    submitAnswer();
                    tv_submit.setVisibility(View.GONE);
                    ll_bottom.setVisibility(View.VISIBLE);
                    LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                    WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                    WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                    ll_anwser.setVisibility(View.VISIBLE);
                    WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                    WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {}

    /**
     * 答题错误显示的dialog
     *
     */
    private void showErrorDialog() {
        new CommonDialog.Builder(this)
                .setTitle("提示")
                .setMessage("很遗憾，您答错了！本题未能扫除")
                .setPositiveButton(R.string.next, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        activity.next(page);
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
    }

    /**
     * 已扫除的题目如果回答正确更改状态为已掌握
     *
     * @param state     需要更改的状态
     */
    private void changeCorrectState(String state) {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id",majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", 1);
        params.put("subject_id", subId);
        params.put("err_status", state);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    showToast("更改为已扫除");
                }
            }
        });
    }
}
