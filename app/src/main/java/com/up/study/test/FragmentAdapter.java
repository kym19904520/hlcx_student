package com.up.study.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.up.study.ui.ErrorsFragment;
import com.up.study.ui.HomeFragment;
import com.up.study.ui.MyFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
//Fragment是用来展示信息的，相当于Activity来展示一个页面
    HomeFragment oneF = new HomeFragment();
    ErrorsFragment towF = new ErrorsFragment();
    MyFragment threeF = new MyFragment();
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            return oneF;
        case 1:
            return towF;
         case 2:
            return threeF;
        default:
            break;
        }
        return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
    
}  