package com.up.study.ui.errors;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.StringUtils;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.FullyLinearLayoutManager;
import com.up.common.widget.MyListView;
import com.up.common.widget.MyScrollview;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.base.BaseFragment;
import com.up.study.callback.CallBack;
import com.up.study.callback.ImgCallBack;
import com.up.study.model.ErrorClearnModel;
import com.up.study.model.ImgUrl;
import com.up.study.model.LcSeqModel;
import com.up.study.model.Options;
import com.up.study.weight.dialog.CommonDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题扫除Fragment 题目(答题模式，需要做题)
 */

public class ErrorsClearFragment extends BaseFragment {
    public final static int SEQ_TYPE_XST = 1;//相似题题目
    public final static int SEQ_TYPE_CTSC = 2;//错题扫除所有题目
    public final static int SEQ_TYPE_WSC = 3;//错题未扫除

    TextView tv_submit;
    TextView tv_subject_type;

    private LcSeqModel seqModel;
    private int page = 1;
    private int seqType = 1;//操作题目类型
    int modelType = 1;//1：刘全的接口数据，2：林城的接口数据

    private ErrorsClearActivity activity;

    //错题接口需要的参数
    private String majorId;//学科id
    private TextView tv_right;
    private TextView tv_error;
    private ImageView iv_1;
    private String err_status;
    private TextView tv_xst_exercise;
    private List<String> knowNameList = null;
    private List<String> answerList = null;
    private RecyclerView mRecyclerView;
    private List<LcSeqModel> subList = new ArrayList<>();
    private LinearLayout ll_anwser;
    private WebView wv_analysis;
    private LinearLayout ll_bottom;
    private ImageView error_iv_1, error_iv_2, error_iv_3;
    private ImageView error_iv_clear_1, error_iv_clear_2, error_iv_clear_3;
    private List<String> imgList;
    private LinearLayout ll_error;
    private ImageView iv_answer_error;
    private ImageView iv_answer_right;
    private ReceiverFinish receiverFinish;
    private MyScrollview scrollView;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("pageNo");
            seqType = args.getInt("seqType");
            majorId = args.getString("majorId");//学科id
            activity = (ErrorsClearActivity) context;
        }
        if (seqType == SEQ_TYPE_WSC) {
            err_status = args.getString("err_status");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("AnswerFragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        showLog("AnswerFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fra_errors_not_clear;
    }

    @Override
    protected void initView() {
        tv_submit = bind(R.id.tv_submit);//提交答案
        tv_error = bind(R.id.tv_error);
        tv_right = bind(R.id.tv_right);
        iv_1 = bind(R.id.iv_1);
        tv_subject_type = bind(R.id.tv_subject_type);
        ll_anwser = bind(R.id.ll_answer);
        wv_analysis = bind(R.id.wv_analysis);
        tv_xst_exercise = bind(R.id.tv_xst_exercise);
        ll_bottom = bind(R.id.ll_bottom);
        ll_error = bind(R.id.ll_error);
        error_iv_1 = bind(R.id.error_iv_1);
        error_iv_2 = bind(R.id.error_iv_2);
        error_iv_3 = bind(R.id.error_iv_3);
        error_iv_clear_1 = bind(R.id.error_iv_clear_1);
        error_iv_clear_2 = bind(R.id.error_iv_clear_2);
        error_iv_clear_3 = bind(R.id.error_iv_clear_3);
        iv_answer_error = bind(R.id.iv_answer_error);
        iv_answer_right = bind(R.id.iv_answer_right);
        scrollView = bind(R.id.scrollView);

        if (seqType == SEQ_TYPE_CTSC) {//错题扫除
            getErrorCleanList();
        } else if (seqType == SEQ_TYPE_WSC) {
            getErrorNotCleanList(err_status);
        }

        //找到这个Listview
        mRecyclerView = bind(R.id.recylist);
        //设置线性管理器
        FullyLinearLayoutManager ms = new FullyLinearLayoutManager(ctx);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(ms);

        //相似题回答正确通过发送广播修改当前题目的状态
        receiverFinish = new ReceiverFinish();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.FINISH_ERROR);
        ctx.registerReceiver(receiverFinish, filter);
    }

    @Override
    protected void initData() {}

    @Override
    protected void initEvent() {
        tv_submit.setOnClickListener(this);
        tv_error.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_xst_exercise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_error) {
            showAnswerErrorDialog();
        } else if (v == tv_right) {
            showAnswerRightDialog();
        } else if (v == tv_xst_exercise) {
            Map<String, Object> map = new HashMap<>();
            map.put("subId", seqModel.getSubjectId());
            map.put("majorId", majorId);
            gotoActivity(ErrorsSameActivity1.class, map);
        }
    }

    /**
     * 主观题答对了的dialog
     */
    private void showAnswerRightDialog() {
        new CommonDialog.Builder(activity)
                .setTitle(R.string.dialog_hint)
                .setMessage("做对了 ！")
                .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ll_bottom.setVisibility(View.GONE);
                        changeCorrectState("2", seqModel.getSubjectId());
                        //提交错题图片
                        StudyUtils.uploadImgUrl(imgList, ctx, new CallBack<List<ImgUrl>>() {
                            @Override
                            public void suc(List<ImgUrl> obj) {
                                submitPicture(obj);
                            }
                        });
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
    }

    /**
     * 提交错题图片
     *
     * @param obj
     */
    private void submitPicture(List<ImgUrl> obj) {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("err_attached", new Gson().toJson(obj));
        params.put("subject_id", seqModel.getSubjectId());
        J.http().post(Urls.ERROR_CLEAR_PICTURE, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    showLog("图片提交成功");
                }
            }
        });
    }

    /**
     * 主观题答错了的dialog
     */
    private void showAnswerErrorDialog() {
        new CommonDialog.Builder(activity)
                .setTitle(R.string.dialog_hint)
                .setMessage("再接再厉 ！")
                .setPositiveButton(R.string.next, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳转到下一题并且保存图片并且重新刷新本题
                        //提交错题图片
                        StudyUtils.uploadImgUrl(imgList, ctx, new CallBack<List<ImgUrl>>() {
                            @Override
                            public void suc(List<ImgUrl> obj) {
                                submitPicture(obj);
                            }
                        });
                        activity.next(page);   //
                        refresh(ll_error);
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_reform, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh(ll_error);
                    }
                }, R.color.colorPrimaryDark).show();
    }

    /**
     * 重新做题（没有刷新数据，而是隐藏显示的答案相关的控件）
     * @param ll_error
     */
    private void refresh(LinearLayout ll_error) {
        StudyUtils.reform(ctx, imgList, tv_submit, ll_bottom, ll_anwser, error_iv_1, error_iv_2, error_iv_3
                , error_iv_clear_1, error_iv_clear_2, error_iv_clear_3);
        ll_error.setVisibility(View.GONE);
    }

    /**
     * 错题扫描数据
     */
    private void getErrorCleanList() {
        subList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", page);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    if (!res.getData().getSubList().isEmpty()) {
                        seqModel = res.getData().getSubList().get(0);
                        subList = res.getData().getSubList();
                        modelType = 2;
                        init();
                    }
                }
            }
        });
    }

    /**
     * 错题扫出----未扫除数据
     *
     * @param err_status
     */
    private void getErrorNotCleanList(final String err_status) {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("err_status", err_status);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", page);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    if (!res.getData().getSubList().isEmpty()) {
                        seqModel = res.getData().getSubList().get(0);
                        modelType = 2;
                        init01();
                    }
                }
            }
        });
    }

    /**
     * 根据不同的状态加载不同的数据1：未扫除,2：已扫除,3：已掌握
     */
    private void init01() {
        switch (err_status) {
            case "1":
                errorNotClearData(0);
                break;
            case "2":
                errorAlreadyClearData(err_status);
                break;
            case "3":
                errorAlreadyClearData(err_status);
                break;
        }
    }

    private void init() {
        for (int i = 0; i < subList.size(); i++) {
            String type = subList.get(i).getErr_status();
            switch (type) {
                case "1":
                    errorNotClearData(0);    //未扫除
                    break;
                case "2":
                    errorAlreadyClearData(type); //已扫除
                    break;
                case "3":
                    errorAlreadyClearData(type); //已掌握
                    break;
            }
        }
    }

    /**
     * 设置已掌握数据---已扫除数据
     */
    private void errorAlreadyClearData(String err_status) {
        tv_subject_type.setText(seqModel.getSubjectType());
        String knowName = seqModel.getKnowName();
        ll_anwser.setVisibility(View.VISIBLE);
        if (err_status.equals("2")) {
            iv_1.setImageResource(R.mipmap.yisaochu);
            tv_xst_exercise.setVisibility(View.VISIBLE);
        } else {
            iv_1.setImageResource(R.mipmap.yizhangwo);
        }
        if (knowName != null) {
            if (modelType == 1) {
                Type type1 = new TypeToken<List<String>>() {
                }.getType();
                knowNameList = new Gson().fromJson(knowName, type1);
            } else if (modelType == 2) {
                knowNameList = Arrays.asList(knowName.split(","));
            }
            try {
                String answerjson = seqModel.getAnswer();
                Type type1 = new TypeToken<List<String>>() {
                }.getType();
                answerList = new Gson().fromJson(answerjson, type1);
            } catch (Exception e) {
                Logger.e(Logger.TAG, "答案解析异常，为HTML文本");
            }
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(knowNameList);
            mRecyclerView.setAdapter(myAdapter);
        }
        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {
            WebView wv_title = bind(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());
            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
            final MyListView lv = bind(R.id.lv);
            final CommonAdapter<Options> adapter = new CommonAdapter<Options>(ctx, optionsList, R.layout.item_topic_kgt) {
                @Override
                public void convert(ViewHolder vh, Options item, int position) {
                    TextView tv_answer = vh.getView(R.id.tv_answer);
                    TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                    tv_answer.setText(item.getOpt());
                    tv_answer_text.setText(item.getText());
                    if (answerList != null && answerList.size() > 0) {
                        if (seqModel.getSubjectType().contains("单选") || seqModel.getSubjectType().contains("判断")) {
                            if (answerList.get(0).equals(item.getOpt())) {
                                tv_answer.setBackgroundResource(R.drawable.round_blue_circle_background);
                            } else {
                                tv_answer.setBackgroundResource(R.drawable.round_gray_circle_background);
                            }
                        } else if (seqModel.getSubjectType().contains("多选")) {
                            for (int j = 0; j < answerList.size(); j++) {
                                if (answerList.get(j).equals(item.getOpt())) {
                                    tv_answer.setBackgroundResource(R.drawable.round_blue_circle_background);
                                }
                            }
                        }
                    }
                }
            };
            lv.setAdapter(adapter);
            WebView wv_answer = bind(R.id.wv_answer);
            WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
            WebView wv_analysis = bind(R.id.wv_analysis);
            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
        }
        //主观题初始化内容
        else {
            WebView webView = bind(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());
            WebView wv_answer = bind(R.id.wv_answer);
            WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
            WebView wv_analysis = bind(R.id.wv_analysis);
            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
            MyListView lv = bind(R.id.lv);
            lv.setVisibility(View.GONE);
        }
        setScrollView(); //加载完数据后设置ScrollView在顶部
        StudyUtils.showSeqErrorCauseAndImg(this, seqModel);
    }

    /**
     * 设置未扫除数据
     */
    private void errorNotClearData(int state) {
        if (state == 1){
            ll_bottom.setVisibility(View.GONE);
            ll_anwser.setVisibility(View.GONE);
            iv_answer_error.setVisibility(View.GONE);
            iv_answer_right.setVisibility(View.GONE);
            ll_error.setVisibility(View.GONE);
        }
        iv_1.setImageResource(R.mipmap.weisaochu);
        tv_subject_type.setText(seqModel.getSubjectType());
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            List<String> list2 = Arrays.asList(knowName.split(","));
            //找到这个Listview
            RecyclerView mRecyclerView = bind(R.id.recylist);
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
            WebView wv_title = bind(R.id.wv_title);
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
            if (state == 1){
                lv.setTag(null);
            }
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
                            submitAnswer();
                            lv.setTag(0);
                            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
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
                            WebView wv_answer = bind(R.id.wv_answer);
                            WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                            if (answer.equals(optionsList.get(position).getOpt())) {//回答正确
                                optionsList.get(position).setStatus(1);
                                adapter.notifyDataSetChanged();
                                iv_answer_right.setVisibility(View.VISIBLE);
                                tv_submit.setVisibility(View.VISIBLE);
                                tv_submit.setText("相识题练习");
                                tv_submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("subId", seqModel.getSubjectId());
                                        map.put("majorId", majorId);
                                        gotoActivity(ErrorsSameActivity1.class, map);
                                    }
                                });
                                //如果题目答对更改错题状态
                                changeCorrectState("2", seqModel.getSubjectId());
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
                    submitAnswer();
                    if (seqModel.getSubjectType().contains("多选")) {//多选题提交答案
                        tv_submit.setVisibility(View.GONE);
                        if (seqType == SEQ_TYPE_CTSC) {//错题扫除
                            ll_bottom.setVisibility(View.VISIBLE);
                        }
                        WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
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
                            WebView wv_answer = bind(R.id.wv_answer);
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
            final String[] localImgList = new String[3];
            imgList = new ArrayList<String>();
            WebView webView = bind(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());
            MyListView lv = (MyListView) bind(R.id.lv);
            lv.setVisibility(View.GONE);
            LinearLayout ll_camera = (LinearLayout) bind(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);
            StudyUtils.initImgView(root,imgList, this.getActivity(),
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            localImgList[0] = imgPath;
                            StringUtils.Log(localImgList);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            localImgList[0] = imgPath;
                            StringUtils.Log(localImgList);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            localImgList[0] = imgPath;
                            StringUtils.Log(localImgList);
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
                    submitAnswer();
                    if (seqType == SEQ_TYPE_CTSC) {//错题扫除
                        ll_bottom.setVisibility(View.VISIBLE);
                    } else if (seqType == SEQ_TYPE_WSC) {
                        ll_bottom.setVisibility(View.VISIBLE);
                    }
                    tv_submit.setVisibility(View.GONE);
                    WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                    ll_anwser.setVisibility(View.VISIBLE);
                    WebView wv_answer = bind(R.id.wv_answer);
                    WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                }
            });
        }
        setScrollView();
    }

    /**
     * 设置ScrollView在顶部
     */
    public void setScrollView(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    /**
     * 未扫除的题目如果回答正确更改状态为已扫除
     *
     * @param state     需要更改的状态
     * @param subjectId 题目id
     */
    private void changeCorrectState(String state, String subjectId) {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", page);
        params.put("subject_id", subjectId);
        params.put("err_status", state);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    tv_xst_exercise.setVisibility(View.VISIBLE);
                    iv_1.setImageResource(R.mipmap.yisaochu);
                    error_iv_clear_1.setVisibility(View.GONE);
                    error_iv_clear_2.setVisibility(View.GONE);
                    error_iv_clear_3.setVisibility(View.GONE);
                    error_iv_1.setClickable(false);
                    error_iv_2.setClickable(false);
                    error_iv_3.setClickable(false);
                }
            }
        });
    }

    /**
     * 客观题答题错误显示的dialog
     */
    private void showErrorDialog() {
        new CommonDialog.Builder(activity)
                .setTitle(R.string.dialog_hint)
                .setMessage("很遗憾，您答错了！本题未能扫除")
                .setPositiveButton(R.string.next, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.next(page);
                        errorNotClearData(1);
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_confirm, null, R.color.colorPrimaryDark).show();
    }

    /**
     * 提交答案操作
     */
    private void submitAnswer() {
        StudyUtils.showSeqErrorCauseAndImg(this, seqModel);
    }

    /**
     * 刷新界面
     */
    public class ReceiverFinish extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            iv_1.setImageResource(R.mipmap.yizhangwo);
            tv_xst_exercise.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiverFinish != null) {
            ctx.unregisterReceiver(receiverFinish);
        }
    }
}
