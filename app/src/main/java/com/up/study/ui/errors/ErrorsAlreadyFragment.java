package com.up.study.ui.errors;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.up.common.utils.DensityUtil;
import com.up.common.utils.Logger;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorClearnModel;
import com.up.study.model.LcSeqModel;
import com.up.study.model.Options;
import com.up.study.weight.BottomScrollView;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题扫除----------已掌握，已扫除fragment（暂时不用）
 */

public class ErrorsAlreadyFragment extends BaseFragment {
    public final static int SEQ_TYPE_YZW = 1;//已掌握
    public final static int SEQ_TYPE_CTXQ = 2;//已扫除

    private BottomScrollView bottomScrollView;
    private TextView tv_subject_type;
    private TextView tv_xst_exercise;

    private LcSeqModel seqModel;
    int modelType = 1;//1：刘全的接口数据，2：林城的接口数据

    int page = 1;
    int seqType = 1;//操作题目类型
    int subjectId = 0;//题目id

    //错题接口需要的参数
    private String majorId;//学科id
    private String errorStatus;//错题的状态
    private ImageView iv_1;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("pageNo");
            seqType = args.getInt("seqType");
            subjectId = args.getInt("subjectId");//试题解析 会用到
            majorId = args.getString("majorId");//学科id
            errorStatus = args.getString("err_status");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("Fragment onCreate");
    }

    @Override
    protected int getContentViewId() {
        showLog("Fragment getContentViewId");
        return R.layout.fra_solution;
    }

    @Override
    protected void initView() {
        bottomScrollView = bind(R.id.bottomScrollView);
        tv_subject_type = bind(R.id.tv_subject_type);
        tv_xst_exercise = bind(R.id.tv_xst_exercise);
        iv_1 = bind(R.id.iv_1);
        if (seqType == SEQ_TYPE_CTXQ) {//已移除错题库题目
            iv_1.setImageResource(R.mipmap.yisaochu);
            tv_xst_exercise.setVisibility(View.VISIBLE);
            bottomScrollView.setPadding(0, 0, 0, DensityUtil.dip2px(mParentActivity, 55));
        }else {
            iv_1.setImageResource(R.mipmap.yizhangwo);
        }
    }

    @Override
    protected void initData() {
        getErrorCleanList();
    }

    /**
     * 错题扫描数据-错题详情
     */
    private void getErrorCleanList() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("page", page);
        params.put("err_status",errorStatus);
        params.put("rows", 1);
        J.http().post(Urls.ERROR_CLEAR_LIST, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    if (!res.getData().getSubList().isEmpty()) {
                        seqModel = res.getData().getSubList().get(0);
                        modelType = 2;
                        init();
                    }
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        tv_xst_exercise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_xst_exercise){
            gotoActivity(ErrorsSameActivity.class,"subId",seqModel.getSubjectId());
        }
    }

    private List<String> knowNameList = null;
    private List<String> answerList = null;
    private void init() {
        tv_subject_type.setText(seqModel.getSubjectType());
        String knowName = seqModel.getKnowName();
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
            //找到这个Listview
            RecyclerView mRecyclerView = bind(R.id.recylist);
            //设置线性管理器
            LinearLayoutManager ms = new LinearLayoutManager(ctx);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            mRecyclerView.setLayoutManager(ms);
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

            WebView wv_analysis = (WebView) bind(R.id.wv_analysis);
            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());

            MyListView lv = (MyListView) bind(R.id.lv);
            lv.setVisibility(View.GONE);
        }
        StudyUtils.showSeqErrorCauseAndImg(this, seqModel);
    }
}
