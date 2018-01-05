package com.up.study.ui.home;

import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 任务
 */
public class TaskActivity extends BaseFragmentActivity {
    private ListView lv ;
    private CommonAdapter<TaskModel> adapter;
    private List<TaskModel> dataList = new ArrayList<TaskModel>();

    private TextView tv_sj,tv_jtzy,tv_error_text;

    private boolean isFinish=false;//

    @Override
    protected int getContentViewId() {
        return R.layout.act_task;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_sj = bind(R.id.tv_sj);
        tv_jtzy = bind(R.id.tv_jtzy);
        tv_error_text = bind(R.id.tv_error_text);
    }

    @Override
    protected void initEvent() {
        tv_sj.setOnClickListener(this);
        tv_jtzy.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        adapter = new CommonAdapter<TaskModel>(ctx, dataList, R.layout.item_task) {
            @Override
            public void convert(ViewHolder vh, TaskModel item, int position) {
                ImageView iv_1 = vh.getView(R.id.iv_1);
                if ("1".equals(item.getRelationTypeId())){
                    iv_1.setImageBitmap(ImageUtil.readBitMap(TaskActivity.this,R.mipmap.sy_25));
                }
                else{
                    iv_1.setImageBitmap(ImageUtil.readBitMap(TaskActivity.this,R.mipmap.sy_24));
                }
                TextView tv_title = vh.getView(R.id.tv_title);
                tv_title.setText(item.getTitle());
                TextView tv_info = vh.getView(R.id.tv_info);
                tv_info.setText(item.getMajorName()+"/"+item.getGradeName()+"/"+item.getRelationType());
                TextView tv_type = vh.getView(R.id.tv_type);
                tv_type.setText(item.getPapersType());
                TextView tv_total_num = vh.getView(R.id.tv_total_num);
                tv_total_num.setText("共"+item.getSubjectSum()+"题");
                TextView tv_finish_status = vh.getView(R.id.tv_finish_status);
                String finish_status = "<font color='#FC4C7A'>" + item.getCompletedSum() + "</font>"
                        +"/<font color='#00aeff'>" + item.getClassSum() + "</font> 已完成";
                tv_finish_status.setText(Html.fromHtml(finish_status));
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int relationId = Integer.parseInt(dataList.get(position).getId());
                int relationType = Integer.parseInt(dataList.get(position).getRelationTypeId());
                TApplication.getInstant().setRelationId(relationId);
                TApplication.getInstant().setRelationType(relationType);

                SPUtil.putString(TaskActivity.this, Constants.SP_RELATION_ID,relationId+"");
                SPUtil.putString(TaskActivity.this, Constants.SP_RELATION_TYPE,relationType+"");

                if ("1".equals(dataList.get(position).getRelationTypeId())){//试卷录入
                    if (isFinish){
                        gotoActivity(TaskTestResultActivity.class,null);
                    }
                    else{
                        gotoActivity(TaskTestInputActivity.class,relationId);
                    }
                }
                else{//线上作业
                    if (isFinish){
                        gotoActivity(HomeworkResultActivity.class,null);
                    }
                    else {
                        gotoActivityWithBean(HomeworkActivity.class, dataList.get(position), null);
                    }
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int key = getIntent().getIntExtra(Constants.KEY,0);
        if (key==1){
            initFinish();
        }
        else {
            initNoFinish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==tv_sj){
            initNoFinish();
        }
        else if(v==tv_jtzy){
            initFinish();
        }
    }

    /**
     * 未完成tab
     */
    private void initNoFinish(){
        isFinish = false;
        tv_sj.setSelected(true);
        tv_jtzy.setSelected(false);
        getTastList("1");
    }

    /**
     * 已完成tab
     */
    private void initFinish(){
        isFinish = true;
        tv_sj.setSelected(false);
        tv_jtzy.setSelected(true);
        getTastList("2");
    }

    /**
     * 获取任务列表
     * type 类型:1未完成任务 2已完成任务
     */
    private void getTastList(String type) {
        dataList.clear();
        HttpParams params = new HttpParams();
        params.put("type", type);
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("classsId", TApplication.getInstant().getClassId());
        J.http().post(Urls.TASK_LIST, ctx, params, new HttpCallback<Respond<List<TaskModel>>>(ctx) {
            @Override
            public void success(Respond<List<TaskModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<TaskModel>  list = res.getData();
                    if (list!= null && list.size() >0){
                        dataList.addAll(list);
                        adapter.notifyDataSetChanged();
                        lv.setVisibility(View.VISIBLE);
                        bind(R.id.ll_no_error).setVisibility(View.GONE);
                        tv_error_text.setText(R.string.no_error_data);
                    }else {
                        lv.setVisibility(View.GONE);
                        bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
