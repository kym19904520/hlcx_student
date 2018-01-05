package com.up.study.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/5/25.
 * 题目
 */

public class TopicPagerAdapter extends PagerAdapter {
    private List<View> mViews;
    public TopicPagerAdapter(List<View> views){
        mViews = views;
    }
    public void Notify(List<View> views){
        mViews = views;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mViews.size();
    }
    @Override
    public Object instantiateItem(View collection, int position) {
        /*ViewGroup parent = (ViewGroup) collection.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }*/
        ((ViewPager) collection).addView(mViews.get(position),0);

        return mViews.get(position);
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView(mViews.get(position));
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(object);
    }

    @Override
    public void finishUpdate(View arg0) {}

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {}

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {}
}
