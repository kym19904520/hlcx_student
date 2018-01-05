package com.up.study.listener;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.up.study.R;

/**
 * Created by dell on 2017/5/31.
 */

public class AnswerOnClickListener implements View.OnClickListener{
    TextView tv_answer_right;
    TextView tv_answer_select;
    LinearLayout ll_anwser;
    LinearLayout ll_bottom;
    ImageView iv_answer_error;
    ImageView iv_answer_right;
    LinearLayout ll_answer_a;
    LinearLayout ll_answer_b;
    LinearLayout ll_answer_c;
    LinearLayout ll_answer_d;

    /**
     *
     * @param tv_answer_right 正确答案
     * @param tv_answer_select 选择的答案
     * @param ll_anwser
     * @param ll_bottom
     * @param iv_answer_error
     * @param iv_answer_right
     * @param ll_answer_a
     * @param ll_answer_b
     * @param ll_answer_c
     * @param ll_answer_d
     */
    public AnswerOnClickListener(TextView tv_answer_right,TextView tv_answer_select,
                                 LinearLayout ll_anwser, LinearLayout ll_bottom,
                                 ImageView iv_answer_error, ImageView iv_answer_right,
                                 LinearLayout ll_answer_a,LinearLayout ll_answer_b,LinearLayout ll_answer_c,LinearLayout ll_answer_d){
        this.tv_answer_right = tv_answer_right;
        this.tv_answer_select = tv_answer_select;
        this.ll_anwser = ll_anwser;
        this.ll_bottom = ll_bottom;
        this.iv_answer_error = iv_answer_error;
        this.iv_answer_right = iv_answer_right;
        this.ll_answer_a = ll_answer_a;
        this.ll_answer_b = ll_answer_b;
        this.ll_answer_c = ll_answer_c;
        this.ll_answer_d = ll_answer_d;
    }

    public AnswerOnClickListener(View view,TextView tv_answer_right,TextView tv_answer_select){
          this.tv_answer_right = tv_answer_right;
          this.tv_answer_select = tv_answer_select;

          ll_anwser = (LinearLayout) view.findViewById(R.id.ll_anwser);
          ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);

          ll_answer_a = (LinearLayout) view.findViewById(R.id.ll_answer_a);
          ll_answer_b = (LinearLayout) view.findViewById(R.id.ll_answer_b);
          ll_answer_c = (LinearLayout) view.findViewById(R.id.ll_answer_c);
          ll_answer_d = (LinearLayout) view.findViewById(R.id.ll_answer_d);

          iv_answer_error = (ImageView) view.findViewById(R.id.iv_answer_error);
          iv_answer_right = (ImageView) view.findViewById(R.id.iv_answer_right);
    }
    @Override
    public void onClick(View v) {
        set();
        if (tv_answer_right!=tv_answer_select){
            tv_answer_select.setBackgroundResource(R.drawable.round_pink_circle_background);
            tv_answer_right.setBackgroundResource(R.drawable.round_blue_circle_background);
            iv_answer_error.setVisibility(View.VISIBLE);
        }
        else{
            tv_answer_select.setBackgroundResource(R.drawable.round_blue_circle_background);
            iv_answer_right.setVisibility(View.VISIBLE);
        }
    }
    private void set(){
        ll_anwser.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.VISIBLE);
        ll_answer_a.setClickable(false);
        ll_answer_b.setClickable(false);
        ll_answer_c.setClickable(false);
        ll_answer_d.setClickable(false);
    }
}
