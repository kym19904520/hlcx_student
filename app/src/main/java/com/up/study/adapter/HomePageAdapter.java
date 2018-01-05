package com.up.study.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.up.common.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/6/30.
 */

public class HomePageAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter

    //  private FragmentManager fm;
    //private ArrayList<Fragment> fragments = null;
   // private List<HotIssues> hotIssuesList;
    //private Context context;
    List<Fragment> fragments;
    private ArrayList<Fragment> mList= new ArrayList<>();
    //int total;
    public HomePageAdapter( FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        //this.context = context;
        //this.total=total;
//        this.fragments = fragments;
        mList.addAll(fragments);
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        /*AnswerFragment errorDdetailFragment = AnswerFragment.newInstance(position+1);
        if (fragments.size()<total){
            fragments.add(errorDdetailFragment);
        }
        Logger.i(Logger.TAG,"fragments.add(errorDdetailFragment),positon="+position+",fragments.size="+fragments.size());*/
        return mList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        //return total;//hotIssuesList.size();
        return mList.size();
    }

    public void clearItem(int position){
        Logger.i(Logger.TAG,"clearItem position="+position);
//        List<Fragment> fras = new ArrayList<>();
//        for(int i  =0;i<fragments.size();i++){
//            if(i!=position){
//                fras.add(fragments.get(i));
//            }
//        }
        mList.remove(position);
        /*fragments.clear();
        fragments.addAll(fras);*/
        notifyDataSetChanged();
    }

}
