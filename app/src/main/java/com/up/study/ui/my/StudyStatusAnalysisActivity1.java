package com.up.study.ui.my;

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.model.BaseBean;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.LearningAnalysisModel;
import com.up.study.model.LearningAnalysisModel1;
import com.up.study.model.MaterialModel;
import com.up.study.ui.home.TaskActivity;
import com.up.study.weight.treeee.MyNodeBean;
import com.up.study.weight.treeee.MyTreeListViewAdapter;
import com.up.study.weight.treeee.Node;
import com.up.study.weight.treeee.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 学情分析 备份
 */
public class StudyStatusAnalysisActivity1 extends BaseFragmentActivity {
    private MyListView treeLv;
    private MyTreeListViewAdapter<MyNodeBean> treeAdapter;
    List<MyNodeBean> treeDatas = new ArrayList<MyNodeBean>();

   /* private MyListView mlv_difficy;
    CommonAdapter<DifficultyModel> difficultyModelCommonAdapter;
    private List<DifficultyModel> difficultyModelListList = new ArrayList<DifficultyModel>();*/

    private TextView tv_my_right,tv_class_right,tv_grade_right,tv_test_num;

    private Spinner spinner_title;
    private ArrayAdapter<String> adapter_title;
    private List<String> list_qd =  new ArrayList<String>();

    private String curMajorId;
    private int curJcId;
    /*private RecyclerView mRecyclerView;
    private TextView tv_error_num;
    private WrongMsgModel wrongMsgModel;
    private List<WrongMsgSubModel> wrongMsgSubModelList= new ArrayList<WrongMsgSubModel>();
    private ErrorNumRecyclerViewAdapter errorNumRecyclerViewAdapter;*/

   // List<MajorModel> majorModelList = new ArrayList<MajorModel>();//学科列表
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
//        tv_error_num = bind(R.id.tv_error_num);

//        mlv_difficy = bind(R.id.mlv_difficy);

        spinner_title = bind(R.id.spinner_title);

//        mRecyclerView = bind(R.id.recylist);
       /* treeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getZjData(curMajorId,treeDatas.get(position).getId());
            }
        });*/
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

        //习题难度列表
       /* difficultyModelCommonAdapter = new CommonAdapter<DifficultyModel>(ctx, difficultyModelListList, R.layout.item_difficy) {
            @Override
            public void convert(ViewHolder vh, DifficultyModel item, int position) {
                TextView tv_percent = vh.getView(R.id.tv_percent);
                RatingBar ratingBar = vh.getView(R.id.ratingBar);
                ratingBar.setRating(item.getDifficulty());
                tv_percent.setText(item.getDiffCorrectRate()+"%");
            }
        };
        mlv_difficy.setAdapter(difficultyModelCommonAdapter);*/


