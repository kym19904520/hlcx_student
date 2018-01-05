package com.up.study.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorSubjectModel;
import com.up.study.ui.errors.ErrorSubjectContentFragment;
import com.up.study.ui.errors.ErrorsActivity;
import com.up.study.ui.errors.ErrorsClearActivity;
import com.up.study.ui.errors.RemoveErrorsActivity;
import com.up.study.ui.errors.SmartOrganizationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017/4/20.
 */

public class ErrorsFragment extends BaseFragment {
    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private List<View> mViews = new ArrayList<View>();
    private TabLayout tabLayout;

    private TextView tv_top_tab_1, tv_top_tab_2, tv_top_tab_3, tv_right;
    private ExpandableListView mainlistview = null;
    private Map<String, List<HashMap<String, Object>>> child_map = null;
    private List<HashMap<String, Object>> parent_list = null;

    private Button btn_import;
    private LinearLayout ll_no_error;
    private ErrorSubjectModel curMajor;//当前科目


    FrameLayout content;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && TApplication.getInstant().isRefreshError()) {//可见
            showLog("---------刷新错题库------------可见---");
            initTabs();
            TApplication.getInstant().setRefreshError(false);
        } else {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fra_errors;
    }

    @Override
    protected void initView() {
        viewPager = bind(R.id.viewpager);
        tv_top_tab_1 = bind(R.id.tv_top_tab_1);
        tv_top_tab_2 = bind(R.id.tv_top_tab_2);
        tv_top_tab_3 = bind(R.id.tv_top_tab_3);
        tv_right = bind(R.id.tv_right);
        //已删除错题（隐藏暂时不用）
        tv_right.setVisibility(View.GONE);

        btn_import = bind(R.id.btn_import);
        tabLayout = bind(R.id.tabs);

        ll_no_error = bind(R.id.ll_no_error);
        content = bind(R.id.content);

        showLog("=========initView=============");
        initTabs();
    }

    @Override
    protected void initEvent() {
        tv_top_tab_1.setOnClickListener(this);
        tv_top_tab_2.setOnClickListener(this);
        tv_top_tab_3.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        btn_import.setOnClickListener(this);
        registerForContextMenu(btn_import);//btn是要点击的控件
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v == tv_top_tab_1) {
            if (curMajor == null) {
                showToast("无学科");
            } else {
                Intent intent = new Intent(getContext(),ErrorsClearActivity.class);
                intent.putExtra("majorId",curMajor.getCode());
                intent.putExtra("type","1");
                startActivity(intent);
            }
        } else if (v == tv_top_tab_2) {
            gotoActivity(ErrorsActivity.class, null);
        } else if (v == tv_top_tab_3) {
            gotoActivity(SmartOrganizationActivity.class, null);
        } else if (v == tv_right) {
            gotoActivity(RemoveErrorsActivity.class, null);
        } else if (v == btn_import) {//导出
            if (curMajor == null) {
                showToast("无学科");
                return;
            }
            Integer majorNum = TApplication.getInstant().getErrors().get(curMajor.getCode());
            if (majorNum == null || majorNum == 0) {
                showToast("本学科无错题可导出");
                return;
            }

            v.showContextMenu();//单击直接显示Context菜单

           /* new AlertDialog.Builder(this.getActivity())
                    .setTitle("确认导出"+curMajor.getName()+"学科的错题详情图片？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            importImg();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();*/

            //showToast("文件格式不正确");
        }
    }

    //导出图片
    private void importImg() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("major_id", curMajor.getCode());
        params.put("rows", 200);
        OkGo.get(Urls.EXPORT_PIC)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback(curMajor.getName() + "_" + new Date().getTime() + "_qsx.png") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
                        showToast("下载完成，图片已经保存在SD卡根目录的Download文件下");
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showToast("请求超时");
                    }
                });
    }

    private void initTabs() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        J.http().post(Urls.ERROR_SUBJECT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectModel>>>(ctx, true) {
            @Override
            public void success(Respond<List<ErrorSubjectModel>> res, Call call, Response response, boolean isCache) {
                list = res.getData();
                initContent();
                if (list != null && list.size() > 0) {
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content, ErrorSubjectContentFragment.newInstance(list.get(0).getCode()))
                                .commitAllowingStateLoss();
                        curMajor = list.get(0);
                    }
                } else {
                    ll_no_error.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                }

            }
        });
    }

    List<ErrorSubjectModel> list = new ArrayList<>();

    private void initContent() {

//        for (int i = 0; i < list.size(); i++) {
//            tabLayout.addTab(tabLayout.newTab().setText(list.get(i).getName()), i);
//        }
        if (list.size() > 6) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i = 0; i < list.size(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);//获得每一个tab
//            tab.setCustomView(R.layout.item_tab);//给每一个tab设置view
            tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.item_tab));
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(true);//第一个tab被选中
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.VISIBLE);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_title);
            textView.setText(list.get(i).getName());//设置tab上的文字
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(true);
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, ErrorSubjectContentFragment.newInstance(list.get(tab.getPosition()).getCode()))
                        .commitAllowingStateLoss();
                curMajor = list.get(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tv_title).setSelected(false);
                tab.getCustomView().findViewById(R.id.v).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.load, menu);
        //menu.setHeaderTitle("选择导出方式");
        /*menu.add(0, 3, 0, "修改");
        menu.add(0, 4, 1, "删除");*/
        //给menu设置布局文件，当触发时显示在界面上
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //布局文件里面对应的id，当点击时，根据id区别那个被点击
        switch (item.getItemId()) {
            case R.id.text1:
                showLog("=========importImg=================");
                importImg();
                break;
            case R.id.text2:
                showLog("=========loadDoc=================");
                importDoc();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //导出word
    private void importDoc() {
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("major_id", curMajor.getCode());
        params.put("rows", 200);
        OkGo.get(Urls.EXPORT_DOC)//
                .tag(this)//
                .params(params)
                .execute(new FileCallback(curMajor.getName() + "_" + new Date().getTime() + "_qsx.doc") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        showLog(file.getName());
//                        showToast("下载完成，图片已经保存在sdcard/download文件下");
                        Toast.makeText(getActivity(), "下载完成，word已经保存在SD卡根目录的Download文件下", Toast.LENGTH_LONG).show();
                        hideLoading();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        initLoadingDialog();
                        showLoading("下载中...");
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                    }
                });
    }


}
