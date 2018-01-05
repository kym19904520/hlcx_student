package com.up.common.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.up.study.R;


/**
 * Todo
 * Created by 王剑洪
 * on 2016/7/13.
 */
public class LoadingDialog extends Dialog {

    private TextView tv;
    private String text="";
    private ImageView iv;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_loading);
        tv = (TextView)findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        tv.setText(TextUtils.isEmpty(getText())? "加载中...":text);
        Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.loading);
        animator.setTarget(iv);
        animator.start();
        LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.ll);
        linearLayout.getBackground().setAlpha(210);
        setCancelable(false);
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setCancelable(true);
        dismiss();
    }
}
