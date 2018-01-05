package com.up.study.ui.errors;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.ImgCallBack;
import com.up.study.model.Options;
import com.up.study.model.SeqModel;
import com.up.study.model.ZnzjModel;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 智能组卷（开始做题）
 */
public class SmartOrganizationResultActivity extends BaseFragmentActivity {

    TextView tv_num_1,tv_time;
    Button btn_begin,btn_import;

    //练习卷
    private LinearLayout ll_lxj,ll_lxj_down;
    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();
    private TopicPagerAdapter adapter;
    private List<SeqModel> mSeqList = new ArrayList<SeqModel>();

    private TextView tv_num;

    ZnzjModel znzjModel;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int subjectId = intent.getIntExtra(Constants.KEY,0);
        for (int i = 0;i<mSeqList.size();i++){
            if (mSeqList.get(i).getSubjectId()==subjectId){
                viewPager.setCurrentItem(i,false);
                break;
            }
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_smart_result;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);

        //练习卷
        ll_lxj = bind(R.id.ll_lxj);
        ll_lxj_down = bind(R.id.ll_lxj_down);
        tv_num_1 = bind(R.id.tv_num_1);
        tv_time = bind(R.id.tv_time);
        btn_begin = bind(R.id.btn_begin);
        btn_import = bind(R.id.btn_import);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tv_time.setText(sdf.format(new Date()));

    }

    @Override
    protected void initEvent() {

        btn_begin.setOnClickListener(this);
        btn_import.setOnClickListener(this);
        registerForContextMenu(btn_import);//btn是要点击的控件
    }

    @Override
    protected void initData() {
        znzjModel = (ZnzjModel)getIntent().getSerializableExtra("bean1");
        //getSmartTest();

        if (znzjModel.isEasySet()){//简易设置
            getJySmartTest();
        }
        else{//高级设置
            getGjSmartTest();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btn_begin){
            if(znzjModel!=null){
                ll_lxj_down.setVisibility(View.VISIBLE);
                ll_lxj.setVisibility(View.GONE);
            }
        }
        else if(v==btn_import){
            v.showContextMenu();//单击直接显示Context菜单
//            importImg();
        }
    }

    /**
     * 获取智能组卷试卷(高级)
     */
    private void getGjSmartTest() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());//学生id
        params.put("major_id", znzjModel.getMajor_id());
        params.put("rows",znzjModel.getRows());
        params.put("material_id",znzjModel.getMaterial_id());
        params.put("subject_type",znzjModel.getSubject_type());
        params.put("chapter_id",znzjModel.getChapter_id());
        params.put("knowledge",znzjModel.getKnowledge());
        params.put("difficulty",znzjModel.getDifficulty());
        J.http().post(Urls.SMART_PAPERS, ctx, params, new HttpCallback<Respond<List<SeqModel>>>(ctx) {
            @Override
            public void success(Respond<List<SeqModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    mSeqList = res.getData();
                    if(mSeqList.size()==0){
                        showToast("未找到匹配的题目");
                        SmartOrganizationResultActivity.this.finish();
                        return;
                    }

                    if (Integer.parseInt(znzjModel.getRows())==mSeqList.size()) {
                        tv_num_1.setText(mSeqList.size() + "题");
                    }
                    else{
                        tv_num_1.setText("为您找到的相关题目有"+mSeqList.size() + "题");
                    }
                    initSeqCardData();
                }
            }
        });

    }
    /**
     * 获取智能组卷试卷(简易)
     */
    private void getJySmartTest() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());//学生id
        params.put("major_id", znzjModel.getMajor_id());
        params.put("rows",znzjModel.getRows());
        J.http().post(Urls.SMART_EASY, ctx, params, new HttpCallback<Respond<List<SeqModel>>>(ctx) {
            @Override
            public void success(Respond<List<SeqModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    mSeqList = res.getData();
                    if(mSeqList.size()==0){
                        showToast("未找到匹配的题目");
                        SmartOrganizationResultActivity.this.finish();
                        return;
                    }
                    if (Integer.parseInt(znzjModel.getRows())==mSeqList.size()) {
                        tv_num_1.setText(mSeqList.size() + "题");
                    }
                    else{
                        tv_num_1.setText("为您找到的相关题目有"+mSeqList.size() + "题");
                    }
                    initSeqCardData();
                }
            }
        });

    }
    //导出图片
    private void importImg() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());//学生id
        params.put("major_id", znzjModel.getMajor_id());
        params.put("rows",znzjModel.getRows());
        params.put("material_id",znzjModel.getMaterial_id());
        params.put("subject_type",znzjModel.getSubject_type());
        params.put("chapter_id",znzjModel.getChapter_id());
        params.put("knowledge",znzjModel.getKnowledge());
        params.put("difficulty",znzjModel.getDifficulty());
        if (znzjModel.isEasySet()){
            params.put("flag","jd");
        }
        else {
            params.put("flag","gj");
        }

        OkGo.get(Urls.ZNZJ_EXPORT_PIC)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback("智能组卷"+"_"+new Date().getTime()+"_qsx.png") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
                        showToast("下载完成，图片已经保存在sdcard/download文件下");
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }
    //导出word
    private void importDoc() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());//学生id
        params.put("major_id", znzjModel.getMajor_id());
        params.put("rows",znzjModel.getRows());
        params.put("material_id",znzjModel.getMaterial_id());
        params.put("subject_type",znzjModel.getSubject_type());
        params.put("chapter_id",znzjModel.getChapter_id());
        params.put("knowledge",znzjModel.getKnowledge());
        params.put("difficulty",znzjModel.getDifficulty());
        if (znzjModel.isEasySet()){
            params.put("flag","jd");
        }
        else {
            params.put("flag","gj");
        }
        OkGo.get(Urls.ZNZJ_EXPORT_DOC)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback("智能组卷"+"_"+new Date().getTime()+"_qsx.doc") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
