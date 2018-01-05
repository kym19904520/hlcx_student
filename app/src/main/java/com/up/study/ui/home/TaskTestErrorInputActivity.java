package com.up.study.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.up.common.utils.FormatUtils;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.Logger;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.AnswerSheetModel;
import com.up.study.model.ImgUrl;
import com.up.study.model.SeqModel;
import com.up.study.model.TestModel;
import com.up.study.weight.MyGridView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 试卷错题录入（答题卡）
 */
public class TaskTestErrorInputActivity extends BaseFragmentActivity {

    private ListView lv ;
    private CommonAdapter<AnswerSheetModel> answerSheetModelCommonAdapter;
    private List<AnswerSheetModel> dataList = new ArrayList<AnswerSheetModel>();

    private TextView tv_error,tv_right,tv_title,tv_submit;
    private AnswerSheetModel selectedSubject;//被选中的题型
    private SeqModel selectedSeq;//被选中的题目

    private int parentId=-1;//已选中的AnswerSheetModel的uid
    private int chaldId=-1;//已选中的SeqModel的id

    private boolean isLoad = false;//正在加载刷新Gridview，不能点击
    @Override
    protected int getContentViewId() {
        return R.layout.act_test_error_input;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_error = bind(R.id.tv_error);
        tv_right = bind(R.id.tv_right);
        tv_title = bind(R.id.tv_title);
        tv_submit = bind(R.id.tv_submit);
    }

