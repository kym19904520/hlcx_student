package com.up.common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.up.common.conf.Constants;
import com.up.common.utils.Logger;
import com.up.common.widget.LoadingDialog;

import java.util.Map;

import butterknife.ButterKnife;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/2/24.
 */

public abstract class AbsFragment extends Fragment implements View.OnClickListener {
    protected String tag = this.getClass().getSimpleName();
    protected View root;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected LoadingDialog loadingDialog;
    protected int lanType = 0;
    protected Context ctx;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.container = container;
        this.inflater = inflater;
        ctx = getActivity();
        initLoadingDialog();
        root = inflater.inflate(getContentViewId(), container, false);
        initView();
        initEvent();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    protected void initLoadingDialog() {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(getActivity());
//            loadingDialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showLoading(String text) {
        loadingDialog.setText(text);
        loadingDialog.show();
    }


    public void hideLoading() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    protected abstract int getContentViewId();

    /**
     * 载入布局
     */
    protected abstract void initView();

    /**
     * 载入数据初始化数据(每次回到该页面都会重新运行该方法)
     */
    protected abstract void initData() ;

    /**
     * 初始化事件
     */
    protected abstract void initEvent();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 懒加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于Fragment的onResume
        } else {
            // 相当于Fragment的onPause

        }
    }

    @Override
    public abstract void onClick(View v);

    /**
     * 绑定ID
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T bind(int viewId) {
        View view = root.findViewById(viewId);
        return (T) view;
    }

    /**
     * 跳转到Activity
     *
     * @param cls 目标Activity
     * @param map 传递的参数，可为null
     */
    public void gotoActivity(Class<?> cls, Map<String, Object> map) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (null != map) {
            SerializableMap serializableMap = new SerializableMap();
            serializableMap.setMap(map);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Map", serializableMap);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    /**
     * 跳转到Activity
     *
     * @param cls 目标Activity
     */
    public void gotoActivity(Class<?> cls,int value) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        intent.putExtra(Constants.KEY,value);
        startActivity(intent);
    }
    /**
     * 跳转到Activity
     *
     * @param cls 目标Activity
     */
    public void gotoActivityWithBean(Class<?> cls, BaseBean bean1, BaseBean bean2) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bean1!=null) {
            intent.putExtra("bean1", bean1);
        }
        if(bean2!=null) {
            intent.putExtra("bean2", bean2);
        }
        startActivity(intent);
    }
    /**
     * 跳转到Activity
     * 只需要一个参数
     * @param cls 目标Activity
     * @param value 传递的参数，可为null
     */
    public void gotoActivity(Class<?> cls,String key, String value) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (null != key && null!=value) {
            intent.putExtra(key,value);
        }
        startActivity(intent);
    }

    public void showToast(String str){
        Toast.makeText(ctx,str, Toast.LENGTH_SHORT).show();
    }
    public void showLog(String str){
        Logger.i(Logger.TAG,str);
    }
    public void showLoge(String str){
        Logger.e(Logger.TAG,str);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
