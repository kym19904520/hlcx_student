package com.up.study.ui.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.PhoneUtils;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.WidgetUtils;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.Login;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseFragmentActivity {

    private TextView tv_forget_psw,tv_register;
    private Button btn_login;
    private EditText et_psw,et_phone;
    private ImageView iv_eye;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast("onNewIntent");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_login;
    }

    @Override
    protected void initView() {
        tv_forget_psw = bind(R.id.tv_forget_psw);
        btn_login = bind(R.id.btn_login);
        tv_register = bind(R.id.tv_register);
        et_phone = bind(R.id.et_phone);
        et_psw = bind(R.id.et_psw);
        iv_eye = bind(R.id.iv_eye);

        et_phone.setText(TApplication.getInstant().getPhone());

    }

    @Override
    protected void initEvent() {
        tv_register.setOnClickListener(this);
        tv_forget_psw.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        iv_eye.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        boolean nologin = getIntent().getBooleanExtra("noLogin",false);
        if (nologin){
            SPUtil.clear(ctx);
            showToast("请重新登录");
        }
    }

    @Override
    public void onClick(View v) {
        if(tv_forget_psw==v){
            gotoActivity(ForgetPswActivity.class,null);
        }
        else if(btn_login==v){
            /*EditText et_ip = bind(R.id.et_ip);
            EditText et_port = bind(R.id.et_port);
            String ip = et_ip.getText().toString();
            String port = et_port.getText().toString();
            Urls.setIp(ip,port);*/

            login();//待测试
        }
        else if(v==tv_register){
            gotoActivity(RegisterActivity.class,null);
        }
        else if(v==iv_eye){
            WidgetUtils.eye(iv_eye,et_psw);
        }

    }

        private void login() {
            final String userTel = et_phone.getText().toString();
            final String userPassword = et_psw.getText().toString();
            if (TextUtils.isEmpty(userTel)){
                showToast("手机号码不能为空");
                return;
            }
            if (!PhoneUtils.is(et_phone.getText().toString())){
                showToast("请输入正确的手机号码");
                return;
            }
            if (TextUtils.isEmpty(userPassword)){
                showToast("密码不能为空");
                return;
            }
            HttpParams params = new HttpParams();
            params.put("account", userTel);
            params.put("password",userPassword);
            J.http().post(Urls.LOGIN, ctx, params, new HttpCallback< Respond<Login>>(ctx) {
                @Override
                public void success(Respond<Login> res, Call call, Response response, boolean isCache) {
                    if(Respond.SUC.equals(res.getCode())){
                        SPUtil.clear(LoginActivity.this);
                        Login loginData = res.getData();

                        StudyUtils.saveLoginInfo(LoginActivity.this,loginData,userPassword);
                        String userId = loginData.getUser().getId() + "";
                        if (!userId.isEmpty()){
                            SPUtil.putString(LoginActivity.this, "userId", userId);
                        }
                        if (!TextUtils.isEmpty(loginData.getUser().getHead())) {
                            SPUtil.putString(LoginActivity.this, "head", loginData.getUser().getHead());
                        }
                        TApplication.getInstant().setHasGotoLogin(false);
                        gotoActivity(MainActivity.class,null);
                        LoginActivity.this.finish();
                    }
                }
            });
        }
}
