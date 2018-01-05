package com.up.study.ui.my;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.SPUtil;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.Login;
import com.up.study.ui.login.LoginActivity;
import com.up.study.ui.login.RegisterActivity;

import okhttp3.Call;
import okhttp3.Response;

public class EditMyInfoActivity extends BaseFragmentActivity {
    private EditText  et_name,et_study_num,et_class;
    @Override
    protected int getContentViewId() {
        return R.layout.act_edit_my_info;
    }

    @Override
    protected void initView() {
        et_name = bind(R.id.et_name);
        et_study_num = bind(R.id.et_study_num);
        et_class = bind(R.id.et_class);

        et_name.setText(TApplication.getInstant().getStudentName());
        et_study_num.setText(TApplication.getInstant().getStudentNum());

    }
    /**
     * 绑定学生
     */
    private void bindingStudent() {
        String sname =  et_name.getText().toString();
        String scode =  et_study_num.getText().toString();
        String ccode =  et_class.getText().toString();
        if(TextUtils.isEmpty(sname)){
            showToast("学生姓名不能为空");
            return;
        }
        if(TextUtils.isEmpty(scode)){
            showToast("学生学号不能为空");
            return;
        }
        if(TextUtils.isEmpty(ccode)){
            showToast("班级邀请码不能为空");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("sname", sname);
        params.put("scode", scode);
        params.put("ccode", ccode);
        J.http().post(Urls.BINDING_STU, ctx, params, new HttpCallback< Respond<Login>>(ctx) {
            @Override
            public void success( Respond<Login> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("绑定成功");
                    Login loginData = res.getData();

                    if (loginData.getUser()!=null) {
                        SPUtil.putString(EditMyInfoActivity.this, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
                        SPUtil.putString(EditMyInfoActivity.this,Constants.SP_USER_NAME,loginData.getUser().getName());
                        SPUtil.putString(EditMyInfoActivity.this,Constants.SP_USER_ID,loginData.getUser().getId()+"");

                        TApplication.getInstant().setPhone(loginData.getUser().getAccount());
                        TApplication.getInstant().setUserId(loginData.getUser().getId());
                        TApplication.getInstant().setUserName(loginData.getUser().getName());
                    }

                    if (loginData.getClasss()!=null){
                        SPUtil.putString(EditMyInfoActivity.this,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
                        SPUtil.putString(EditMyInfoActivity.this,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
                        SPUtil.putString(EditMyInfoActivity.this,Constants.SP_GRADE_ID,loginData.getClasss().getGrade()+"");
                        TApplication.getInstant().setClassId(loginData.getClasss().getId());
                        TApplication.getInstant().setClassName(loginData.getClasss().getName());
                        TApplication.getInstant().setGradeId(loginData.getClasss().getGrade());
                    }

                    if (loginData.getStudent()!=null) {
                        SPUtil.putString(EditMyInfoActivity.this, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
                        SPUtil.putString(EditMyInfoActivity.this, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
                        SPUtil.putString(EditMyInfoActivity.this, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
                        TApplication.getInstant().setStudentId(loginData.getStudent().getId());
                        TApplication.getInstant().setStudentName(loginData.getStudent().getName());
                        TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
                    }

                    EditMyInfoActivity.this.finish();
                    TApplication.getInstant().setRefreshHomeData(true);
                    TApplication.getInstant().setRefreshError(true);
                }
            }
        });
    }
    @Override
    protected void initEvent() {

        bind(R.id.btn_sure).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_sure){
            bindingStudent();
        }
    }

}
