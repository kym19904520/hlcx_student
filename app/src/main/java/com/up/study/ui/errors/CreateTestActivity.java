package com.up.study.ui.errors;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.SeqModel;
import com.up.study.model.ZnzjModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 生成试卷
 */
public class CreateTestActivity extends BaseFragmentActivity {

    TextView tv_num,tv_time;
    Button btn_begin,btn_import;
    ZnzjModel znzjModel;

    private List<SeqModel> mSeqList = new ArrayList<SeqModel>();
    @Override
    protected int getContentViewId() {
        return R.layout.act_create_test;
    }

    @Override
    protected void initView() {
        tv_num = bind(R.id.tv_num);
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
    }

    @Override
    protected void initData() {
        znzjModel = (ZnzjModel)getIntent().getSerializableExtra("bean1");

        if (znzjModel.isEasySet()){//简易设置
            getJySmartTest();
        }
        else{//高级设置
            getGjSmartTest();
        }
//        tv_num.setText(znzjModel.getRows()+"题");
    }


    @Override
    public void onClick(View v) {
        if(v==btn_begin){
            if(znzjModel!=null){
                gotoActivityWithBean(SmartOrganizationResultActivity.class,znzjModel,null);
            }
        }
        else if(v==btn_import){
            importImg();
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
                        CreateTestActivity.this.finish();
                        return;
                    }
                    else{
                        tv_num.setText(mSeqList.size()+"题");
                    }
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
                        CreateTestActivity.this.finish();
                        return;
                    }
                    else{
                        tv_num.setText(mSeqList.size()+"题");
                    }
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
        params.put("subject_type",znzjModel.getSubject_type());
        params.put("chapter_id",znzjModel.getChapter_id());
        params.put("knowledge",znzjModel.getKnowledge());
        params.put("difficulty",znzjModel.getDifficulty());
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
}
