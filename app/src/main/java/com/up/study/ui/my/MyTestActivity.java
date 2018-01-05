package com.up.study.ui.my;

import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.TaskModel;
import com.up.study.ui.home.HomeworkActivity;
import com.up.study.ui.home.TaskTestInputActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学情分析-我的试卷
 */
public class MyTestActivity extends BaseFragmentActivity {

    private ListView lv ;
    private CommonAdapter<TaskModel> adapter;
    private List<TaskModel> dataList = new ArrayList<TaskModel>();

//    private TextView tv_sj,tv_jtzy;

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_test;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
//        tv_sj = bind(R.id.tv_sj);
//        tv_jtzy = bind(R.id.tv_jtzy);
    }

    @Override
    protected void initEvent() {
//        tv_sj.setOnClickListener(this);
//        tv_jtzy.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        adapter = new CommonAdapter<TaskModel>(ctx, dataList, R.layout.item_xqfx_errors) {
            @Override
            public void convert(ViewHolder vh, TaskModel item, int position) {
                TextView tv_title = vh.getView(R.id.tv_title);
                tv_title.setText(item.getTitle());
                TextView tv_info = vh.getView(R.id.tv_info);
                tv_info.setText(item.getMajorName()+"/"+item.getGradeName()+"/"+item.getRelationType());
                TextView tv_type = vh.getView(R.id.tv_type);
                tv_type.setText(item.getPapersType());
                TextView tv_finish_status = vh.getView(R.id.tv_finish_status);
                String finish_status = "共<font color='#00aeff'>" + (item.getSubjectSum()==null?0:item.getSubjectSum()) + "</font>题";
                tv_finish_status.setText(Html.fromHtml(finish_status));
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(MyTestDetailActivity.class,Integer.parseInt(dataList.get(position).getId()));

            }
        });
        initNoFinish();
    }

    @Override
    public void onClick(View v) {
        /*if(v==tv_sj){
            initNoFinish();
        }
        else if(v==tv_jtzy){
            initFinish();
        }*/
    }

    /**
     * tab1
     */
    private void initNoFinish(){
//        tv_sj.setSelected(true);
//        tv_jtzy.setSelected(false);
        getTastList("0");
    }

    /**
     * tab2
     */
    private void initFinish(){
//        tv_sj.setSelected(false);
//        tv_jtzy.setSelected(true);
        getTastList("1");
    }

    /**
     * 获取任务列表
     * type 类型:1未完成任务 2已完成任务
     */
    private void getTastList(String type) {
        dataList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("type", type);
        J.http().post(Urls.MY_TEST, ctx, params, new HttpCallback<Respond<List<TaskModel>>>(ctx) {
            @Override
            public void success(Respond<List<TaskModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<TaskModel>  list = res.getData();
                    if (list!=null&&list.size()>0){
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();

                        lv.setVisibility(View.VISIBLE);
                        bind(R.id.ll_no_error).setVisibility(View.GONE);
                    }
                    else {
                        lv.setVisibility(View.GONE);
                        bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                        TextView tv = bind(R.id.tv_error_text);
                        tv.setText("还没有试卷奥~");
                    }
                }
            }
        });
    }

    /*public final static int ERROR_SJ=0;//跳转到真实试卷tab
    public final static int ERROR_JTZY=1;//跳转到练习卷tab
    private ListView lv ;
    private CommonAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private TextView tv_sj,tv_jtzy;
    @Override
    protected int getContentViewId() {
        return R.layout.act_my_test;
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(MyTestDetailActivity.class,null);
            }
        });
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

    *//**
     * 试卷tab
     *//*
    private void initSj(){
        tv_sj.setSelected(true);
        tv_jtzy.setSelected(false);
    }

    *//**
     * 家庭作业tab
     *//*
    private void initJtzy(){
        tv_sj.setSelected(false);
        tv_jtzy.setSelected(true);
    }*/
}
