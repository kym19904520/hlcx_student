package com.up.study.ui.my;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 学情分析-错题详情
 */
public class StudyStatusAnalysisErrorActivity extends BaseFragmentActivity {
    public final static int ERROR_SJ=0;//跳转到试卷tab
    public final static int ERROR_JTZY=1;//跳转到家庭作业tab
    private ListView lv ;
    private CommonAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private TextView tv_sj,tv_jtzy;
    @Override
    protected int getContentViewId() {
        return R.layout.act_study_status_analysis_errors;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_sj = bind(R.id.tv_sj);
        tv_jtzy = bind(R.id.tv_jtzy);
    }

    @Override
    protected void initEvent() {
        tv_sj.setOnClickListener(this);
        tv_jtzy.setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(TestAnalysisActivity.class,null);
            }
        });
    }

    @Override
    protected void initData() {
        int jumpType = getIntent().getIntExtra("key",0);
        if (jumpType==ERROR_SJ){
            initSj();
        }
        else{
            initJtzy();
        }
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        adapter = new CommonAdapter<String>(ctx, dataList, R.layout.item_xqfx_errors) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
               // TextView tv11 = vh.getView(R.id.tv_offer_item_11);
            }
        };
        lv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v==tv_sj){
            initSj();
        }
        else if(v==tv_jtzy){
            initJtzy();
        }
    }

    /**
     * 试卷tab
     */
    private void initSj(){
        tv_sj.setSelected(true);
        tv_jtzy.setSelected(false);
    }

    /**
     * 家庭作业tab
     */
    private void initJtzy(){
        tv_sj.setSelected(false);
        tv_jtzy.setSelected(true);
    }
}
