package com.up.common.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.up.common.conf.Constants;
import com.up.common.utils.AppUtils;
import com.up.common.utils.Logger;
import com.up.common.widget.LoadingDialog;

import java.util.Map;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/2/22.
 */

public abstract class AbsFragmentActivity extends FragmentActivity implements View.OnClickListener {

    protected String TAG = this.getClass().getSimpleName();
    protected Context ctx;

    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横屏
        ctx = this;
        setContentView(getContentViewId());
        initLoadingDialog();
        initView();
        initData();
        initEvent();
        if (!AppUtils.isNetworkAvailable(this)){
            showToast("当前网络不可用！");
        }
    }

    protected abstract int getContentViewId();

    /**
     * 载入布局
     */
    protected abstract void initView();

    /**
     * 载入数据初始化数据
     */
    protected abstract void initData();

    /**
     * 事件
     */
    protected abstract void initEvent();


    protected void initLoadingDialog() {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(ctx);
//            loadingDialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showLoading(String text) {
        loadingDialog.setText(text);
        loadingDialog.show();
    }
    public void showLoading(String text,boolean isCanCancel) {
        loadingDialog.setText(text);
        loadingDialog.setCancelable(isCanCancel);
        loadingDialog.show();
    }

    public void hideLoading() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 绑定ID
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T bind(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }

    @Override
    public abstract void onClick(View v);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到Activity
     *
     * @param cls 目标Activity
     * @param map 传递的参数，可为null
     */
    public void gotoActivity(Class<?> cls, @Nullable Map<String, Object> map) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
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
        intent.setClass(this, cls);
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
        intent.setClass(this, cls);
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
        intent.setClass(this, cls);
        if (null != key && null!=value) {
            intent.putExtra(key,value);
        }
        startActivity(intent);
    }

    public Map<String, Object> getMap() {
        if (getIntent().hasExtra("Map")) {
            SerializableMap map = (SerializableMap) getIntent().getSerializableExtra("Map");
            return map.getMap();
        } else {
            return null;
        }
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    public void showLog(String str){
        Logger.i(Logger.TAG,str);
    }
    public void showLoge(String str){
        Logger.e(Logger.TAG,str);
    }

    /**
     * 隐藏布局
     * @param views
     */
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示布局
     * @param views
     */
    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
