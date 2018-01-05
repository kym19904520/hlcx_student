package com.up.study.ui.my;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.ui.login.LoginActivity;
import com.up.study.ui.login.UpdatePswActivity;
import com.up.study.weight.dialog.CommonDialog;

public class SettingActivity extends BaseFragmentActivity {
    private RelativeLayout rl_update_psw,rl_about;
    private Button btn_loginout;
    @Override
    protected int getContentViewId() {
        return R.layout.act_setting;
    }

    @Override
    protected void initView() {
        rl_about = bind(R.id.rl_about);
        rl_update_psw = bind(R.id.rl_update_psw);
        btn_loginout = bind(R.id.btn_loginout);
    }

    @Override
    protected void initEvent() {
        rl_update_psw.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        btn_loginout.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        if(v==rl_about){
            gotoActivity(AboutActivity.class,null);
        }
        else if(v==rl_update_psw){
            gotoActivity(UpdatePswActivity.class,null);
        }
        else if (v==btn_loginout) {
            new CommonDialog.Builder(this)
                    .setTitle(R.string.dialog_hint)
                    .setMessage(R.string.dialog_affirm_quit)
                    .setPositiveButton(R.string.dialog_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SPUtil.clear(SettingActivity.this);
                            closeAllActivty();
                            gotoActivity(LoginActivity.class, null);
                        }
                    }, R.color.colorPrimaryDark)
                    .setNegativeButton(R.string.dialog_off, null, R.color.colorPrimaryDark).show();
        }
    }
}
