package com.up.study.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
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
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.StringUtils;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.callback.ImgCallBack;
import com.up.study.model.ImgUrl;
import com.up.study.model.Options;
import com.up.study.model.SeqModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 开始做题（家庭作业）
 */
public class DoHomeworkActivity extends BaseFragmentActivity {
    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();
    private TopicPagerAdapter adapter;
    private List<SeqModel> mSeqList = new ArrayList<SeqModel>();

    private TextView tv_num, tv_right;

    private List<ImgUrl> localSelectImgUrls = new ArrayList<ImgUrl>();//本地已选的图片的地址

    private int hasDoItNum = 0;//已经做的题目数
    private int mSatus;//viewpager滑动状态
    private boolean gointo = true;//是否可进入

    private String hadDoitId = "";//已经作答的题目id
    private ImageView back;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int subjectId = intent.getIntExtra(Constants.KEY, 0);
        for (int i = 0; i < mSeqList.size(); i++) {
            if (mSeqList.get(i).getSubjectId() == subjectId) {
                viewPager.setCurrentItem(i, false);
                break;
            }
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_do_homework;
    }

    @Override
    protected void initView() {
        back = bind(R.id.back);
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);
        tv_right = bind(R.id.tv_right);

    }

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getHomeWorkList();
    }

    @Override
    public void onClick(View v) {
        if (v == tv_right) {
            gotoActivity(AnswerSheetActivity.class, "hadDoitId", hadDoitId);
        } else
        if (v.getId() == R.id.back) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_hint)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.dialog_off, null)
                    .show();
        }
    }

    /**
     * 获取家庭作业列表
     */
    private void getHomeWorkList() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        J.http().post(Urls.SUBJECT_DETAILS, ctx, params, new HttpCallback<Respond<List<SeqModel>>>(ctx) {
            @Override
            public void success(Respond<List<SeqModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    mSeqList = res.getData();
                    initSeqCardData();
                    /*String options = mSeqList.get(0).getOptions();
                    Type type=new TypeToken<List<Options>>(){}.getType();
                    List<Options> list1 = new Gson().fromJson(options,type);

                    String knowName = mSeqList.get(0).getKnowName();
                    Type type1=new TypeToken<List<String>>(){}.getType();
                    List<String> list2 = new Gson().fromJson(knowName,type1);

                    showToast(list1.get(0).getText()+","+list2.get(0));*/
                }
            }
        });
    }

    private void initSeqCardData() {
        mInflater = getLayoutInflater();
        for (int i = 0; i < mSeqList.size(); i++) {
            View view = mInflater.inflate(R.layout.view_homework_seq, null);
            initInflaterView(view, mSeqList.get(i));
            mViews.add(view);
        }
        tv_num.setText(1 + "/" + mSeqList.size());

        adapter = new TopicPagerAdapter(mViews);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //showLog("1111111111111--onPageScrolled  position="+position+",positionOffset="+positionOffset+",positionOffsetPixels="+positionOffsetPixels);
                if (gointo) {
                    if (position + 1 == mSeqList.size() && mSatus == 1 && positionOffset == 0 && positionOffsetPixels == 0) {
                        gointo = false;
                        showLog("已经是最后一题");
                        showDialog();
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/" + mSeqList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //showLog("22222222222222--onPageScrollStateChanged state="+state);
                mSatus = state;
                if (state == 0) {
                    gointo = true;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    AlertDialog.Builder dialog;

    private void showDialog() {
        int noDoNum = mSeqList.size() - hasDoItNum;
        String title = "您已经全部作答完毕，是否提交？";
        if (noDoNum > 0) {
            title = "您还有" + noDoNum + "题未作答，是否提交？";
        }
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this);
        }
        dialog.setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submit();
                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }

    /**
     * 提交作业
     */
    private void submit() {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.FINISH_ONLINE, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    showToast("作业已提交");
                } else {
                    showToast(res.getMsg());
                }
                gotoActivity(HomeworkResultActivity.class, null);
            }
        });
    }

    private void initInflaterView(final View view, final SeqModel seqModel) {
        TextView tv_subject_type = (TextView) view.findViewById(R.id.tv_subject_type);
        tv_subject_type.setText(seqModel.getSubjectType());
        //TextView tv_subject_title = (TextView)view.findViewById(R.id.tv_subject_title);
        final TextView tv_submit = (TextView) view.findViewById(R.id.tv_submit);//提交答案
        //需要提交按钮吗
        if (StudyUtils.getNeedSumitBtn(seqModel.getSubjectType())) {
            tv_submit.setVisibility(View.VISIBLE);
        }
        //tv_submit.bringToFront();
        String knowName = seqModel.getKnowName();
        if (knowName != null) {
            //List<String> list2 = Arrays.asList(knowName.split(","));
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> knowNameList = new Gson().fromJson(knowName, type1);
            //找到这个Listview
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recylist);
            //设置线性管理器
            LinearLayoutManager ms = new LinearLayoutManager(ctx);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            mRecyclerView.setLayoutManager(ms);
            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(knowNameList);
            mRecyclerView.setAdapter(myAdapter);
        }

        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {
            //tv_subject_title.setText(seqModel.getTitle());
            WebView wv_title = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());

            /*if(seqModel.getSubjectType().contains("判断")){
                seqModel.setOptions("[{\"text\":\"正确\",\"opt\":\"A\"},{\"text\":\"错误\",\"opt\":\"B\"}]");
            }*/

            final String options = seqModel.getOptions();

            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
            final MyListView lv = (MyListView) view.findViewById(R.id.lv);
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
                            hasDoItNum++;
                            submitAnswer(seqModel);
                            lv.setTag(0);
                            //submitKgt(view,seqModel,position,optionsList,adapter);
                            LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                            //TextView tv_answer = (TextView) view.findViewById(R.id.tv_right_answer);


                            WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());

                            ImageView iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
                            ImageView iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);
                            ll_anwser.setVisibility(View.VISIBLE);
                            //tv_analysis.setText(seqModel.getAnalysis());
                            String answer = "";
                            try {
                                /*if (seqModel.getSubjectType().contains("判断")){
                                    String as = seqModel.getAnswer();
                                    as = as.replace("0","A");
                                    as = as.replace("1","B");
                                    seqModel.setAnswer(as);
                                }*/
                                String answerjson = seqModel.getAnswer();
                                Type type1 = new TypeToken<List<String>>() {
                                }.getType();
                                List<String> answerList = new Gson().fromJson(answerjson, type1);
                                if (answerList != null && answerList.size() > 0) {
                                    answer = answerList.get(0);
                                }

                                //tv_answer.setText(answer);
                                WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                                WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
                            } catch (Exception e) {
                                showLog("答案格式异常！");
                            }


                            if (answer.equals(optionsList.get(position).getOpt())) {//回答正确
                                optionsList.get(position).setStatus(1);
                                adapter.notifyDataSetChanged();
                                iv_answer_right.setVisibility(View.VISIBLE);
                                submitKgt(view, seqModel, -1, optionsList, adapter, "0");
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
                                submitKgt(view, seqModel, -1, optionsList, adapter, "1");
                            }

                        }
                    }

                }
            });

            //提交答案
            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (seqModel.getSubjectType().contains("多选")) {//多选题提交答案
                        hasDoItNum++;
                        submitAnswer(seqModel);
                        tv_submit.setVisibility(View.GONE);
                        LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                        //TextView tv_answer = (TextView) view.findViewById(R.id.tv_right_answer);


                        WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                        WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());

                        ImageView iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
                        ImageView iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);

                        //tv_analysis.setText(seqModel.getAnalysis());

                        String answerjson = seqModel.getAnswer();
                        Type type1 = new TypeToken<List<String>>() {
                        }.getType();
                        List<String> answerList = new Gson().fromJson(answerjson, type1);
                        if (answerList == null) {
                            answerList = new ArrayList<String>();
                        }
                        ll_anwser.setVisibility(View.VISIBLE);
                        //tv_answer.setText(FormatUtils.List2String(answerList));
                        WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                        WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());
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
                            submitKgt(view, seqModel, -1, optionsList, adapter, "0");
                        } else {//回答错误
                            adapter.notifyDataSetChanged();
                            iv_answer_error.setVisibility(View.VISIBLE);
                            submitKgt(view, seqModel, -1, optionsList, adapter, "1");
                        }
                    }

                }
            });

        }
        //主观题初始化内容
        else {
            final String[] localImgList = new String[3];//本地已选图片,最多三张
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);

            LinearLayout ll_camera = (LinearLayout) view.findViewById(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);

            StudyUtils.initImgView(view, localImgList, this,
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
                            localImgList[1] = imgPath;
                            StringUtils.Log(localImgList);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                            localImgList[2] = imgPath;
                            StringUtils.Log(localImgList);
                        }
                    }
            );

            tv_submit.setOnClickListener(new View.OnClickListener() {//都要传图片的，填空，计算，应用，解答
                @Override
                public void onClick(View v) {
                    if (StringUtils.isArrayEmpty(localImgList)) {
                        showToast("请选择答题图片");
                        return;
                    }
                    hasDoItNum++;
                    submitAnswer(seqModel);
                    StudyUtils.uploadImgUrl(localImgList, DoHomeworkActivity.this, new CallBack<List<ImgUrl>>() {
                        @Override
                        public void suc(List<ImgUrl> obj) {
                            submitZgt(view, seqModel, obj);
                        }
                    });
                    tv_submit.setVisibility(View.GONE);

                    LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                    //TextView tv_answer = (TextView) view.findViewById(R.id.tv_right_answer);

                    WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                    WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());
                    ll_anwser.setVisibility(View.VISIBLE);
                    //tv_answer.setText(FormatUtils.List2String(GsonUtils.toList(seqModel.getAnswer())));

                    WebView wv_answer = (WebView) view.findViewById(R.id.wv_answer);
                    WidgetUtils.initAnswerWebView(wv_answer, seqModel.getAnswer());

                }
            });
        }
    }

    private void submitAnswer(SeqModel seqModel) {
        hadDoitId = hadDoitId + seqModel.getSubjectId() + ";";
        int noDoNum = mSeqList.size() - hasDoItNum;
        if (noDoNum == 0) {
            showDialog();
        }
    }

    /**
     * 客观题提交答案
     */
    private void submitKgt(final View view, SeqModel seqModel, final int position, final List<Options> optionsList, final CommonAdapter<Options> adapter, String status) {
        List<String> answerList = new ArrayList<>();
        String content = "";//客观题答案
        if (position == -1) {//多选题
            for (int i = 0; i < optionsList.size(); i++) {
                if (optionsList.get(i).getStatus() == 3) {
                    /*content+=optionsList.get(i).getOpt()+",";
                    showLog("content:"+content);*/
                    answerList.add(optionsList.get(i).getOpt());
                }
            }
        } else {
            // content =  optionsList.get(position).getOpt();
            answerList.add(optionsList.get(position).getOpt2(seqModel));
        }
        content = new Gson().toJson(answerList);
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("content", content);
        params.put("status", status);
        // params.put("attached", "");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(null) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    //答案早已给
                    /*SeqModel model = res.getData();

                    LinearLayout ll_anwser = (LinearLayout)view.findViewById(R.id.ll_anwser);
                    TextView tv_answer = (TextView)view.findViewById(R.id.tv_answer);
                    TextView tv_analysis = (TextView)view.findViewById(R.id.tv_analysis);
                    ImageView iv_answer_error = (ImageView)view.findViewById(R.id.iv_answer_error);
                    ImageView iv_answer_right = (ImageView)view.findViewById(R.id.iv_answer_right);
                    ll_anwser.setVisibility(View.VISIBLE);
                    tv_answer.setText(model.getAnswer());
                    tv_analysis.setText(model.getAnalysis());

                    if (model.getStatus()==1){//回答正确
                        optionsList.get(position).setStatus(1);
                        adapter.notifyDataSetChanged();
                        iv_answer_right.setVisibility(View.VISIBLE);
                    }
                    else {//回答错误
                        optionsList.get(position).setStatus(2);
                        for (int i = 0 ;i<optionsList.size();i++){
                            if (model.getAnswer().equals(optionsList.get(i).getOpt())){
                                optionsList.get(i).setStatus(1);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        iv_answer_error.setVisibility(View.VISIBLE);
                    }*/

                }
            }
        });
    }

    /**
     * 主观题提交答案
     */
    private void submitZgt(final View view, SeqModel seqModel, List<ImgUrl> imgList) {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("attached", new Gson().toJson(imgList));
        params.put("status", "0");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(null) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if (Respond.SUC.equals(res.getCode())) {
                    //答案早已给
                    /*SeqModel model = res.getData();

                    LinearLayout ll_anwser = (LinearLayout)view.findViewById(R.id.ll_anwser);
                    TextView tv_answer = (TextView)view.findViewById(R.id.tv_answer);
                    TextView tv_analysis = (TextView)view.findViewById(R.id.tv_analysis);
                    ll_anwser.setVisibility(View.VISIBLE);
                    tv_answer.setText(model.getAnswer());
                    tv_analysis.setText(model.getAnalysis());*/

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_hint)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.dialog_off, null)
                    .show();
        return false;
    }
}
