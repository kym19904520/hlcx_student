package com.up.common.http;

import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.request.BaseRequest;
import com.up.common.utils.L;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/5/12.
 */

/**
 * ---网络请求成功,不读取缓存 onBefore -> convertSuccess -> onSuccess -> onAfter<br>
 * ---网络请求失败,读取缓存成功 onBefore -> parseError -> onError -> onCacheSuccess -> onAfter<br>
 * ---网络请求失败,读取缓存失败 onBefore -> parseError -> onError -> onCacheError -> onAfter<br>
 */
public abstract class BaseCallback<T> extends AbsCallback<T> {
    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        start(request);
    }

 /*   @Override
    public T convertSuccess(Response response) throws Exception {
        String s = StringConvert.create().convertSuccess(response);
        response.close();
        L.d("服务端返回数据------->>", s);
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        T t = new Gson().fromJson(s, type);
        Respond respond = (Respond) t;
        if (!Respond.SUC.equals(respond.getCode())) {
            throw new Exception(respond.getMsg());
        }
        return new Gson().fromJson(s, type);
    }*/


    @Override
    public void onSuccess(T s, Call call, Response response) {
        success(s, call, response, false);

    }

    @Override
    public void onCacheSuccess(T s, Call call) {
        super.onCacheSuccess(s, call);
        success(s, call, null, true);
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        error(call, response, e, false);
    }

    @Override
    public void onCacheError(Call call, Exception e) {
        super.onCacheError(call, e);
        error(call, null, e, true);
    }

    @Override
    public void onAfter(T s, Exception e) {
        super.onAfter(s, e);
        finish(s, e);
    }

    public abstract void start(BaseRequest request);

    public abstract void success(T s, Call call, Response response, boolean isCache);

    public abstract void error(Call call, Response response, Exception e, boolean isCache);

    public abstract void finish(T s, Exception e);
}
