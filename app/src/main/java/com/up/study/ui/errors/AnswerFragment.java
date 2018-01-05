package com.up.study.ui.errors;

import android.app.Activity;
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
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.base.BaseFragment;
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
 * Fragment 题目(答题模式，需要做题)
 * Created by Administrator on 2017/6/29 0029.
 */

public class AnswerFragment extends BaseFragment {
    public final static int SEQ_TYPE_XST = 1;//相似题题目

    TextView tv_submit;
    TextView tv_subject_type;

    private LcSeqModel seqModel;
    private int page = 1;
    private int seqType = 1;//操作题目类型

    private ErrorsSameActivity1 activity;

    //相似题接口需要的参数
    private String subId;
    private LinearLayout ll_bottom;
    private TextView tv_error;
    private TextView tv_right;
    private WebView wv_analysis;
    private LinearLayout ll_answer;
    private String majorId;   //学科id
    private List<String> imgList;
    private ImageView error_iv_1,error_iv_2,error_iv_3;
    private ImageView error_iv_clear_1,error_iv_clear_2,error_iv_clear_3;
    private RecyclerView mRecyclerView;
    private ImageView iv_answer_error;
    private ImageView iv_answer_right;
    private MyListView lv;
    private ArrayList<LcSeqModel> mSeqList = new ArrayList<>();

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {
            page = args.getInt("pageNo");
            seqType = args.getInt("seqType");
            subId = args.getString("subId");
            activity = (ErrorsSameActivity1) context;
            majorId = args.getString("majorId");
            mSeqList = (ArrayList<LcSeqModel>) args.getSerializable("mSeqList");
        }
        if (page == 1){
            seqModel = mSeqList.get(0);
        }else if (page == 2){
            seqModel = mSeqList.get(1);
        }else if (page == 3){
            seqModel = mSeqList.get(2);
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
        showLog("AnswerFragment getContentViewId");
        return R.layout.fra_errors_not_clear;
    }

    @Override
    protected void initView() {
        tv_submit = bind(R.id.tv_submit);//提交答案
        tv_subject_type = bind(R.id.tv_subject_type);
        tv_error = bind(R.id.tv_error);
        tv_right = bind(R.id.tv_right);
        ll_bottom = bind(R.id.ll_bottom);
        wv_analysis = bind(R.id.wv_analysis);
        ll_answer = bind(R.id.ll_answer);
        error_iv_1 = bind(R.id.error_iv_1);
        error_iv_2 = bind(R.id.error_iv_2);
        error_iv_3 = bind(R.id.error_iv_3);
        error_iv_clear_1 = bind(R.id.error_iv_clear_1);
        error_iv_clear_2 = bind(R.id.error_iv_clear_2);
        error_iv_clear_3 = bind(R.id.error_iv_clear_3);
        iv_answer_error = bind(R.id.iv_answer_error);
        iv_answer_right = bind(R.id.iv_answer_right);
        lv = bind(R.id.lv);

        //找到这个Listview
        mRecyclerView = bind(R.id.recylist);
        //设置线性管理器
        LinearLayoutManager ms = new LinearLayoutManager(ctx);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setLayoutManager(ms);

        if (seqType == SEQ_TYPE_XST) {
            init(0);//相似题
        }
    }

    @Override
    protected void initData() {}

