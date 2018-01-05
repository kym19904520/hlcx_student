package com.up.study.weight;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 可设置为不滚动的viewPager
 * @author yy_cai
 *
 * 2015年8月3日
 */
public class MyViewPager extends ViewPager {

	private boolean noScroll = false;
    
    public MyViewPager(Context context) {
            super(context);
    }
    
    public MyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
    }

    /**
     * 设置是否滚动
     * @param noScroll true：不滚动，false：滚动
     */
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }
 

}
