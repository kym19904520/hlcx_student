package com.up.study.ui.login;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.AbsFragmentActivity;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.WidgetUtils;
import com.up.study.R;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.weight.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UpdatePswActivity extends AbsFragmentActivity {
    private int mStep=0;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews= new ArrayList<View>();

    private ImageView iv_back,iv_eye,iv_eye1;
    private TextView tv_new_psw;
    private EditText et_psw,et_psw1;
    @Override
    protected int getContentViewId() {
        return R.layout.act_update_psw;
    }

    @Override
    protected void initView() {
        viewPager =  bind(R.id.viewpager);
        tv_new_psw =  bind(R.id.tv_input_new_psw);
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
        View view1=mInflater.inflate(R.layout.view_update_psw_1, null);
        initUpdatePsw1(view1);
        mViews.add(view1);
        View view2=mInflater.inflate(R.layout.view_update_psw_2, null);
        initUpdatePsw2(view2);
        mViews.add(view2);
        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.setNoScroll(true);
    }
    private void initUpdatePsw1(View view1) {
        view1.findViewById(R.id.btn_next).setOnClickListener(this);
        iv_eye = (ImageView) view1.findViewById(R.id.iv_eye);
        iv_eye.setOnClickListener(this);
        et_psw =(EditText) view1.findViewById(R.id.et_psw);
    }
    private void initUpdatePsw2(View view2) {
        Button btn_stu = (Button)view2.findViewById(R.id.btn_finish);
        btn_stu.setOnClickListener(this);
        iv_eye1 = (ImageView)view2.findViewById(R.id.iv_eye1);
        iv_eye1.setOnClickListener(this);
        et_psw1 =(EditText) view2.findViewById(R.id.et_psw1);
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_next){
            String psw = et_psw.getText().toString();
            if (psw.length()<6||psw.length()>16){
                showToast("密码只能为6-16位");
            }
            else{
                mStep = 1;
                viewPager.setCurrentItem(1);
                tv_new_psw.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        }
        else if (v.getId()==R.id.btn_finish){
            String psw = et_psw1.getText().toString();
            if (psw.length()<6||psw.length()>16){
                showToast("密码只能为6-16位");
            }
            else{
                updatePsw();
            }
        }

        else if(v.getId()==R.id.iv_back){
            switch (mStep){
                case 0:
                    this.finish();
                    break;
                case 1:
                    viewPager.setCurrentItem(0);
                    tv_new_psw.setTextColor(getResources().getColor(R.color.text_black));
                    break;
                default:
                    break;
            }
            mStep--;
        }
        else if(v.getId()==R.id.iv_eye){
            WidgetUtils.eye(iv_eye,et_psw);
        }
        else if(v.getId()==R.id.iv_eye1){
            WidgetUtils.eye(iv_eye1,et_psw1);
        }
    }

    /**
     *  修改密码
     */
    private void updatePsw() {
        HttpParams params = new HttpParams();
        params.put("oldpass", et_psw.getText().toString());
        params.put("newpass", et_psw1.getText().toString());
        J.http().post(Urls.UPDATE_PSW, ctx, params, new HttpCallback< Respond<String>>(ctx) {
            @Override
            public void success(Respond<String> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    showToast("密码修改成功");
                    UpdatePswActivity.this.finish();
                }
            }
        });
    }
}
