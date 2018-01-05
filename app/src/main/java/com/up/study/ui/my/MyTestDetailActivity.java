package com.up.study.ui.my;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ImgUrl;
import com.up.study.model.TestModel;
import com.up.study.ui.home.TaskTestErrorInputActivity;
import com.up.study.weight.showimages.ImagePagerActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的试卷详情
 */
public class MyTestDetailActivity extends BaseFragmentActivity {
    private ListView lv ;
    private CommonAdapter<ImgUrl> adapter;
    private List<ImgUrl> dataList = new ArrayList<ImgUrl>();

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_test_detail;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        int paperId = getIntent().getIntExtra(Constants.KEY,0);//试卷id
        if(paperId==0){
            showToast("请传递试卷id");
            finish();
            return;
        }
        getTest(paperId+"");
        adapter = new CommonAdapter<ImgUrl>(ctx, dataList, R.layout.item_img) {
            @Override
            public void convert(ViewHolder vh, ImgUrl item, int position) {
                ImageView tv_title = vh.getView(R.id.iv_img);
                J.image().load(ctx, item.getUrl(), tv_title);
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ArrayList<String> imageShow = new ArrayList<String>();
                for (int i = 0; i < dataList.size(); i++) {
                    imageShow.add(dataList.get(i).getUrl());
                }
                Intent intent = new Intent(MyTestDetailActivity.this, ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageShow);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                MyTestDetailActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 获取试卷详情
     * relationId 试卷ID、任务ID
     */
    private void getTest(String paper_id) {
        HttpParams params = new HttpParams();
        params.put("paper_id", paper_id);
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.MY_TEST_DETAIL, ctx, params, new HttpCallback<Respond<TestModel>>(ctx) {
            @Override
            public void success(Respond<TestModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    TestModel  model = res.getData();
                    String imgUrls = null;
                    if (model!=null){
                        imgUrls = model.getAttached();
                    }
                    try {
                        if (TextUtils.isEmpty(imgUrls)){
                            showToast("未上传试卷");
                        }
                        else{
                            Type type=new TypeToken<List<ImgUrl>>(){}.getType();
                            List<ImgUrl> list = new Gson().fromJson(imgUrls,type);
                            dataList.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    catch (Exception e){
                        showToast("标准试卷地址异常");
                    }
                }
            }
        });
    }
}
