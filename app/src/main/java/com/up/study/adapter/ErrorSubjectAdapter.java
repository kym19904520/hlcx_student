package com.up.study.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.up.study.R;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.ui.errors.ErrorSubjectContentFragment;

import java.util.List;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/6/26.
 */

public class ErrorSubjectAdapter extends FragmentPagerAdapter {
    //    private List<ErrorSubjectModel> list;
//
//    public ErrorSubjectAdapter(FragmentManager fm, List<ErrorSubjectModel> list) {
//        super(fm);
//        this.list = list;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return ErrorSubjectContentFragment.newInstance(list.get(position).getCode());
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return list.get(position).getName();
//    }
    private List<BaseFragment> mFragments;
    private List<ErrorSubjectModel> list;
    private Context context;

    public ErrorSubjectAdapter(Context context, FragmentManager fm, List<BaseFragment> fragments, List<ErrorSubjectModel> list) {
        super(fm);
        mFragments = fragments;
        this.list = list;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public void initTab(TabLayout tl, final ViewPager vp) {
        if (list.size() > 6) {
            tl.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tl.setTabMode(TabLayout.MODE_FIXED);
        }
        if (vp != null) {
            //给ViewPager设置适配器
            vp.setAdapter(this);
            //将TabLayout和ViewPager关联起来。
            tl.setupWithViewPager(vp);
        }


        for (int i = 0; i < list.size(); i++) {
            TabLayout.Tab tab = tl.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.item_tab);//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(true);//第一个tab被选中
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.VISIBLE);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_title);
            textView.setText(list.get(i).getName());//设置tab上的文字
        }
        tl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(true);
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.VISIBLE);
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(false);
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
