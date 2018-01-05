package com.up.study.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.ImageUtil;
import com.up.study.R;
import com.up.study.adapter.MesAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.bean.HomePageBean;
import com.up.study.model.MesModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 消息列表
 */
public class MessageActivity extends BaseFragmentActivity {

    private ListView dynamicList;
    private MesAdapter adapter;
    private List<MesModel> listDatas = new ArrayList<MesModel>();
    private LinearLayout ll_no_error;
    private TextView tv_error_text;

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_home;
    }

    @Override
    protected void initView() {
        dynamicList = (ListView)this.findViewById(R.id.lv_home_dyn);
        ll_no_error = bind(R.id.ll_no_error);
        tv_error_text = bind(R.id.tv_error_text);
        /*List<HomePageBean> listDatas=new ArrayList<HomePageBean>();
        listDatas = getHomePageContent(this);
        dynamicList.setAdapter(new MyHomeAdapter(listDatas, this));*/
        adapter = new MesAdapter(listDatas, this);
        dynamicList.setAdapter(adapter);
        dynamicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                gotoActivityWithBean(MessageDetailActivity.class,listDatas.get(position),null);
            }
        });
    }

    @Override
    protected void initEvent() {}

    @Override
    protected void initData() {
        getMesList();
    }

    @Override
    public void onClick(View v) {}

    //获取消息列表
    private void getMesList() {
        listDatas.clear();
        HttpParams params = new HttpParams();
        params.put("page", 1);
        J.http().post(Urls.GET_MES_LIST, ctx, params, new HttpCallback< Respond<List<MesModel>>>(ctx) {
            @Override
            public void success(Respond<List<MesModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<MesModel> list = res.getData();
                    if (list.size() <=0){
                        ll_no_error.setVisibility(View.VISIBLE);
                        tv_error_text.setText(R.string.no_message);
                        dynamicList.setVisibility(View.GONE);
                    }else {
                        dynamicList.setVisibility(View.VISIBLE);
                    }
                    listDatas.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    /**
     * 获取主页的动态内容
     * @param mActivity
     * @return
     */
    public static List<HomePageBean> getHomePageContent(Context mActivity){
        List<Bitmap> imgs0 = new ArrayList<Bitmap>();
        imgs0.add(ImageUtil.readBitMap(mActivity, R.mipmap.test_content_img_2));
        List<Bitmap> imgs1 = new ArrayList<Bitmap>();
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs1.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        List<Bitmap> imgs = new ArrayList<Bitmap>();
        imgs.add(ImageUtil.readBitMap(mActivity, R.mipmap.test_content_img_2));
        imgs.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        imgs.add(ImageUtil.readBitMap(mActivity, R.mipmap.tx8));
        List<HomePageBean> dynamic = new ArrayList<HomePageBean>();
        HomePageBean bean = new HomePageBean();
        bean.setContent("从今天起，我要好好学习天天向上");
        bean.setImgs(imgs1);
        bean.setTime("今天");
        dynamic.add(bean);
        bean = new HomePageBean();
        bean.setContent("我最想去的几个地方！！");

        bean.setImgs(imgs);
        bean.setTime("星期五");
        dynamic.add(bean);
        bean = new HomePageBean();
        bean.setContent("世界那么大，我想去看看");

        bean.setImgs(imgs0);
        bean.setTime("8月30日");
        dynamic.add(bean);
        bean = new HomePageBean();
        bean.setContent("转发：我喜欢的，你不一定喜欢");

        bean.setImgs(imgs);
        bean.setTime("8月28日");
        dynamic.add(bean);
        bean = new HomePageBean();
        bean.setContent("西藏，一个神圣的地域，在我读完《藏地密码》后，我对他的崇拜已经不可言喻，我期待哪一天，我能踏上它，面对布达拉宫，说一声：西藏，我来了!");

        bean.setImgs(imgs0);
        bean.setTime("8月25日");
        dynamic.add(bean);
        bean = new HomePageBean();
        bean.setContent("云岚来了，好开心！！");

        bean.setImgs(imgs);
        bean.setTime("8月21日");
        dynamic.add(bean);
        return dynamic;
    }
}
