package com.up.study.ui.errors;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.FragmentAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.model.ErrorSubjectModel;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 错题知识点解析
 */
public class ErrorsActivity extends BaseFragmentActivity {
    //private MyTreeListViewAdapter<MyNodeBean> adapter;

    private ViewPager viewPager;
    //private LayoutInflater mInflater;
    //private List<View> mViews= new ArrayList<View>();

    private List<Fragment> fragmentList = new ArrayList<Fragment>();


    @Override
    protected int getContentViewId() {
        return R.layout.act_errors;
    }

    @Override
    protected void initView() {
        viewPager =  bind(R.id.viewpager);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        getMaJorList();
    }

    @Override
    public void onClick(View v) {

    }

    private void initMagicIndicator() {

        for (int i = 0;i<majorModelList.size();i++){
            ErrorZSDJXFragment fra = new ErrorZSDJXFragment();
            Bundle bundle = new Bundle();
            bundle.putString("majorId",majorModelList.get(i).getCode());
            fra.setArguments(bundle);
            fragmentList.add(fra);
        }

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragmentList));

        //viewPager.setAdapter(new TopicPagerAdapter(mViews));
        viewPager.setOffscreenPageLimit(1);//限制存储在内存的页数

        MagicIndicator magicIndicator =bind(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragmentList == null ? 0 : fragmentList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(Color.BLACK);
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                simplePagerTitleView.setText(majorModelList.get(index).getName());
                simplePagerTitleView.setTextSize(18);

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }


    List<ErrorSubjectModel> majorModelList = new ArrayList<ErrorSubjectModel>();//学科列表

    /**
     * 获取学科
     */
    private void getMaJorList() {
        majorModelList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(null) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<ErrorSubjectModel> list = res.getData();
                    if(list!=null&&list.size()>0){
                        majorModelList.addAll(list);
                        initMagicIndicator();
                    }
                    else{
                        viewPager.setVisibility(View.GONE);
                        bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
