package com.up.study.ui.home;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.common.utils.StringUtils;
import com.up.common.utils.StudyUtils;
import com.up.study.R;
import com.up.study.adapter.TopicPagerAdapter;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.callback.CallBack;
import com.up.study.model.ImgUrl;
import com.up.study.model.SeqModel;
import com.up.study.weight.ScaleCircleNavigator;
import com.up.study.weight.camera.TakePhotoActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 拍照搜题结果
 */
public class SearchResultActivity extends BaseFragmentActivity {
    private TextView tv_error_input, tv_error_input2,tv_error_input3, tv_camera;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();

    private List<SeqModel> mSeqList = new ArrayList<SeqModel>();

    private SeqModel curSeqModel;//当前题目
    private String searchContent = "";

    @Override
    protected int getContentViewId() {
        return R.layout.act_search_result;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        tv_error_input = bind(R.id.tv_error_input);
        tv_error_input2 = bind(R.id.tv_error_input2);
        tv_error_input3 = bind(R.id.tv_error_input3);
        tv_camera = bind(R.id.tv_camera);
    }

    @Override
    protected void initEvent() {
        tv_error_input.setOnClickListener(this);
        tv_error_input2.setOnClickListener(this);
        tv_error_input3.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String searchText = getIntent().getStringExtra("search");
        String path = getIntent().getStringExtra("imgPath");
        if (!TextUtils.isEmpty(searchText)) {
            showLog("查询关键词：" + searchText);
            beginSearch(searchText);
            searchContent = searchText;
            tv_camera.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(path)) {
            uploadImg(path);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_error_input) {//对题目错题录入
            ImgUrl imgUrl = new ImgUrl();
            String path = getIntent().getStringExtra("imgPath");
            if (!TextUtils.isEmpty(path)) {
                imgUrl.setUrl(path);
            }
            gotoActivityWithBean(ErrorsInputActivity.class, curSeqModel, imgUrl);
        } else if (v == tv_error_input2||v==tv_error_input3) {//找不到题目进行错题录入
            List<String> uploadImgs = new ArrayList<>();
            String path = getIntent().getStringExtra("imgPath");
            String searchText = getIntent().getStringExtra("search");
            if (!TextUtils.isEmpty(path)) {
                uploadImgs.add(path);
                StudyUtils.uploadImgUrl(uploadImgs, this, new CallBack<List<ImgUrl>>() {
                    @Override
                    public void suc(List<ImgUrl> obj) {
                        if (obj.size() > 0) {
                            searchContent = obj.get(0).getUrl();
                            searchContent = "<img src=\"" + searchContent + "\"/>";
                            gotoActivity(ErrorsInputActivity2.class, "searchContent", searchContent);
                        } else {
                            showToast("图片未上传到云");
                        }

                    }
                });
            } else if (!TextUtils.isEmpty(searchText)) {
                searchContent = searchText;
                gotoActivity(ErrorsInputActivity2.class, "searchContent", searchContent);
            }
        } else if (v == tv_camera) {
            gotoActivity(TakePhotoActivity.class, null);
            this.finish();
        }
    }

    /**
     * 图片搜索查询题目
     *
     * @param filePath
     */
    private void uploadImg(String filePath) {
        showLoading("正在寻找相近的题目，请稍后...", true);
        OkGo.post(Urls.IMG_SEARCH_SEQ)//
                .tag(this)//
                // .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("file", new File(filePath))   // 可以添加文件上传
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Logger.i(Logger.TAG, s);
                        Type type = new TypeToken<Respond<List<SeqModel>>>() {
                        }.getType();
                        Respond<List<SeqModel>> res = new Gson().fromJson(s, type);
                        mSeqList = res.getData();
                        if (mSeqList == null || mSeqList.size() == 0) {
                            showToast("未查询到任何相关题目，请重新查询");
                            SearchResultActivity.this.finish();
                            return;
                        }
                        curSeqModel = mSeqList.get(0);
                        initSeqCardData();
                        SearchResultActivity.this.hideLoading();
                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        Logger.i(Logger.TAG, currentSize + "===========================");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!TextUtils.isEmpty(e.getMessage())) {
                            if (e.getMessage().contains("after 15000ms")) {
                                showToast("请求超时，请重试");
                            }
                        } else {
                            showToast("请求超时，请重试");
                            tv_error_input.setClickable(false);
                            hideLoading();
                            return;
                        }
                    }
                });
    }


    /**
     * 文字搜索查询题目
     */
    private void beginSearch(String search) {
        HttpParams params = new HttpParams();
        params.put("selecttext", search);
        J.http().post(Urls.SEARCH_SEQ, ctx, params, new HttpCallback<Respond<List<SeqModel>>>(ctx) {
            @Override
            public void success(Respond<List<SeqModel>> res, Call call, Response response, boolean isCache) {
                if (Respond.SUC.equals(res.getCode())) {
                    mSeqList = res.getData();
                    if (mSeqList == null || mSeqList.size() == 0) {
                        showToast("未查询到任何相关题目，请重新查询");
                        SearchResultActivity.this.finish();
                        return;
                    }
                    curSeqModel = mSeqList.get(0);
                    initSeqCardData();
                }
            }
        });
    }

    private void initSeqCardData() {
        mInflater = getLayoutInflater();
        for (int i = 0; i < mSeqList.size(); i++) {
            View view = mInflater.inflate(R.layout.view_topic_kgt_list_1, null);
            StudyUtils.initSeqView(view, mSeqList.get(i), this);
            mViews.add(view);
        }

        TopicPagerAdapter adapter = new TopicPagerAdapter(mViews);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(mViews.size());//限制存储在内存的页数
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                curSeqModel = mSeqList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adapter.notifyDataSetChanged();

        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(this);
        scaleCircleNavigator.setCircleCount(mViews.size());
        scaleCircleNavigator.setNormalCircleColor(Color.LTGRAY);
        scaleCircleNavigator.setSelectedCircleColor(Color.DKGRAY);
        scaleCircleNavigator.setMinRadius(StringUtils.dip2Px(this,5));
        scaleCircleNavigator.setMaxRadius(StringUtils.dip2Px(this,6));
        magicIndicator.setNavigator(scaleCircleNavigator);

        ViewPagerHelper.bind(magicIndicator, viewPager);
    }
}