    @Override
    protected void initEvent() {
        tv_error.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        answerSheetModelCommonAdapter = new CommonAdapter<AnswerSheetModel>(ctx, dataList, R.layout.item_answer_card) {
            @Override
            public void convert(ViewHolder vh, final AnswerSheetModel parentItem, final int parentPosition) {
                MyGridView gv = vh.getView(R.id.gv);
                TextView tv_title = vh.getView(R.id.tv_title);
                TextView tv_score = vh.getView(R.id.tv_score);
                tv_title.setText(parentItem.getSubjectType()+"  （"+parentItem.getSubjectSum()+"）");
                tv_score.setText("共"+ FormatUtils.double1(parentItem.getMarkSum())+"分");
                List<SeqModel> seqList = parentItem.getSeqList();
                /*List<String> dataList1 = new ArrayList<>();
                dataList1.add("1");
                dataList1.add("2");
                */
                CommonAdapter<SeqModel> adapter = new CommonAdapter<SeqModel>(ctx, seqList, R.layout.item_gv) {
                    @Override
                    public void convert(ViewHolder vh, final SeqModel item, int position) {
                        final TextView tv = vh.getView(R.id.tv);
                        tv.setText(item.getSeq()+"");
                        if(item.getStatus()==1){//错误的题
                            //                            ImageUtil.readBitMap(TaskTestErrorInputActivity.this, R.mipmap.sy_30);
                            if(item.isSelect()){
                                tv.setBackgroundResource(R.mipmap.sy_29);
                            }
                            else{
                                tv.setBackgroundResource(R.mipmap.sy_30);
                            }

                        }
                        else{//正确的题
                            //ImageUtil.readBitMap(TaskTestErrorInputActivity.this, R.mipmap.sy_31);
                            if (item.isSelect()){
                                tv.setBackgroundResource(R.mipmap.sy_28);
                            }
                            else{
                                tv.setBackgroundResource(R.mipmap.sy_31);
                            }

                        }
                       /* tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //少了判断是否为选中状态
                                selectedSeq = item;
                                selectedSubject = parentItem;
                                if (item.getStatus()==1){
//                                    ImageUtil.readBitMap(TaskTestErrorInputActivity.this, R.mipmap.sy_28);
                                    tv.setBackgroundResource(R.mipmap.sy_28);
                                }
                                else{
//                                    ImageUtil.readBitMap(TaskTestErrorInputActivity.this, R.mipmap.sy_29);
                                    tv.setBackgroundResource(R.mipmap.sy_29);
                                }
                            }
                        });*/
                    }
                };
                gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(isLoad){//正在刷新控件不让点击
                            return;
                        }
                       // TextView tv = (TextView)view.findViewById(R.id.tv);
                        selectedSeq = (SeqModel)parent.getAdapter().getItem(position);
                        selectedSubject = parentItem;
                        /*if (seqModel.getStatus()==1){
                            tv.setBackgroundResource(R.mipmap.sy_28);
                        }
                        else{
                            tv.setBackgroundResource(R.mipmap.sy_29);
                        }*/
                        Logger.i(Logger.TAG,"parentPosition="+parentPosition+",position="+position);
                        if(parentId==parentPosition&&position==chaldId){//点了同一个位置
                            boolean select = dataList.get(parentId).getSeqList().get(chaldId).isSelect();
                            dataList.get(parentId).getSeqList().get(chaldId).setSelect(!select);
                        }
                        else{
                            dataList.get(parentPosition).getSeqList().get(position).setSelect(true);
                            if (parentId!=-1||chaldId!=-1){
                                dataList.get(parentId).getSeqList().get(chaldId).setSelect(false);
                            }
                        }

                        answerSheetModelCommonAdapter.notifyDataSetChanged();
                        //((CommonAdapter<SeqModel>)parent.getAdapter()).notifyDataSetChanged();
                        parentId = parentPosition;
                        chaldId = position;
                    }
                });
                gv.setAdapter(adapter);
            }
        };
        lv.setAdapter(answerSheetModelCommonAdapter);
        getErrorPapers(TApplication.getInstant().getRelationId()+"");
    }

    @Override
    public void onClick(View v) {
        if(v==tv_error){
            if(selectedSeq!=null){
                Intent intent = new Intent();
                intent.setClass(this, TaskErrorsInputActivity.class);
                intent.putExtra("bean1", selectedSeq);
                intent.putExtra("bean2", selectedSubject);
                startActivityForResult(intent,0);
            }
            else{
                showToast("请先选择您要操作的题目");
            }
        }
        else if(v==tv_right){
            if(selectedSeq!=null){
                setRightSeq();
            }
            else{
                showToast("请先选择您要操作的题目");
            }
        }
        else if (v==tv_submit){
            showDialog();
        }
    }

    /**
     * 将题目设为正确
     */
    private void setRightSeq() {
        HttpParams params = new HttpParams();
        params.put("cuid", selectedSubject.getCuid());//	创建者或老师ID
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", selectedSeq.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("mark", selectedSeq.getMark());
        J.http().post(Urls.SAVE_CORRECT_PAPERS, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("已设置答案为正确");
                    getErrorPapers(TApplication.getInstant().getRelationId()+"");
                }
            }
        });
    }

    /**
     * 获取试卷错题录入列表/答题卡
     * relationId 试卷ID、任务ID
     */
    private void getErrorPapers(String relationId) {
        isLoad = true;
        dataList.clear();
        HttpParams params = new HttpParams();
        params.put("relationType", TApplication.getInstant().getRelationType());//	试卷类型 1:试卷录入 2:线上作业
        params.put("relationId", relationId);
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("classsId", TApplication.getInstant().getClassId());
        J.http().post(Urls.GET_ERROR_PAPERS, ctx, params, new HttpCallback<Respond<List<AnswerSheetModel>>>(null) {
            @Override
            public void success(Respond<List<AnswerSheetModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<AnswerSheetModel>  list = res.getData();
                    //添加两个假数据
                   /* SeqModel seq = new SeqModel();
                    seq.setMark(4);
                    seq.setSeq(2+"");
                    seq.setStatus(1);
                    seq.setSubjectId(1+"");
                    list.get(0).getSeqList().add(seq);
                    SeqModel seq1 = new SeqModel();
                    seq1.setMark(3);
                    seq1.setSeq(3+"");
                    seq1.setStatus(0);
                    seq1.setSubjectId(4+"");
                    list.get(0).getSeqList().add(seq1);*/


                    initTitle(list);
                    dataList.addAll(list);
                    answerSheetModelCommonAdapter.notifyDataSetChanged();

                    isLoad = false;//加载完成
                }
            }
        });
    }

    //后台少传两个字段，自己计算
    private void initTitle(List<AnswerSheetModel> list) {
        int subjectSumTotal = 0;//总题数
        String title = "";//试卷标题
        for (AnswerSheetModel model:list){
            subjectSumTotal+=model.getSubjectSum();
            title = model.getTitle();
            //List<SeqModel> seqList = model.getSeqList();
        }
        tv_title.setText(title+" ("+subjectSumTotal+"题)");

    }

    /**
     * 提交试卷
     */
    private void submit(double point) {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("point", point);//试卷所得总分数 =试卷总分- 错题所扣分数
        J.http().post(Urls.SAVE_PAPERS, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
//                    showToast("试卷已提交");
                    showLog("-------试卷提交成功，才开始上传学生试卷----------------");
                    Map<String, Object> map = getMap();
                    if(map!=null){
                        ArrayList<String> imageList = (ArrayList<String>)map.get("imageList");
                        if(imageList==null||imageList.size()==0){
                            showLoge("--------学生试卷图片数量为空-----1-----");
                        }
                        else {
                            submitStuTest(imageList);
                        }
                    }
                    else{
                        showLoge("--------学生试卷图片数量为空------2----");
                        showToast("试卷提交成功");
                    }
                }
                else{
                    showToast(res.getMsg());
                }
            }
        });
    }

    private void submitStuTest(ArrayList<String> imageList){
        showLog("-------开始提交学生试卷----------------");
        //showLog("学生试卷："+imageList.size()+","+imageList.toString());
        List<String> uploadImgs = StudyUtils.getUploadImgs(imageList);
        //提交图片到阿里云后再提交错题
        showLoading("加载中...",false);
        StudyUtils.uploadImgUrl(uploadImgs,this, new CallBack<List<ImgUrl>>() {
            @Override
            public void suc(List<ImgUrl> obj) {
                TaskTestErrorInputActivity.this.hideLoading();
                testUpload(obj);
            }
        });
    }

    /**
     * 试卷上传
     * relationId 试卷ID、任务ID
     */
    private void testUpload(List<ImgUrl> obj) {
        HttpParams params = new HttpParams();
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("attached", new Gson().toJson(obj));
        J.http().post(Urls.UPLOAD_PAPERS, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showLog("------提交学生试卷完成----------------");
                    showToast("试卷提交成功");
                    gotoActivity(TaskTestResultActivity.class,null);
                }
            }
        });
    }


    AlertDialog dialog ;

    private void showDialog(){
        dialog = new AlertDialog.Builder(ctx).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_submit);
        //初始化数据
        final ArrayList<String> lineList1 = new ArrayList<>();
        lineList1 .add("1");
        lineList1 .add("0");

        final ArrayList<String> lineList = new ArrayList<>();
        lineList .add("0");
        lineList .add("1");
        lineList .add("2");
        lineList .add("3");
        lineList .add("4");
        lineList .add("5");
        lineList .add("6");
        lineList .add("7");
        lineList .add("8");
        lineList .add("9");
        TextView tv_right = (TextView) window.findViewById(R.id.tv_right);

        //设置数据
        final WheelView wheelView_1 =(WheelView) window.findViewById(R.id.wheel_1);
        final WheelView wheelView_line =(WheelView) window.findViewById(R.id.wheel_line);
        final WheelView wheelView_section =(WheelView) window.findViewById(R.id.wheel_section);
        final WheelView wheelView_2 =(WheelView) window.findViewById(R.id.wheel_2);
        WheelView.LineConfig config = new WheelView.LineConfig();
        config.setColor(getResources().getColor(R.color.text_white));//设置分割线颜色

        wheelView_1.setLineConfig(config);
        wheelView_1.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_1.setTextSize(24);

        wheelView_line.setLineConfig(config);
        wheelView_line.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));//设置选中字体颜色
        wheelView_line.setTextSize(24);
        wheelView_section.setLineConfig(config);
        wheelView_section.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));
        wheelView_section.setTextSize(24);

        wheelView_2.setLineConfig(config);
        wheelView_2.setTextColor(Color.GRAY,getResources().getColor(R.color.colorPrimary));
        wheelView_2.setTextSize(24);

        wheelView_1.setItems(lineList1,0);//设置第一个WheelView
        wheelView_line.setItems(lineList,0);//设置第一个WheelView
        wheelView_section.setItems(lineList,0);
        wheelView_2.setItems(lineList,0);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lineValue1 = lineList1.get(wheelView_1.getSelectedIndex());
                String lineValue = lineList.get(wheelView_line.getSelectedIndex());
                String sectionValue = lineList.get(wheelView_section.getSelectedIndex());
                String lineValue2 = lineList.get(wheelView_2.getSelectedIndex());
                double num = Double.parseDouble(lineValue1+lineValue+sectionValue+"."+lineValue2);
                showLog("num="+num);
                if(num<0||num>100){
                    showToast("分数只能在0-100之间");
                    return;
                }
                submit(num);
                dialog.cancel();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        showToast("onActivityResult");
        getErrorPapers(TApplication.getInstant().getRelationId()+"");
    }
}