        //错题量横向列表
        //设置线性管理器
       /* LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setLayoutManager(ms);
        errorNumRecyclerViewAdapter = new ErrorNumRecyclerViewAdapter(wrongMsgSubModelList);
        mRecyclerView.setAdapter(errorNumRecyclerViewAdapter);*/

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
                       /* Toast.makeText(getApplicationContext(), node.getName(),
                                Toast.LENGTH_SHORT).show();*/
                       if(node.getType()==0){
                           getZjData(curMajorId,node.getId());
                           curJcId = node.getId();
                       }
                       else if(node.getType()==1){
                           getZsdData(curMajorId,curJcId,node.getId());
                       }

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
//        difficultyModelListList.clear();
//        wrongMsgSubModelList.clear();
        treeDatas.clear();
        HttpParams params = new HttpParams();
        params.put("majorId", majorId);
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.LEARINGIN_ANALYSIS, ctx, params, new HttpCallback< Respond<LearningAnalysisModel1>>(ctx) {
            @Override
            public void success(Respond<LearningAnalysisModel1> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    LearningAnalysisModel1 model = res.getData();
                    //试卷总数
                    tv_test_num.setText(model.getTotalNum()+"份");
                    tv_my_right.setText(model.getMyCorrectRate()+"");
                    tv_class_right.setText(model.getClassCorrectRate()+"");
                    tv_grade_right.setText(model.getGradeCorrectRate()+"");
                    //习题难度
                    /*difficultyModelListList.addAll(model.getDifficulty());
                    difficultyModelCommonAdapter.notifyDataSetChanged();*/

                    //错题量
                    /*wrongMsgModel = model.getWrongMsg();
                    tv_error_num.setText(wrongMsgModel.getTotalNum()+"");
                    wrongMsgSubModelList.addAll(wrongMsgModel.getSubList());
                    errorNumRecyclerViewAdapter.notifyDataSetChanged();*/

                    //知识点分析
                    List<MaterialModel> materialModelList = model.getMaterialList();
                    if (materialModelList!=null&&materialModelList.size()>0){
                        treeLv.setVisibility(View.VISIBLE);
                        bind(R.id.ll_no_error).setVisibility(View.GONE);
                        for (int i = 0 ;i<materialModelList.size();i++){
                            treeDatas.add(new MyNodeBean(materialModelList.get(i).getMaterialId(), 0, materialModelList.get(i).getMaterialName()+"("+materialModelList.get(i).getKnowledgePoint()+")",0));
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

    /**
     * 获取学情分析---章节数据
     * @param majorId
     */
    private void getZjData(String majorId,final int materialId) {
        HttpParams params = new HttpParams();
        params.put("majorId", majorId);
        params.put("materialId", materialId);
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.LEARINGIN_ANALYSIS_ZJ, ctx, params, new HttpCallback< Respond<List<XqfxZjModel>>>(ctx) {
            @Override
            public void success(Respond<List<XqfxZjModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<XqfxZjModel> list = res.getData();
                    for (int i = 0 ;i<list.size();i++){
                        treeDatas.add(new MyNodeBean(list.get(i).getStructureId(), materialId, list.get(i).getStructureName()+"("+list.get(i).getKnowledgePoint()+")",1));
                    }
                    try {
                        treeAdapter.updateAdapter(treeDatas,1,true);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取学情分析---知识点数据
     * @param majorId
     */
    private void getZsdData(String majorId,int materialId,final int structureId) {
        HttpParams params = new HttpParams();
        params.put("majorId", majorId);
        params.put("materialId", materialId);
        params.put("structureId", structureId);
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        J.http().post(Urls.LEARINGIN_ANALYSIS_ZSD, ctx, params, new HttpCallback< Respond<List<XqfxZsdModel>>>(ctx) {
            @Override
            public void success(Respond<List<XqfxZsdModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<XqfxZsdModel> list = res.getData();
                    for (int i = 0 ;i<list.size();i++){
                        treeDatas.add(new MyNodeBean(list.get(i).getKnowledgeId(), structureId, list.get(i).getKnowledgeName()+"("+list.get(i).getKnowledgePoint()+")",2));
                    }
                    try {
                        treeAdapter.updateAdapter(treeDatas,2,true);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
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

    private class XqfxZjModel extends BaseBean{

        private int structureId;//章节id
        private String structureName;//章节名称
        private double knowledgePoint;//章节分数

        public int getStructureId() {
            return structureId;
        }

        public void setStructureId(int structureId) {
            this.structureId = structureId;
        }

        public String getStructureName() {
            return structureName;
        }

        public void setStructureName(String structureName) {
            this.structureName = structureName;
        }

        public double getKnowledgePoint() {
            return knowledgePoint;
        }

        public void setKnowledgePoint(double knowledgePoint) {
            this.knowledgePoint = knowledgePoint;
        }
    }

    private class XqfxZsdModel extends BaseBean{
        private int knowledgeId;
        private String knowledgeName;
        private double knowledgePoint;

        public int getKnowledgeId() {
            return knowledgeId;
        }

        public void setKnowledgeId(int knowledgeId) {
            this.knowledgeId = knowledgeId;
        }

        public String getKnowledgeName() {
            return knowledgeName;
        }

        public void setKnowledgeName(String knowledgeName) {
            this.knowledgeName = knowledgeName;
        }

        public double getKnowledgePoint() {
            return knowledgePoint;
        }

        public void setKnowledgePoint(double knowledgePoint) {
            this.knowledgePoint = knowledgePoint;
        }
    }
}
