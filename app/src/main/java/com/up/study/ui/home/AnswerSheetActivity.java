package com.up.study.ui.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.up.common.utils.Logger;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.AnswerSheetModel;
import com.up.study.model.SeqModel;
import com.up.study.weight.MyGridView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 答题卡
 */
public class AnswerSheetActivity extends BaseFragmentActivity {

    private ListView lv ;
    private CommonAdapter<AnswerSheetModel> adapter;
    private List<AnswerSheetModel> dataList = new ArrayList<AnswerSheetModel>();

    private TextView tv_title,tv_submit,tv_num;

    private String[] hadDoitIdArray=null;//已经作答的题目id
    @Override
    protected int getContentViewId() {
        return R.layout.act_answer_sheet_1;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_title = bind(R.id.tv_title);
        tv_submit = bind(R.id.tv_submit);
        tv_num = bind(R.id.tv_num);
    }

    @Override
    protected void initEvent() {
        tv_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String hadDoitId = getIntent().getStringExtra("hadDoitId");
        if(hadDoitId.endsWith(";")){
            hadDoitId = hadDoitId.substring(0,hadDoitId.length()-1);
        }
        showLog("hadDoitId="+hadDoitId);
        String[] arrays = hadDoitId.split(";");
        if (arrays.length==1 && TextUtils.isEmpty(arrays[0])){
            hadDoitIdArray = new String[]{};
        }
        else {
            hadDoitIdArray = hadDoitId.split(";");
        }

        getErrorPapers(TApplication.getInstant().getRelationId()+"");
        adapter = new CommonAdapter<AnswerSheetModel>(ctx, dataList, R.layout.item_answer_card) {
            @Override
            public void convert(ViewHolder vh, final AnswerSheetModel parentItem, final int parentPosition) {
                MyGridView gv = vh.getView(R.id.gv);
                TextView tv_title = vh.getView(R.id.tv_title);
                TextView tv_score = vh.getView(R.id.tv_score);
                tv_title.setText(parentItem.getSubjectType()+"  （"+parentItem.getSubjectSum()+"）");
                tv_score.setText("共"+parentItem.getMarkSum()+"分");
                final List<SeqModel> seqList = parentItem.getSeqList();
                CommonAdapter<SeqModel> adapter = new CommonAdapter<SeqModel>(ctx, seqList, R.layout.item_gv) {
                    @Override
                    public void convert(ViewHolder vh, final SeqModel item, int position) {
                        final TextView tv = vh.getView(R.id.tv);
                        tv.setText(item.getSeq()+"");
                        boolean hasDoit = false;
                        for (int i = 0;i<hadDoitIdArray.length;i++){
                            if (item.getSubjectId()==Integer.parseInt(hadDoitIdArray[i])){
                                hasDoit = true;
                                tv.setBackgroundResource(R.mipmap.sy_31);
                                break;
                            }
                        }
                        if (!hasDoit){
                            tv.setBackgroundResource(R.mipmap.sy_32);
                        }
                        /*if(item.getStatus()==0){//正确的题
                            if (item.isSelect()){
                                tv.setBackgroundResource(R.mipmap.sy_28);
                            }
                            else{
                                tv.setBackgroundResource(R.mipmap.sy_31);
                            }

                        }
                        else{//错误的题
                            if(item.isSelect()){
                                tv.setBackgroundResource(R.mipmap.sy_29);
                            }
                            else{
                                tv.setBackgroundResource(R.mipmap.sy_30);
                            }

                        }*/
                    }
                };
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showLog("seqList.get(position).getSubjectId()="+seqList.get(position).getSubjectId());
                        gotoActivity(DoHomeworkActivity.class,seqList.get(position).getSubjectId());
                    }
                });
                gv.setAdapter(adapter);
            }
        };
        lv.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        if (v==tv_submit){
            submit();
        }
    }

    /**
     * 获取答题卡
     * relationId 试卷ID、任务ID
     */
    private void getErrorPapers(String relationId) {
        //dataList.clear();
        HttpParams params = new HttpParams();
        params.put("relationType", TApplication.getInstant().getRelationType());//	试卷类型 1:试卷录入 2:线上作业
        params.put("relationId", relationId);
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("classsId", TApplication.getInstant().getClassId());
        J.http().post(Urls.GET_ERROR_PAPERS, ctx, params, new HttpCallback<Respond<List<AnswerSheetModel>>>(ctx) {
            @Override
            public void success(Respond<List<AnswerSheetModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<AnswerSheetModel>  list = res.getData();
                    initTitle(list);
                    dataList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //后台少传两个字段，自己计算
    private void initTitle(List<AnswerSheetModel> list) {
        int completedTotal = 0;//已完成数
        int subjectSumTotal = 0;//总题数
        String title = "";//试卷标题
        for (AnswerSheetModel model:list){
            subjectSumTotal+=model.getSubjectSum();
            completedTotal+=model.getCompletedTotal();
            title = model.getTitle();
            //List<SeqModel> seqList = model.getSeqList();
        }
        tv_title.setText(title+" （"+subjectSumTotal+"题）");
        tv_num.setText(completedTotal + "/"+subjectSumTotal);
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
                if(Respond.SUC.equals(res.getCode())){
                    showToast("作业已提交");
                }
                else{
                    showToast(res.getMsg());
                }
                gotoActivity(HomeworkResultActivity.class,null);
            }
        });
    }
    /*private GridView gv_1,gv_2;
    private CommonAdapter<String> adapter,adapter2;
    private List<String> dataList = new ArrayList<>();
    private List<String> dataList2 = new ArrayList<>();
    private TextView tv_right;

    @Override
    protected int getContentViewId() {
        return R.layout.act_answer_sheet;
    }

    @Override
    protected void initView() {
        gv_1 = bind(R.id.gv_1);
        gv_2 = bind(R.id.gv_2);
        tv_right = bind(R.id.tv_right);
    }

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        dataList.add("1");
        dataList.add("2");
        dataList.add("3");
        dataList.add("4");
        dataList.add("5");
        dataList.add("6");
        dataList.add("7");
        dataList.add("8");
        dataList.add("9");
        dataList.add("10");
        dataList.add("11");
        dataList.add("12");
        dataList.add("13");
        dataList.add("14");
        adapter = new CommonAdapter<String>(ctx, dataList, R.layout.item_gv) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                vh.setText(R.id.tv,item);
            }
        };
        gv_1.setAdapter(adapter);

        dataList2.add("15");
        dataList2.add("16");
        dataList2.add("17");
        dataList2.add("18");
        dataList2.add("20");
        dataList2.add("21");
        dataList2.add("22");
        dataList2.add("23");
        dataList2.add("24");
        dataList2.add("25");
        dataList2.add("26");
        dataList2.add("27");
        adapter2 = new CommonAdapter<String>(ctx, dataList2, R.layout.item_gv) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                vh.setText(R.id.tv,item);
            }
        };
        gv_2.setAdapter(adapter2);
    }

    @Override
    public void onClick(View v) {
        if(v==tv_right){
            gotoActivity(HomeworkResultActivity.class,null);
        }
    }*/
}
