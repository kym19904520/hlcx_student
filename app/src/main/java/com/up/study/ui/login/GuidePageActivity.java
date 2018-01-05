package com.up.study.ui.login;

import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.up.common.utils.AppUtils;
import com.up.common.utils.StringUtils;
import com.up.study.MainActivity;
import com.up.study.R;
import com.up.study.adapter.GuidePageAdapter;
import com.up.study.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by think on 2017/5/4.
 */

public class GuidePageActivity extends BaseFragmentActivity implements ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private int[] imageArrays;
    private List<View> viewList;
    private ViewGroup vg;

    private ImageView iv_point;
    private ImageView[] ivPointArray;

    @Bind(R.id.ll_guide_start)
    LinearLayout ll_guide_start;
    @Bind(R.id.tv_version)
    TextView tv_version;
    @Bind(R.id.guide_rl_start)
    RelativeLayout guide_rl_start;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        final SharedPreferences sp = getSharedPreferences("First", MODE_PRIVATE);

        boolean isFirst = sp.getBoolean("isFirst", true);
        if (!isFirst) {
            gotoActivity(MainActivity.class, null);
            finish();
        } else {
            initViewPager();
            initPoint();
        }

        guide_rl_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isFirst", false);
                editor.commit();
                gotoActivity(MainActivity.class, null);
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        tv_version.setText(AppUtils.packageName(getApplicationContext()));
    }

    @Override
    protected void initEvent() {
    }

    @Override
    public void onClick(View v) {
    }

    public void initViewPager() {
        vp = (ViewPager) findViewById(R.id.guide_vp);
        imageArrays = new int[]{R.mipmap.guide_01, R.mipmap.guide_02, R.mipmap.guide_03
        };
        viewList = new ArrayList<>();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        int len = imageArrays.length;
        for (int i = 0; i < len; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(imageArrays[i]);

            viewList.add(imageView);
        }

        vp.setAdapter(new GuidePageAdapter(viewList));

        vp.setOnPageChangeListener(this);
    }

    public void initPoint() {
        vg = (ViewGroup) findViewById(R.id.guide_ll_point);
        ivPointArray = new ImageView[viewList.size()];
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            iv_point = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(StringUtils.px2Dip(GuidePageActivity.this, 20), StringUtils.px2Dip(GuidePageActivity.this, 20));
            params.setMargins(5, 0, 5, 0);
            iv_point.setLayoutParams(params);
            iv_point.setPadding(30, 0, 30, 0);

            ivPointArray[i] = iv_point;
            if (i == 0) {
                iv_point.setBackgroundResource(R.drawable.full_holo);
            } else {
                iv_point.setBackgroundResource(R.drawable.empty_holo);
            }
            vg.addView(ivPointArray[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int length = imageArrays.length;
        for (int i = 0; i < length; i++) {
            ivPointArray[position].setBackgroundResource(R.drawable.full_holo);
            if (position != i) {
                ivPointArray[i].setBackgroundResource(R.drawable.empty_holo);
            }
        }

        //判断是否是最后一页，若是则显示按钮
        if (position == imageArrays.length - 1) {
            ll_guide_start.setVisibility(View.VISIBLE);
        } else {
            ll_guide_start.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
