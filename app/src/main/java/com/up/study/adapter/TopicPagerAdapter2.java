package com.up.study.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.up.study.ui.errors.AnswerFragment;

/**
 * Created by dell on 2017/5/25.
 * 题目
 */

public class TopicPagerAdapter2 extends FragmentPagerAdapter {

    int total;
    public TopicPagerAdapter2(FragmentManager fm,int total) {
        super(fm);
        this.total=total;
    }

    @Override
    public Fragment getItem(int position) {
        return new AnswerFragment();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return total;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
