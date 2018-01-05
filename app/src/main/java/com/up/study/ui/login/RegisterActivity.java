package com.up.study.ui.login;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.AbsFragmentActivity;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.HttpCallback2;
import com.up.common.http.Respond;
import com.up.common.utils.PhoneUtils;
import com.up.common.utils.SPUtil;
import com.up.common.utils.StudyUtils;
import com.up.common.utils.TimeUtils;
import com.up.common.utils.WidgetUtils;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.model.Login;
import com.up.study.weight.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends AbsFragmentActivity {

    private int mStep=0;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews= new ArrayList<View>();

    private ImageView iv_back,iv_psw,iv_stu,iv_eye;
    private TextView tv_get_code;
    private TextView tv_sms_info;//发送短信提示
    private EditText et_phone,et_psw,et_code,et_sname,et_ccode,et_scode;
    @Override
    protected int getContentViewId() {
        return R.layout.act_register;
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
        initRegister1(view1);
        mViews.add(view1);
        View view2=mInflater.inflate(R.layout.view_register_2, null);
        initRegister2(view2);
        mViews.add(view2);
        View view3=mInflater.inflate(R.layout.view_register_3, null);
        initRegister3(view3);
        mViews.add(view3);
        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.setNoScroll(true);
    }

    private void initRegister1(View view1) {
        view1.findViewById(R.id.btn_get_code).setOnClickListener(this);
        et_phone = (EditText) view1.findViewById(R.id.et_phone);
    }

    private void initRegister2(View view2) {
        view2.findViewById(R.id.btn_stu).setOnClickListener(this);
        tv_sms_info  = (TextView)view2.findViewById(R.id.tv_sms_info);
        tv_get_code =  (TextView) view2.findViewById(R.id.tv_get_code);
        tv_get_code.setOnClickListener(this);
        et_psw = (EditText)view2.findViewById(R.id.et_psw);
        et_code = (EditText)view2.findViewById(R.id.et_code);
        iv_eye = (ImageView)view2.findViewById(R.id.iv_eye);
        iv_eye.setOnClickListener(this);
    }

    private void initRegister3(View view3) {
        view3.findViewById(R.id.btn_finish).setOnClickListener(this);
        view3.findViewById(R.id.btn_ranp).setOnClickListener(this);

        et_sname = (EditText) view3.findViewById(R.id.et_sname);
        et_scode = (EditText) view3.findViewById(R.id.et_scode);
        et_ccode = (EditText) view3.findViewById(R.id.et_ccode);
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_get_code){
            if (!PhoneUtils.is(et_phone.getText().toString())){
                showToast("请输入正确的手机号码");
                return;
            }
            getSms(et_phone.getText().toString());
        }
        else if (v.getId()==R.id.btn_stu){//完成注册
            verificationPhone();
        }
        else if (v.getId()==R.id.btn_finish){//立即绑定
            bindingStudent();
        }
        else if(v.getId()==R.id.btn_ranp){//跳过绑定
            gotoActivity(MainActivity.class,null);
            RegisterActivity.this.finish();
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
                case 2:
                    viewPager.setCurrentItem(1);
                    iv_stu.setSelected(false);
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
        J.http().post(Urls.REGISTER_GET_CODE, ctx, params, new HttpCallback2< Respond<String>>(ctx) {
            @Override
            public void success( Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast(res.getMsg());
                    new TimeUtils(tv_get_code,"获取验证码").RunTimer();
                    tv_sms_info.setVisibility(View.VISIBLE);
                    tv_sms_info.setText("验证码已发送到  "+et_phone.getText().toString());
                    mStep = 1;
                    viewPager.setCurrentItem(1);
                    iv_psw.setSelected(true);
                }
                else{
                    showToast(res.getMsg());
                    viewPager.setCurrentItem(0);
                    iv_psw.setSelected(false);
                }
            }
        });
    }

    /**
     * 验证，注册
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
        J.http().post(Urls.VERIFICATION_PHONE, ctx, params, new HttpCallback2< Respond<Login>>(ctx) {
            @Override
            public void success( Respond<Login> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    SPUtil.clear(RegisterActivity.this);
                    mStep = 2;
                    viewPager.setCurrentItem(2);
                    iv_stu.setSelected(true);
                    showToast("注册成功");
                    Login loginData = res.getData();
                    StudyUtils.saveLoginInfo(RegisterActivity.this,loginData,psw);
                    /*if (loginData.getUser()!=null) {
                        SPUtil.putString(RegisterActivity.this, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
                        SPUtil.putString(RegisterActivity.this,Constants.SP_USER_NAME,loginData.getUser().getName());
                        SPUtil.putString(RegisterActivity.this,Constants.SP_USER_ID,loginData.getUser().getId()+"");

                        TApplication.getInstant().setPhone(loginData.getUser().getAccount());
                        TApplication.getInstant().setUserId(loginData.getUser().getId());
                        TApplication.getInstant().setUserName(loginData.getUser().getName());
                    }

                    if (loginData.getClasss()!=null){
                        SPUtil.putString(RegisterActivity.this,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
                        SPUtil.putString(RegisterActivity.this,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
                        SPUtil.putString(RegisterActivity.this,Constants.SP_GRADE_ID,loginData.getClasss().getGrade()+"");
                        TApplication.getInstant().setClassId(loginData.getClasss().getId());
                        TApplication.getInstant().setClassName(loginData.getClasss().getName());
                        TApplication.getInstant().setGradeId(loginData.getClasss().getGrade());
                    }

                    if (loginData.getStudent()!=null) {
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
                        TApplication.getInstant().setStudentId(loginData.getStudent().getId());
                        TApplication.getInstant().setStudentName(loginData.getStudent().getName());
                        TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
                    }
                    SPUtil.putString(RegisterActivity.this, Constants.SP_USER_PSW, psw);*/
                }
                else{
                    showToast(res.getMsg());
                }
            }
        });
    }

    /**
     * 绑定学生
     */
    private void bindingStudent() {
        String sname =  et_sname.getText().toString();
        String scode =  et_scode.getText().toString();
        String ccode =  et_ccode.getText().toString();
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
                        SPUtil.putString(RegisterActivity.this, Constants.SP_USER_PHONE, loginData.getUser().getAccount());
                        SPUtil.putString(RegisterActivity.this,Constants.SP_USER_NAME,loginData.getUser().getName());
                        SPUtil.putString(RegisterActivity.this,Constants.SP_USER_ID,loginData.getUser().getId()+"");

                        TApplication.getInstant().setPhone(loginData.getUser().getAccount());
                        TApplication.getInstant().setUserId(loginData.getUser().getId());
                        TApplication.getInstant().setUserName(loginData.getUser().getName());
                    }

                    if (loginData.getClasss()!=null){
                        SPUtil.putString(RegisterActivity.this,Constants.SP_CLASS_ID,loginData.getClasss().getId()+"");
                        SPUtil.putString(RegisterActivity.this,Constants.SP_CLASS_NAME,loginData.getClasss().getName()+"");
                        SPUtil.putString(RegisterActivity.this,Constants.SP_GRADE_ID,loginData.getClasss().getGrade()+"");
                        TApplication.getInstant().setClassId(loginData.getClasss().getId());
                        TApplication.getInstant().setClassName(loginData.getClasss().getName());
                        TApplication.getInstant().setGradeId(loginData.getClasss().getGrade());
                    }

                    if (loginData.getStudent()!=null) {
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_ID, loginData.getStudent().getId() + "");
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_NAME, loginData.getStudent().getName() + "");
                        SPUtil.putString(RegisterActivity.this, Constants.SP_STUDENT_NUM, loginData.getStudent().getCode() + "");
                        TApplication.getInstant().setStudentId(loginData.getStudent().getId());
                        TApplication.getInstant().setStudentName(loginData.getStudent().getName());
                        TApplication.getInstant().setStudentNum(loginData.getStudent().getCode());
                    }

                    gotoActivity(MainActivity.class,null);
                    RegisterActivity.this.finish();
                }
            }
        });
    }

}
