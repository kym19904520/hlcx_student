package com.up.study.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.up.common.conf.Constants;
import com.up.study.R;
import com.up.study.adapter.HomePageAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.ui.errors.SolutionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务-试卷解析
 */
public class TaskTestAnalysisActivity extends BaseFragmentActivity {

    private ViewPager viewPager;
    private HomePageAdapter homePageAdapter;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    private int subjectId;

    @Override
    protected int getContentViewId() {
        return R.layout.act_task_test_analysis;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
    }

    @Override
    protected void initData() {
        subjectId = getIntent().getIntExtra(Constants.KEY,-1);
        initViewPager();
    }

    private void initViewPager() {
        SolutionFragment newFragment = new SolutionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pageNo", 0);
        bundle.putInt("subjectId", subjectId);
        bundle.putInt("seqType", SolutionFragment.SEQ_TYPE_STJX);
        newFragment.setArguments(bundle);
        fragmentList.add(newFragment);
        homePageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(homePageAdapter);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    public void onClick(View v) {

    }


    /*private MyListView lv;
    private List<Options> optionsList = new ArrayList<Options>();
    private CommonAdapter<Options> adapter;

    RecyclerView mRecyclerView;
    RecyclerViewAdapter myAdapter;
    List<String> knowNameList = new ArrayList<String>();

    TextView tv_answer,tv_analysis,tv_subject_type,tv_subject_title;
    private int subjectId;
    @Override
    protected int getContentViewId() {
        return R.layout.act_task_test_analysis;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        mRecyclerView= bind(R.id.recylist);
        tv_answer = bind(R.id.tv_answer);
        tv_analysis = bind(R.id.tv_analysis);
        tv_subject_type = bind(R.id.tv_subject_type);
        tv_subject_title = bind(R.id.tv_subject_title);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        subjectId = getIntent().getIntExtra(Constants.KEY,-1);

        //设置线性管理器
        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setLayoutManager(ms);
        myAdapter = new RecyclerViewAdapter(knowNameList);
        mRecyclerView.setAdapter(myAdapter);
        adapter = new CommonAdapter<Options>(ctx, optionsList, R.layout.item_topic_kgt) {
            @Override
            public void convert(ViewHolder vh, Options item, int position) {
                TextView tv_answer = vh.getView(R.id.tv_answer);
                TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                tv_answer.setText(item.getOpt());
                tv_answer_text.setText(item.getText());
                if(item.getStatus()==1){
                    tv_answer.setBackgroundResource(R.drawable.round_blue_circle_background);
                }
                else{
                    tv_answer.setBackgroundResource(R.drawable.round_gray_circle_background);
                }
            }
        };
        lv.setAdapter(adapter);

        getAnalysis();
    }

    @Override
    public void onClick(View v) {
    }

    *//**
     * 获取试题解析
     *//*
    private void getAnalysis() {
        optionsList.clear();
        knowNameList.clear();
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", subjectId);
        J.http().post(Urls.SAVE_SUBJECT_ANALYSIS, ctx, params, new HttpCallback<Respond<SeqModel>>(ctx) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    SeqModel mode = res.getData();
                    if (mode==null){
                        showToast("查询不到");
                        TaskTestAnalysisActivity.this.finish();
                        return;
                    }
                    tv_answer.setText(mode.getAnswer());
                    tv_analysis.setText(mode.getAnalysis());
                    tv_subject_type.setText(mode.getSubjectType());
                    tv_subject_title.setText(mode.getTitle());

                    String options = mode.getOptions();
                    Type type=new TypeToken<List<Options>>(){}.getType();
                    List<Options> list = new Gson().fromJson(options,type);
                    if (list!=null) {
                        optionsList.addAll(list);
                    }
                    if ("单选题".equals(mode.getSubjectType())){
                        for (int i = 0 ;i<optionsList.size();i++){
                            if (mode.getAnswer().equals(optionsList.get(i).getOpt())){
                                optionsList.get(i).setStatus(1);
                                break;
                            }
                        }
                    }
                    else if("多选题".equals(mode.getSubjectType())){
                        List<String> answers = Arrays.asList(mode.getAnswer().split(","));
                        for (int j = 0;j<answers.size();j++){
                            for (int i = 0 ;i<optionsList.size();i++){
                                if (answers.get(j).equals(optionsList.get(i).getOpt())){
                                    optionsList.get(i).setStatus(1);
                                    break;
                                }
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    String knowName =mode.getKnowName();
                    Type type1=new TypeToken<List<String>>(){}.getType();
                    List<String> list1= new Gson().fromJson(knowName,type1);
                    if (list1!=null) {
                        knowNameList.addAll(list1);
                        myAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }*/
}
