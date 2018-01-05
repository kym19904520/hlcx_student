package com.up.study.ui.home;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.callback.ImgCallBack;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.model.ImgUrl;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题录入（直接录入，没有指定题目）
 */
public class ErrorsInputActivity2 extends BaseFragmentActivity {

    private EditText et_D;
    private String errType;//错误原因选项
    List<String> imgList;

    private TextView tv_submit;

    private Spinner spinner_title;
    private ArrayAdapter<String> adapter_title;
    private List<String> list_qd =  new ArrayList<String>();

    private String curMajorId;
    private ArrayList<String> imageList = new ArrayList<>();
    @Override
    protected int getContentViewId() {
        return R.layout.act_error_input2;
    }

    @Override
    protected void initView() {
        tv_submit = bind(R.id.tv_submit);
        spinner_title = bind(R.id.spinner_title);
    }

    @Override
    protected void initEvent() {
        tv_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        imgList = new ArrayList<String>();
        et_D = bind(R.id.et_D);
        StudyUtils.initErrorCause(this, et_D, new CallBack<String>() {
            @Override
            public void suc(String str) {
                errType = str;
            }
        });
        StudyUtils.initImgSelector(this,imageList,9);
        /*StudyUtils.initImgActivity(this,
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                },
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                },
                new ImgCallBack() {
                    @Override
                    public void suc(String imgPath) {
                        showToast(imgPath);
                        imgList.add(imgPath);
                    }
                }
        );*/

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
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        getMaJorList();
    }

    @Override
    public void onClick(View v) {
        if(v==tv_submit){
            errorInput();
        }
    }

    /**
     * 错题录入
     */
    private void errorInput() {
        if (TextUtils.isEmpty(errType)){
            showToast("请选择错误原因");
            return;
        }
        if (errType.equals("D")&& TextUtils.isEmpty(et_D.getText().toString())){
            showToast("请填写错误描述");
            return;
        }
        List<String> uploadImgs = StudyUtils.getUploadImgs(imageList);
        if(uploadImgs.size()==0){
            showToast("请选择错题图片");
            return;
        }
        //提交图片到阿里云后再提交错题
        showLoading("提交中...",false);
        StudyUtils.uploadImgUrl(uploadImgs,this, new CallBack<List<ImgUrl>>() {
            @Override
            public void suc(List<ImgUrl> obj) {
                hideLoading();
                submitErrorSeq(obj);
            }
        });

    }

    private void submitErrorSeq(List<ImgUrl> imgList) {
        HttpParams params = new HttpParams();
        params.put("cuser_id", TApplication.getInstant().getUserId());
        params.put("student_id", TApplication.getInstant().getStudentId());
        params.put("err_type", errType);
        params.put("err_describe", et_D.getText().toString());
        params.put("err_attached", new Gson().toJson(imgList));
        params.put("major", curMajorId);
        params.put("title", getIntent().getStringExtra("searchContent"));
        J.http().post(Urls.ERROR_INPUT_1, ctx, params, new HttpCallback<Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("录入成功");
                    ErrorsInputActivity2.this.finish();
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
                        curMajorId = majorModelList.get(0).getCode();
                        for (int i = 0;i<list.size();i++){
                            list_qd.add(list.get(i).getName());
                        }
                        adapter_title.notifyDataSetChanged();//执行spinner的setOnItemSelectedListener
                    }
                    else{
                        showToast("没有科目数据，不能直接录入");
                        ErrorsInputActivity2.this.finish();
                    }
                }
            }
        });
    }
}