//                        showToast("下载完成，图片已经保存在sdcard/download文件下");
                        Toast.makeText(SmartOrganizationResultActivity.this, "下载完成，word已经保存在sdcard/download文件下", Toast.LENGTH_LONG).show();
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.load,menu);
        //menu.setHeaderTitle("选择导出方式");
        /*menu.add(0, 3, 0, "修改");
        menu.add(0, 4, 1, "删除");*/
        //给menu设置布局文件，当触发时显示在界面上
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //布局文件里面对应的id，当点击时，根据id区别那个被点击
        switch (item.getItemId()){
            case R.id.text1:
                showLog("=========importImg=================");
                importImg();
                break;
            case R.id.text2:
                showLog("=========importDoc=================");
                importDoc();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取智能组卷试卷
     */
    private void getSmartTest() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());//学生id
        params.put("major_id", znzjModel.getMajor_id());
        params.put("rows",znzjModel.getRows());
        params.put("subject_type",znzjModel.getSubject_type());
        params.put("chapter_id",znzjModel.getChapter_id());
        params.put("knowledge",znzjModel.getKnowledge());
        params.put("difficulty",znzjModel.getDifficulty());
        J.http().post(Urls.SMART_PAPERS, ctx, params, new HttpCallback<Respond<List<SeqModel>>>(ctx) {
            @Override
            public void success(Respond<List<SeqModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    mSeqList = res.getData();
                    if(mSeqList.size()==0){
                        showToast("未找到匹配的题目");
                        SmartOrganizationResultActivity.this.finish();
                        return;
                    }
                    initSeqCardData();
                    try {

                    }catch (Exception e){
                        e.printStackTrace();
                        showToast("返回数据有问题");
                        SmartOrganizationResultActivity.this.finish();
                    }

                }
            }
        });

    }

    private void initSeqCardData(){
        mInflater = getLayoutInflater();
        for (int i = 0; i <mSeqList.size(); i++) {
            View view = mInflater.inflate(R.layout.view_homework_seq, null);
            initInflaterView(view,mSeqList.get(i));
            mViews.add(view);
        }
        tv_num.setText(1 + "/"+mSeqList.size());

        adapter = new TopicPagerAdapter(mViews);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                tv_num.setText(position + 1 + "/"+mSeqList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        adapter.notifyDataSetChanged();
    }
    private void initInflaterView(final View view, final SeqModel seqModel) {
        TextView tv_subject_type = (TextView)view.findViewById(R.id.tv_subject_type);
        tv_subject_type.setText(seqModel.getSubjectType());
        //TextView tv_subject_title = (TextView)view.findViewById(R.id.tv_subject_title);
        final TextView tv_submit = (TextView)view.findViewById(R.id.tv_submit);//提交答案
        //需要提交按钮吗
        if (StudyUtils.getNeedSumitBtn(seqModel.getSubjectType())) {
            tv_submit.setVisibility(View.VISIBLE);
        }

        String knowName = seqModel.getKnowName();
        try{
            if(knowName!=null){
                //List<String> list2 = Arrays.asList(knowName.split(","));
                /*Type type1 = new TypeToken<List<String>>() {}.getType();
                List<String> knowNameList = new Gson().fromJson(knowName, type1);*/
                List<String> knowNameList = Arrays.asList(knowName.split(","));
                //找到这个Listview
                RecyclerView mRecyclerView =(RecyclerView)view.findViewById(R.id.recylist);
                //设置线性管理器
                LinearLayoutManager ms = new LinearLayoutManager(ctx);
                ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
                mRecyclerView.setLayoutManager(ms);
                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(knowNameList);
                mRecyclerView.setAdapter(myAdapter);
            }
        }catch (Exception e){
            Logger.e(Logger.TAG,"知识点格式有误");
        }


        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType()) == StudyUtils.TYPE_KGT) {

            //tv_subject_title.setText(seqModel.getTitle());
            WebView wv_title = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());

            final String options = seqModel.getOptions();
            final Type type = new TypeToken<List<Options>>() {
            }.getType();
            final List<Options> optionsList = new Gson().fromJson(options, type);
            StudyUtils.clearOptionList(optionsList);
            final MyListView lv =(MyListView)view.findViewById(R.id.lv);
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
                    }
                    else if (item.getStatus() == 4) {
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
                            lv.setTag(0);
                           // submitKgt(view,seqModel,position,optionsList,adapter);
                            LinearLayout ll_anwser = (LinearLayout)view.findViewById(R.id.ll_anwser);
                            //TextView tv_answer = (TextView) view.findViewById(R.id.tv_right_answer);


                            WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                            WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());

                            ImageView iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
                            ImageView iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);
                            ll_anwser.setVisibility(View.VISIBLE);
                            //tv_analysis.setText(seqModel.getAnalysis());

                            String answerjson = seqModel.getAnswer();
                            Type type1 = new TypeToken<List<String>>() {
                            }.getType();
                            List<String> answerList = new Gson().fromJson(answerjson, type1);

                            String answer = "";
                            if(answerList!=null&&answerList.size()>0){
                                answer = answerList.get(0);
                            }
                            //tv_answer.setText(answer);
                            WebView wv_answer = (WebView)view.findViewById(R.id.wv_answer);
                            WidgetUtils.initAnswerWebView(wv_answer,seqModel.getAnswer());

                            if (answer.equals(optionsList.get(position).getOpt())) {//回答正确
                                optionsList.get(position).setStatus(1);
                                adapter.notifyDataSetChanged();
                                iv_answer_right.setVisibility(View.VISIBLE);
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
                        tv_submit.setVisibility(View.GONE);
                       // submitKgt(view,seqModel,-1,optionsList,adapter);
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
                        if(answerList==null){
                            answerList = new ArrayList<String>();
                        }
                        ll_anwser.setVisibility(View.VISIBLE);
                        //tv_answer.setText(FormatUtils.List2String(answerList));
                        WebView wv_answer = (WebView)view.findViewById(R.id.wv_answer);
                        WidgetUtils.initAnswerWebView(wv_answer,seqModel.getAnswer());
                        //该题错与对逻辑：回答正确数目和答案数目一直、并且选中数目和答案数目一致，就是对的。
                        //选项错与对逻辑：选了要么错要么对，不选要么错，要么无状态
                        int rightNum = 0;//回答正确的数目
                        int selectNum = 0;//选中数目
                        for (int i = 0; i < optionsList.size(); i++) {
                            if(optionsList.get(i).getStatus()==3) {//选中状态
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
                                }
                                else{
                                    optionsList.get(i).setStatus(2);
                                }
                            }
                            else{//未选中状态
                                boolean isHaving = false;//判断是否有对应的答案
                                for (int j = 0; j < answerList.size(); j++) {
                                    if (answerList.get(j).equals(optionsList.get(i).getOpt())) {
                                        isHaving = true;
                                        break;
                                    }
                                }
                                if (isHaving) {//不选要么错，要么无状态
                                    optionsList.get(i).setStatus(4);//未选中却在正确答案里，所以这个选项错了
                                }
                                else{
                                    optionsList.get(i).setStatus(0);
                                }
                            }
                        }


                        if (rightNum==answerList.size()&&rightNum==selectNum) {//回答正确  回答正确数目和答案数目一直，并且选中数目和答案数目一致,就是对的。
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
            final List<String> imgList = new ArrayList<String>();
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            WidgetUtils.initWebView(webView, seqModel.getTitle());

            MyListView lv = (MyListView) view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);

            LinearLayout ll_camera = (LinearLayout) view.findViewById(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);

            StudyUtils.initImgView(view, imgList, this,
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                           // showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                          //  showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    },
                    new ImgCallBack() {
                        @Override
                        public void suc(String imgPath) {
                           // showToast(imgPath);
                            imgList.add(imgPath);
                        }
                    }
            );

            /*final ImageView iv_camera_1 = (ImageView) view.findViewById(R.id.iv_camera_1);
            iv_camera_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(1, new CameraResultCallback(iv_camera_1, new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {
                            //submitZgt(view,seqModel);
                            imgList.add(filePath);
                        }
                    }));
                }
            });
            final ImageView iv_camera_2 = (ImageView) view.findViewById(R.id.iv_camera_2);
            iv_camera_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(2, new CameraResultCallback(iv_camera_2, new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {
                            imgList.add(filePath);
                        }
                    }));
                }
            });
            final ImageView iv_camera_3 = (ImageView) view.findViewById(R.id.iv_camera_3);
            iv_camera_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(3, new CameraResultCallback(iv_camera_3, new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {
                            imgList.add(filePath);
                        }
                    }));
                }
            });*/

            tv_submit.setOnClickListener(new View.OnClickListener() {//都要传图片的，填空，计算，应用，解答
                @Override
                public void onClick(View v) {
                    if(imgList.size()<=0){
                        showToast("请选择答题图片");
                        return;
                    }
                   // submitZgt(view,seqModel);
                    tv_submit.setVisibility(View.GONE);

                    LinearLayout ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
                    //TextView tv_answer = (TextView) view.findViewById(R.id.tv_right_answer);

                    WebView wv_analysis = (WebView) view.findViewById(R.id.wv_analysis);
                    WidgetUtils.initWebView(wv_analysis, seqModel.getAnalysis());

                    ll_anwser.setVisibility(View.VISIBLE);
                    //tv_answer.setText(FormatUtils.List2String(GsonUtils.toList(seqModel.getAnswer())));
                    WebView wv_answer = (WebView)view.findViewById(R.id.wv_answer);
                    WidgetUtils.initAnswerWebView(wv_answer,seqModel.getAnswer());
                }
            });
        }
    }

    /**
     * 客观题提交答案
     */
    private void submitKgt(final View view,SeqModel seqModel, final int position, final List<Options> optionsList, final CommonAdapter<Options> adapter) {
        String content = "";//客观题答案
        if(position==-1){//多选题
            for (int i = 0;i<optionsList.size();i++){
                if(optionsList.get(i).getStatus()==3){
                    content+=optionsList.get(i).getOpt()+",";
                    showLog("content:"+content);
                }
            }
        }
        else{
            content =  optionsList.get(position).getOpt2(seqModel);
        }
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("content", content);
        // params.put("attached", "");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(null) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if(Respond.SUC.equals(res.getCode())){
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
    private void submitZgt(final View view,SeqModel seqModel) {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("attached", "head.png");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(null) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if(Respond.SUC.equals(res.getCode())){
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

}
