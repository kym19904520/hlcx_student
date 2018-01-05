package com.up.study.ui.home;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.up.study.R;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.weight.ScaleCircleNavigator;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity1 extends BaseFragmentActivity {
    private TextView tv_error_input,tv_error_input2;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private LayoutInflater mInflater;
    private List<View> mViews= new ArrayList<View>();
    @Override
    protected int getContentViewId() {
        return R.layout.act_search_result;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        tv_error_input = bind(R.id.tv_error_input);
        tv_error_input2 = bind(R.id.tv_error_input2);
    }

    @Override
    protected void initEvent() {
        tv_error_input.setOnClickListener(this);
        tv_error_input2.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mInflater = getLayoutInflater();
        for (int i = 0;i<5;i++){
            View view=null;
            if (i==1||i==4){
                view=mInflater.inflate(R.layout.view_topic_zgt, null);
            }
            else{
                view=mInflater.inflate(R.layout.view_topic_kgt, null);
            }

            mViews.add(view);
        }

        viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数

       /* CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(mViews.size());
        circleNavigator.setCircleColor(Color.GRAY);
        circleNavigator.setRadius(13);*/
        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(this);
        scaleCircleNavigator.setCircleCount(mViews.size());
        scaleCircleNavigator.setNormalCircleColor(Color.LTGRAY);
        scaleCircleNavigator.setSelectedCircleColor(Color.DKGRAY);
        scaleCircleNavigator.setMinRadius(10);
        scaleCircleNavigator.setMaxRadius(12);
        magicIndicator.setNavigator(scaleCircleNavigator);

        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    public void onClick(View v) {
        if(v==tv_error_input){
            gotoActivity(ErrorsInputActivity.class,null);
        }
        else if(v==tv_error_input2){
            gotoActivity(ErrorsInputActivity2.class,null);
        }
    }
}
