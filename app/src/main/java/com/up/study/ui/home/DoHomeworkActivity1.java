package com.up.study.ui.home;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.common.widget.MyListView;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.RecyclerViewAdapter;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.listener.CameraResultCallback;
import com.up.study.model.Options;
import com.up.study.model.SeqModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 开始做题（家庭作业）备份
 */
public class DoHomeworkActivity1 extends BaseFragmentActivity {
    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();
    private TopicPagerAdapter adapter;
    private List<SeqModel> mSeqList = new ArrayList<SeqModel>();

    private TextView tv_num,tv_right;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast("onNewIntent");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_do_homework;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_num = bind(R.id.tv_num);
        tv_right = bind(R.id.tv_right);

    }

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getHomeWorkList();
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
                if(Respond.SUC.equals(res.getCode())){
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
        //TextView tv_subject_title = (TextView)view.findViewById(R.id.tv_subject_title);
        tv_subject_type.setText(seqModel.getSubjectType());

        String knowName =seqModel.getKnowName();
        Type type1=new TypeToken<List<String>>(){}.getType();
        List<String> list2 = new Gson().fromJson(knowName,type1);

        //找到这个Listview
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recylist);
        //设置线性管理器
        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mRecyclerView.setLayoutManager(ms);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(list2);
        mRecyclerView.setAdapter(myAdapter);


        //客观题初始化选项
        if (StudyUtils.getSubjectType(seqModel.getSubjectType())==StudyUtils.TYPE_KGT){
            //tv_subject_title.setText(seqModel.getTitle());
            WebView wv_title = (WebView) view.findViewById(R.id.wv_title);
            WidgetUtils.initWebView(wv_title, seqModel.getTitle());

            String options = seqModel.getOptions();
            Type type=new TypeToken<List<Options>>(){}.getType();
            final List<Options> optionsList = new Gson().fromJson(options,type);
            StudyUtils.clearOptionList(optionsList);

            final MyListView lv = (MyListView)view.findViewById(R.id.lv);
            final CommonAdapter<Options> adapter = new CommonAdapter<Options>(ctx, optionsList, R.layout.item_topic_kgt) {
                @Override
                public void convert(ViewHolder vh, Options item, int position) {
                    TextView tv_answer = vh.getView(R.id.tv_answer);
                    TextView tv_answer_text = vh.getView(R.id.tv_answer_text);
                    tv_answer.setText(item.getOpt());
                    tv_answer_text.setText(item.getText());
                    if(item.getStatus()==1){
                        tv_answer.setBackgroundResource(R.drawable.round_blue_circle_background);
                    }
                    else if(item.getStatus()==2){
                        tv_answer.setBackgroundResource(R.drawable.round_pink_circle_background);
                    }
                }
            };
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                    if (lv.getTag()==null){//只能点击一次
                        submitKgt(view,seqModel,position,optionsList,adapter);
                        lv.setTag(0);
                    }

                }
            });
        }
        //主观题初始化内容
        else{
            WebView webView = (WebView) view.findViewById(R.id.web_view);
            webView.setVisibility(View.VISIBLE);
            WebSettings mysettings = webView.getSettings();
            mysettings.setSupportZoom(true);
            mysettings.setJavaScriptEnabled(true);// 设置支持Javascript
            //mysettings.setJavaScriptCanOpenWindowsAutomatically(true);
            //mysettings.setBuiltInZoomControls(true);
            mysettings.setDomStorageEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.requestFocus();// 触摸焦点起作用
            //webView.loadUrl("http://mall.5i84.cn/?CASTGC=TGT-93962-ajhGWVtVTP7QG0kKVRPiALaZN9ynr7HWLd9blBgpld4VgOS5rO-5i84.cn");
           // webView.loadData(seqModel.getTitle(),null,"UTF-8");
            webView.loadDataWithBaseURL(null,seqModel.getTitle(), "text/html",  "utf-8", null);

            MyListView lv = (MyListView)view.findViewById(R.id.lv);
            lv.setVisibility(View.GONE);

            /*TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(seqModel.getTitle());*/

            LinearLayout ll_camera = (LinearLayout)view.findViewById(R.id.ll_camera);
            ll_camera.setVisibility(View.VISIBLE);

            final ImageView iv_camera_1 = (ImageView)view.findViewById(R.id.iv_camera_1);
            iv_camera_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(1, new CameraResultCallback(iv_camera_1, new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {
                            submitZgt(view,seqModel);
                        }
                    }));
                }
            });
            final ImageView iv_camera_2 = (ImageView)view.findViewById(R.id.iv_camera_2);
            iv_camera_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(2, new CameraResultCallback(iv_camera_2,new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {

                        }
                    }));
                }
            });
            final ImageView iv_camera_3 = (ImageView)view.findViewById(R.id.iv_camera_3);
            iv_camera_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(3, new CameraResultCallback(iv_camera_3,new CameraResultCallback.CameraSucCallBack() {
                        @Override
                        public void success(String filePath) {

                        }
                    }));
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        if(v==tv_right){
            gotoActivity(AnswerSheetActivity.class,null);
        }
    }
    /**
     * 客观题提交答案
     */
    private void submitKgt(final View view,SeqModel seqModel, final int position, final List<Options> optionsList, final CommonAdapter<Options> adapter) {
        HttpParams params = new HttpParams();
        params.put("relationId", TApplication.getInstant().getRelationId());
        params.put("subjectId", seqModel.getSubjectId());
        params.put("classsId", TApplication.getInstant().getClassId());
        params.put("studentId", TApplication.getInstant().getStudentId());
        params.put("content", optionsList.get(position).getOpt());
       // params.put("attached", "");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(ctx) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if(Respond.SUC.equals(res.getCode())){
                    SeqModel model = res.getData();

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
                    }

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
        params.put("content", "A");
        params.put("attached", "head.png");
        J.http().post(Urls.START_DOING, ctx, params, new HttpCallback<Respond<SeqModel>>(ctx) {
            @Override
            public void success(Respond<SeqModel> res, Call call, Response response, boolean isCache) {

                if(Respond.SUC.equals(res.getCode())){
                    SeqModel model = res.getData();

                    LinearLayout ll_anwser = (LinearLayout)view.findViewById(R.id.ll_anwser);
                    TextView tv_answer = (TextView)view.findViewById(R.id.tv_answer);
                    TextView tv_analysis = (TextView)view.findViewById(R.id.tv_analysis);
                    ll_anwser.setVisibility(View.VISIBLE);
                    tv_answer.setText(model.getAnswer());
                    tv_analysis.setText(model.getAnalysis());

                }
            }
        });
    }

}
