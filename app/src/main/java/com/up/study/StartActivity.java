package com.up.study;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.SPUtil;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.Login;
import com.up.study.ui.login.LoginActivity;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/5/20.
 */

public class StartActivity extends BaseFragmentActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.act_start;
    }

    @Override
    protected void initView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userId= SPUtil.getString(ctx, Constants.SP_USER_ID,"");
                if (TextUtils.isEmpty(userId)){
                    gotoActivity(LoginActivity.class,null);
                }else {
                    String userPhone = SPUtil.getString(StartActivity.this, Constants.SP_USER_PHONE,"");
                    String userName = SPUtil.getString(StartActivity.this,Constants.SP_USER_NAME,"");
                    String classId =  SPUtil.getString(StartActivity.this,Constants.SP_CLASS_ID,"");
                    String studentId =  SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_ID,"");
                    String gradeId =  SPUtil.getString(StartActivity.this,Constants.SP_GRADE_ID,"");

                    if (!TextUtils.isEmpty(userId)){
                        TApplication.getInstant().setUserId(Integer.parseInt(userId));
                    }
                    if(!TextUtils.isEmpty(classId)){
                        TApplication.getInstant().setClassId(Integer.parseInt(classId));
                    }
                    if(!TextUtils.isEmpty(studentId)){
                        TApplication.getInstant().setStudentId(Integer.parseInt(studentId));
                    }
                    if (!TextUtils.isEmpty(gradeId)){
                        TApplication.getInstant().setGradeId(Integer.parseInt(gradeId));
                    }
                    TApplication.getInstant().setPhone(SPUtil.getString(StartActivity.this, Constants.SP_USER_PHONE,""));
                    TApplication.getInstant().setUserName(SPUtil.getString(StartActivity.this,Constants.SP_USER_NAME,""));
                    TApplication.getInstant().setStudentNum(SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_NUM,""));
                    TApplication.getInstant().setStudentName(SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_NAME,""));
                    TApplication.getInstant().setClassName(SPUtil.getString(StartActivity.this,Constants.SP_CLASS_NAME,""));

                    gotoActivity(MainActivity.class,null);
                }
                finish();
            }
        },100);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {

    }
}


/*
public class StartActivity extends BaseFragmentActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.act_start;
    }

    @Override
    protected void initView() {

        String userId= SPUtil.getString(ctx, Constants.SP_USER_ID,"");
        if (TextUtils.isEmpty(userId)){
            gotoActivity(LoginActivity.class,null);
        }else {
            login();
        }


       */
/* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userId= SPUtil.getString(ctx, Constants.SP_USER_ID,"");
                if (TextUtils.isEmpty(userId)){
                    gotoActivity(LoginActivity.class,null);
                }else {
                    login();
                }
                finish();
            }
        },100);*//*

    }

    private void loginSuc(){
        String userId= SPUtil.getString(ctx, Constants.SP_USER_ID,"");
        String classId =  SPUtil.getString(StartActivity.this,Constants.SP_CLASS_ID,"");
        String studentId =  SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_ID,"");

        if (!TextUtils.isEmpty(userId)){
            TApplication.getInstant().setUserId(Integer.parseInt(userId));
        }
        if(!TextUtils.isEmpty(classId)){
            TApplication.getInstant().setClassId(Integer.parseInt(classId));
        }
        if(!TextUtils.isEmpty(studentId)){
            TApplication.getInstant().setStudentId(Integer.parseInt(studentId));
        }
        TApplication.getInstant().setPhone(SPUtil.getString(StartActivity.this, Constants.SP_USER_PHONE,""));
        TApplication.getInstant().setUserName(SPUtil.getString(StartActivity.this,Constants.SP_USER_NAME,""));
        TApplication.getInstant().setStudentNum(SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_NUM,""));
        TApplication.getInstant().setStudentName(SPUtil.getString(StartActivity.this,Constants.SP_STUDENT_NAME,""));
        TApplication.getInstant().setClassName(SPUtil.getString(StartActivity.this,Constants.SP_CLASS_NAME,""));

        gotoActivity(MainActivity.class,null);
        finish();
    }

    */
/**
     * 每次直接进入主界面都做隐藏登录,方便服务端记录登录状态
     *//*

    private void login() {
        String userTel = TApplication.getInstant().getPhone();
        String userPassword = SPUtil.getString(this, Constants.SP_USER_PSW,"");
        */
/*if (TextUtils.isEmpty(userTel)){
            showToast("手机号码不能为空");
            return;
        }
        if (TextUtils.isEmpty(userPassword)){
            showToast("密码不能为空");
            return;
        }*//*

        HttpParams params = new HttpParams();
        params.put("account", userTel);
        params.put("password",userPassword);
        J.http().post(Urls.LOGIN, ctx, params, new HttpCallback< Respond<Login>>(null) {
            @Override
            public void success(Respond<Login> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    Login loginData = res.getData();
                    if (loginData.getUser()!=null) {
                        SPUtil.putString(StartActivity.this, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
                        SPUtil.putString(StartActivity.this,Constants.SP_USER_NAME,loginData.getUser().getName());
                        SPUtil.putString(StartActivity.this,Constants.SP_USER_ID,loginData.getUser().getId()+"");

                        TApplication.getInstant().setPhone(loginData.getUser().getAccount());
                        TApplication.getInstant().setUserId(loginData.getUser().getId());
                        TApplication.getInstant().setUserName(loginData.getUser().getName());
                    }

                    if (loginData.getClasss()!=null){
                        SPUtil.putString(StartActivity.this,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
                        SPUtil.putString(StartActivity.this,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
                        TApplication.getInstant().setClassId(loginData.getClasss().getId());
                        TApplication.getInstant().setClassName(loginData.getClasss().getName());
                    }

                    if (loginData.getStudent()!=null) {
                        SPUtil.putString(StartActivity.this, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
                        SPUtil.putString(StartActivity.this, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
                        SPUtil.putString(StartActivity.this, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
                        TApplication.getInstant().setStudentId(loginData.getStudent().getId());
                        TApplication.getInstant().setStudentName(loginData.getStudent().getName());
                        TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
                    }
                    loginSuc();
                }
            }
        });
    }
    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {

    }
}*/