    @Override
    protected void initEvent() {
        tv_error.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_error) {
            showAnswerErrorDialog();
        } else if (v == tv_right) {
            showAnswerRightDialog();
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
                        //如果3道题全部答对该题目的状态改为已掌握
                        ll_bottom.setVisibility(View.GONE);
                        error_iv_clear_1.setVisibility(View.GONE);
                        error_iv_clear_2.setVisibility(View.GONE);
                        error_iv_clear_3.setVisibility(View.GONE);
                        error_iv_1.setClickable(false);
                        error_iv_2.setClickable(false);
                        error_iv_3.setClickable(false);
                        activity.zgtTotal(true);
                        activity.accomplishAnswer();
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
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
                        //跳转到下一题并且保存图片
                        activity.next(page);
                        activity.accomplishAnswer();
                        StudyUtils.reform(ctx,imgList,tv_submit,ll_bottom,ll_answer,error_iv_1,error_iv_2,error_iv_3
                                ,error_iv_clear_1,error_iv_clear_2,error_iv_clear_3);
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_reform, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StudyUtils.reform(ctx,imgList,tv_submit,ll_bottom,ll_answer,error_iv_1,error_iv_2,error_iv_3
                                ,error_iv_clear_1,error_iv_clear_2,error_iv_clear_3);
                    }
                }, R.color.colorPrimaryDark).show();
    }

    /**
     * 相似题数据
     */
    private void getSameSeqList() {
        HttpParams params = new HttpParams();
        params.put("subject_id", subId);//题目id
        params.put("page", page);
        params.put("rows", 1);
//        params.put("majorId",majorId);
        J.http().post(Urls.SAME_SEQ, ctx, params, new HttpCallback<Respond<ErrorClearnModel>>(null) {
            @Override
            public void success(Respond<ErrorClearnModel> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    if (res.getData().getSubList() != null && res.getData().getSubList().size() > 0) {
                        seqModel = res.getData().getSubList().get(0);
                        init(0);
                    }
                }
            }
        });
    }

    private void init(int state) {
        if (state == 1){
            ll_bottom.setVisibility(View.GONE);
            ll_answer.setVisibility(View.GONE);
            iv_answer_error.setVisibility(View.GONE);
            iv_answer_right.setVisibility(View.GONE);
            lv.setTag(null);
        }
        tv_subject_type.setText(seqModel.getSubjectType());
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            List<String> list2 = Arrays.asList(knowName.split(","));
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(list2);
            mRecyclerView.setAdapter(myAdapter);
        }
        //需要提交按钮吗
        if (StudyUtils.getNeedSumitBtn(seqModel.getSubjectType())) {
            tv_submit.setVisibility(View.VISIBLE);
        }
        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {
            activity.kgtNumber(true);
            WebView wv_title = bind(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());
            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
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
                            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                            ll_answer.setVisibility(View.VISIBLE);
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
                                activity.kgtTotal(true);
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
                        ll_bottom.setVisibility(View.VISIBLE);
                        WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                        ImageView iv_answer_error = (ImageView) bind(R.id.iv_answer_error);
                        ImageView iv_answer_right = (ImageView) bind(R.id.iv_answer_right);
                        ll_answer.setVisibility(View.VISIBLE);
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
            activity.zgtNumber(true);
            imgList = new ArrayList<String>();

            WebView webView = bind(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) bind(R.id.lv);
            lv.setVisibility(View.GONE);

            LinearLayout ll_camera = (LinearLayout) bind(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);

            StudyUtils.initImgView(root, imgList, this.getActivity(),
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {

                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {

                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {

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
                    WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                    ll_answer.setVisibility(View.VISIBLE);
                    WebView wv_answer = bind(R.id.wv_answer);
                    WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                }
            });
        }
    }

    /**
     * 答题错误显示的dialog
     */
    private void showErrorDialog() {
        new CommonDialog.Builder(activity)
                .setTitle(R.string.dialog_hint)
                .setMessage("很遗憾，您答错了 ！")
                .setPositiveButton(R.string.next, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.next(page);
                        activity.kgtError(false);
                        activity.accomplishAnswer();
                        init(1);
                    }
                }, R.color.colorPrimaryDark)
                .setNegativeButton(R.string.dialog_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.kgtError(true);
                        activity.accomplishAnswer();
                    }
                }, R.color.colorPrimaryDark).show();
    }

    /**
     * 提交答案操作
     */
    private void submitAnswer() {
        StudyUtils.showSeqErrorCauseAndImg(this, seqModel);
    }

}
