package com.up.study.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.up.common.base.AbsFragment;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/8.
 */

public abstract class BaseFragment extends AbsFragment {
    protected BaseFragmentActivity mParentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        mParentActivity=(BaseFragmentActivity)context;
    }
}
