package com.up.study.ui.login;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.AbsFragmentActivity;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.PhoneUtils;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.TimeUtils;
import com.up.common.utils.WidgetUtils;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.model.Login;
import com.up.study.weight.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class ForgetPswActivity extends AbsFragmentActivity {
    private int mStep=0;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews= new ArrayList<View>();

    private ImageView iv_back,iv_psw,iv_stu,iv_eye;
    private TextView tv_get_code;
    private TextView tv_sms_info;//发送短信提示
    private EditText et_phone,et_psw,et_code;

    @Override
    protected int getContentViewId() {
        return R.layout.act_forget_psw;
    }

    @Override
    protected void initView() {
        viewPager =  bind(R.id.viewpager);
        iv_psw =  bind(R.id.iv_psw);
        iv_stu =  bind(R.id.iv_stu);
        iv_back =  bind(R.id.iv_back);
    }

    @Override
    protected void initEvent() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mViews.clear();
        mInflater = this.getLayoutInflater();
        View view1=mInflater.inflate(R.layout.view_register_1, null);
        initUpdatePsw1(view1);
        mViews.add(view1);
        View view2=mInflater.inflate(R.layout.view_register_2, null);
        initUpdatePsw2(view2);
        mViews.add(view2);
        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.setNoScroll(true);
    }

    private void initUpdatePsw1(View view1) {
        view1.findViewById(R.id.btn_get_code).setOnClickListener(this);
        et_phone = (EditText)view1.findViewById(R.id.et_phone);
    }
    private void initUpdatePsw2(View view2) {
        Button btn_stu = (Button)view2.findViewById(R.id.btn_stu);
        btn_stu.setOnClickListener(this);
        btn_stu.setText("修改密码");
        tv_sms_info  = (TextView)view2.findViewById(R.id.tv_sms_info);
        et_psw = (EditText)view2.findViewById(R.id.et_psw);
        et_psw.setHint("请输入新密码");
        view2.findViewById(R.id.btn_stu).setOnClickListener(this);

        tv_get_code =  (TextView) view2.findViewById(R.id.tv_get_code);
        tv_get_code.setOnClickListener(this);
        et_code = (EditText)view2.findViewById(R.id.et_code);
        iv_eye = (ImageView)view2.findViewById(R.id.iv_eye);
        iv_eye.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_get_code){
            if (!PhoneUtils.is(et_phone.getText().toString())){
                showToast("请输入正确的手机号码");
                return;
            }
            mStep = 1;
            viewPager.setCurrentItem(1);
            iv_psw.setSelected(true);
        }
        else if (v.getId()==R.id.btn_stu){
            verificationPhone();
        }
        else if(v.getId()==R.id.iv_back){
            switch (mStep){
                case 0:
                    this.finish();
                    break;
                case 1:
                    viewPager.setCurrentItem(0);
                    iv_psw.setSelected(false);
                    break;
                default:
                    break;
            }
            mStep--;
        }
        else if(v==tv_get_code){
            getSms(et_phone.getText().toString());
        }
        else if(v.getId()==R.id.iv_eye){
            WidgetUtils.eye(iv_eye,et_psw);
        }
    }
    private void getSms(String phone) {
        HttpParams params = new HttpParams();
        params.put("phone", phone);
        J.http().post(Urls.GET_CODE, ctx, params, new HttpCallback< Respond<String>>(ctx) {
            @Override
            public void success( Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast(res.getMsg());
                    new TimeUtils(tv_get_code,"获取验证码").RunTimer();
                    tv_sms_info.setVisibility(View.VISIBLE);
                    tv_sms_info.setText("验证码已发送到  "+et_phone.getText().toString());
                }
            }
        });
    }
    /**
     * 验证，注册，修改密码 同一个接口
     */
    private void verificationPhone() {
        final String psw = et_psw.getText().toString();
        if (psw.length()<6||psw.length()>16){
            showToast("密码只能为6-16位");
            return;
        }
        HttpParams params = new HttpParams();
        params.put("phone", et_phone.getText().toString());
        params.put("code", et_code.getText().toString());
        params.put("password", et_psw.getText().toString());
        J.http().post(Urls.VERIFICATION_PHONE, ctx, params, new HttpCallback< Respond<Login>>(ctx) {
            @Override
            public void success(Respond<Login> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    mStep = 2;
                    viewPager.setCurrentItem(2);
                    iv_stu.setSelected(true);
                    showToast("修改密码成功");
                    Login loginData = res.getData();
                    StudyUtils.saveLoginInfo(ForgetPswActivity.this,loginData,psw);
                    /*if (loginData.getUser()!=null) {
                        SPUtil.putString(ForgetPswActivity.this, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
                        SPUtil.putString(ForgetPswActivity.this,Constants.SP_USER_NAME,loginData.getUser().getName());
                        SPUtil.putString(ForgetPswActivity.this,Constants.SP_USER_ID,loginData.getUser().getId()+"");

                        TApplication.getInstant().setPhone(loginData.getUser().getAccount());
                        TApplication.getInstant().setUserId(loginData.getUser().getId());
                        TApplication.getInstant().setUserName(loginData.getUser().getName());
                    }

                    if (loginData.getClasss()!=null){
                        SPUtil.putString(ForgetPswActivity.this,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
                        SPUtil.putString(ForgetPswActivity.this,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
                        SPUtil.putString(ForgetPswActivity.this,Constants.SP_GRADE_ID,loginData.getClasss().getGrade()+"");
                        TApplication.getInstant().setClassId(loginData.getClasss().getId());
                        TApplication.getInstant().setClassName(loginData.getClasss().getName());
                        TApplication.getInstant().setGradeId(loginData.getClasss().getGrade());
                    }

                    if (loginData.getStudent()!=null) {
                        SPUtil.putString(ForgetPswActivity.this, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
                        SPUtil.putString(ForgetPswActivity.this, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
                        SPUtil.putString(ForgetPswActivity.this, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
                        TApplication.getInstant().setStudentId(loginData.getStudent().getId());
                        TApplication.getInstant().setStudentName(loginData.getStudent().getName());
                        TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
                    }*/
                    ForgetPswActivity.this.finish();
                }
                else{
                    showToast(res.getMsg());
                }
            }
        });
    }
}
