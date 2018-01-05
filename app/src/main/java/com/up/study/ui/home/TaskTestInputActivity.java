package com.up.study.ui.home;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.BaseBean;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.common.utils.Logger;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ImgUrl;
import com.up.study.model.TaskModel;
import com.up.study.model.TestModel;
import com.up.study.weight.showimages.ImagePagerActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
//┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃ 　
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//  ┃　　　┃   神兽保佑　　　　　　　　
//  ┃　　　┃   代码少BUG！
//  ┃　　　┗━━━┓
//  ┃　　　　　　　┣┓
//  ┃　　　　　　　┏┛
//  ┗┓┓┏━┳┓┏┛
//    ┃┫┫　┃┫┫
//    ┗┻┛　┗┻┛
/**
 * 任务-试卷录入-标准试卷
 */
public class TaskTestInputActivity extends BaseFragmentActivity {
    private TextView tv_next,tv_subject_title,tv_subject_num,tv_teacher,tv_subject_time;
    private ListView lv ;
    private CommonAdapter<ImgUrl> adapter;
    private List<ImgUrl> dataList = new ArrayList<ImgUrl>();
    private int relationId;
    @Override
    protected int getContentViewId() {
        return R.layout.act_task_test_input;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_next = bind(R.id.tv_next);
        tv_subject_title = bind(R.id.tv_subject_title);
        tv_subject_num = bind(R.id.tv_subject_num);
        tv_teacher = bind(R.id.tv_teacher);
        tv_subject_time = bind(R.id.tv_subject_time);
    }

    @Override
    protected void initEvent() {
        tv_next.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        relationId = getIntent().getIntExtra(Constants.KEY,0);
        if(relationId==0){
            showToast("请传递试卷id");
            finish();
            return;
        }
        SPUtil.saveTaskMesId(this,relationId+"",1);

        getTest(relationId+"");
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
                ArrayList<String> temp = StudyUtils.imgUrl2String(dataList);
                Intent intent = new Intent(ctx, ImagePagerActivity.class);
                intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, temp);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==tv_next){
            gotoActivity(StuTestActivity.class,relationId);
        }
    }

    /**
     * 获取标准试卷
     * relationId 试卷ID、任务ID
     */
    private void getTest(String relationId) {
        //dataList.clear();
        HttpParams params = new HttpParams();
        params.put("relationId", relationId);
        J.http().post(Urls.GET_PAPERS_DETAILS, ctx, params, new HttpCallback<Respond<TestModel>>(ctx) {
            @Override
            public void success(Respond<TestModel> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    TestModel  model = res.getData();
                    String imgUrls = model.getAttached();

                    try {
                        if (TextUtils.isEmpty(imgUrls)){
                            showToast("未上传标准试卷");
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


                    //J.image().load(ctx, null, imageView);
                    tv_subject_title.setText(model.getTitle());
                    tv_subject_num.setText(model.getSubjectSum()+"题");
                    tv_teacher.setText("老师："+model.getName());
                    tv_subject_time.setText(model.getDeadline()+"截止");
                }
            }
        });
    }

}
