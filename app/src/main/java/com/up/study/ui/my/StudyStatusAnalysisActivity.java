package com.up.study.ui.my;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.model.BaseBean;
import com.up.common.utils.SPUtil;
import com.up.common.widget.MyListView;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.ErrorNumRecyclerViewAdapter;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.DifficultyModel;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.Know;
import com.up.study.model.LearningAnalysisModel;
import com.up.study.model.MajorModel;
import com.up.study.model.MaterialModel;
import com.up.study.model.WrongMsgModel;
import com.up.study.model.WrongMsgSubModel;
import com.up.study.model.XqfxJcModel;
import com.up.study.model.XqfxZjModel;
import com.up.study.model.XqfxZsdModel;
import com.up.study.ui.home.TaskActivity;
import com.up.study.ui.login.LoginActivity;
import com.up.study.weight.treeee.MyNodeBean;
import com.up.study.weight.treeee.MyTreeListViewAdapter;
import com.up.study.weight.treeee.Node;
import com.up.study.weight.treeee.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学情分析
 */
public class StudyStatusAnalysisActivity extends BaseFragmentActivity {
    private MyListView treeLv;
    private MyTreeListViewAdapter<MyNodeBean> treeAdapter;
    List<MyNodeBean> treeDatas = new ArrayList<MyNodeBean>();

    private TextView tv_my_right,tv_class_right,tv_grade_right,tv_test_num;

    private Spinner spinner_title;
    private ArrayAdapter<String> adapter_title;
    private List<String> list_qd =  new ArrayList<String>();

    private String curMajorId;
    @Override
    protected int getContentViewId() {
        return R.layout.act_study_status_analysis;
    }

    @Override
    protected void initView() {
        treeLv = bind(R.id.tree_lv);

        tv_my_right = bind(R.id.tv_my_right);
        tv_class_right = bind(R.id.tv_class_right);
        tv_grade_right = bind(R.id.tv_grade_right);
        tv_test_num = bind(R.id.tv_test_num);

        spinner_title = bind(R.id.spinner_title);

    }

    @Override
    protected void initEvent() {
        tv_test_num.setOnClickListener(this);
    }

    @Override
    protected void initData() {

        //spinner标题初始化
        adapter_title = new ArrayAdapter<String>( this ,android.R.layout.simple_spinner_item, list_qd);
        adapter_title.setDropDownViewResource(R.layout.item_spinner);
        spinner_title.setAdapter(adapter_title);
        spinner_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                TextView tv = (TextView)view;
                tv.setTextSize(18f);    //设置大小
                tv.setGravity(Gravity.CENTER_HORIZONTAL);   //设置居中
                curMajorId = majorModelList.get(position).getCode();
                getNalysisData(majorModelList.get(position).getCode());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        //知识点分析树形列表
        /*List<MyNodeBean> mDatas = new ArrayList<MyNodeBean>();
        mDatas.add(new MyNodeBean(1, 0, "人教版语文二年级下册（50）"));
        mDatas.add(new MyNodeBean(2, 1, "第一单元（10）"));
        mDatas.add(new MyNodeBean(3, 1, "第二单元（10）"));
        mDatas.add(new MyNodeBean(4, 3, "第一课 诗两首（10）"));
        mDatas.add(new MyNodeBean(5, 4, "拼音拼写（5）"));
        mDatas.add(new MyNodeBean(6, 4, "拼音音调（6）"));

        mDatas.add(new MyNodeBean(7, 0, "人教版语文二年级下册（50）"));
        mDatas.add(new MyNodeBean(8, 7, "第一单元（10）"));
        mDatas.add(new MyNodeBean(9, 8, "第一课 诗两首（10）"));*/

        try {
            treeAdapter = new MyTreeListViewAdapter<MyNodeBean>(treeLv, this,
                    treeDatas, 0, true);

            treeAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                    }
                }

                @Override
                public void onCheckChange(Node node, int position,
                                          List<Node> checkedNodes) {
                }

            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        treeLv.setAdapter(treeAdapter);

        getMaJorList();
    }

    @Override
    public void onClick(View v) {
        if(v==tv_test_num){
            gotoActivity(TaskActivity.class,1);
        }
    }

    /**
     * 获取学情分析---教材数据
     * @param majorId
     */
    private void getNalysisData(String majorId) {
        treeDatas.clear();
        HttpParams params = new HttpParams();
        params.put("majorId", majorId);
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.LEARINGIN_ANALYSIS, ctx, params, new HttpCallback< Respond<LearningAnalysisModel>>(ctx) {
            @Override
            public void success(Respond<LearningAnalysisModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    LearningAnalysisModel model = res.getData();
                    //试卷总数
                    tv_test_num.setText(model.getTotalNum()+"份");
                    tv_my_right.setText(model.getMyCorrectRate()+"");
                    tv_class_right.setText(model.getClassCorrectRate()+"");
                    tv_grade_right.setText(model.getGradeCorrectRate()+"");

                    //知识点分析
                    List<XqfxJcModel> materialModelList = model.getMaterialList();
                    if (materialModelList!=null&&materialModelList.size()>0){
                        treeLv.setVisibility(View.VISIBLE);
                        bind(R.id.ll_no_error).setVisibility(View.GONE);
                        for (int i = 0 ;i<materialModelList.size();i++){
                            XqfxJcModel jcModel = materialModelList.get(i);
                            treeDatas.add(new MyNodeBean(jcModel.getMaterialId(), 0, jcModel.getMaterialName()+"("+jcModel.getKnowledgePoint()+")",0));
                            for (int j= 0;j<jcModel.getStructureList().size();j++){
                                XqfxZjModel zjModel = jcModel.getStructureList().get(j);
                                treeDatas.add(new MyNodeBean(zjModel.getStructureId(), jcModel.getMaterialId(), zjModel.getStructureName()+"("+zjModel.getKnowledgePoint()+")",1));
                                for (int k=0;k<zjModel.getKnowledge().size();k++){
                                    XqfxZsdModel zsdModel = zjModel.getKnowledge().get(k);
                                    treeDatas.add(new MyNodeBean(zsdModel.getKnowledgeId(), zjModel.getStructureId(), zsdModel.getKnowledgeName()+"("+zsdModel.getKnowledgePoint()+")",2));
                                }
                            }
                        }
                        //treeAdapter.updateView(false);//更新没效果，不知道是adapter的原因还是mylistview的原因
                        try {
                            treeAdapter.updateAdapter(treeDatas,0,true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        treeLv.setVisibility(View.GONE);
                        bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }


    List<ErrorSubjectModel> majorModelList = new ArrayList<ErrorSubjectModel>();//学科列表
    /**
     * 获取学科
     */
    private void getMaJorList() {
        majorModelList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(null) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<ErrorSubjectModel> list = res.getData();
                    if(list!=null&&list.size()>0){
                        majorModelList.addAll(list);
                        for (int i = 0;i<list.size();i++){
                            list_qd.add(list.get(i).getName());
                        }
                        adapter_title.notifyDataSetChanged();//执行spinner的setOnItemSelectedListener
                    }
                    else{
                        treeLv.setVisibility(View.GONE);
                        bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                        TextView tv = bind(R.id.tv_error_text);
                        tv.setText("还没有数据分析奥~");
                    }
                }
            }
        });
    }
}
