package com.up.study.ui.errors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
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
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 已移除错题库
 */
public class RemoveErrorsActivity extends BaseFragmentActivity {
    private ViewPager viewPager;
//    private LayoutInflater mInflater;
//    private List<View> mViews= new ArrayList<View>();

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    List<ErrorSubjectModel> majorModelList = new ArrayList<ErrorSubjectModel>();//学科列表

    @Override
    protected int getContentViewId() {
        return R.layout.act_remove_errors;
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
            RemoveErrorFragment fra = new RemoveErrorFragment();
            Bundle bundle = new Bundle();
            bundle.putString("majorId",majorModelList.get(i).getCode());
            fra.setArguments(bundle);
            fragmentList.add(fra);
        }

        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragmentList));

        /*mInflater = this.getLayoutInflater();
        for (int i = 0;i<majorModelList.size();i++){
            View view=mInflater.inflate(R.layout.view_ctzsdjx, null);
            initList(view);
            mViews.add(view);
        }
        //final String title[] = {"语文","数学","英语"};
        viewPager.setAdapter(new TopicPagerAdapter(mViews));*/
        //viewPager.setOffscreenPageLimit(majorModelList.size());//限制存储在内存的页数

        MagicIndicator magicIndicator =bind(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);//让tab不会缩在中间部位
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return majorModelList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

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
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);

                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(getResources().getColor(R.color.colorPrimary));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(RemoveErrorsActivity.this, 15);
            }
        });
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    /**
     * 知识点列表
     */
    private void initList(View view){
        ListView treeLv = (ListView)view.findViewById(R.id.tree_lv);
        List<String> dataList = new ArrayList<>();
        dataList.add("真实试卷");
        dataList.add("练习卷");
        dataList.add("练习题");
        dataList.add("错题拍照");
        CommonAdapter<String> adapter = new CommonAdapter<String>(ctx, dataList, R.layout.item_expand_lv_parent) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                // TextView tv11 = vh.getView(R.id.tv_offer_item_11);
                vh.setText(R.id.tv_parent_name,item);
            }
        };
        treeLv.setAdapter(adapter);
        treeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(RemoveErrorsDetailActivity.class,null);
            }
        });
    }

    /**
     * 获取学科
     */
    private void getMaJorList() {
        majorModelList.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(ctx) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<ErrorSubjectModel> list = res.getData();
                    if(list!=null&&list.size()>0){
                        majorModelList.addAll(list);
                        initMagicIndicator();
                    }
                }
            }
        });
    }
}
