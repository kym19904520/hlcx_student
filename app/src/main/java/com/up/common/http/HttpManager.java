package com.up.common.http;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.up.common.utils.L;

import java.util.logging.Level;


/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/16.
 */

public class HttpManager {

    private static HttpManager instance;

    private static Application context;

    public static void init(Application application) {
        context = application;
    }

    public static synchronized HttpManager getInstance() {
        if (null == instance) {
            instance = new HttpManager();
        }
        return instance;
    }

    private HttpManager() {
        try {
            OkGo.init(context);
            initOkGo();
        } catch (Exception e) {
            L.e("HttpManager", e.getMessage());
        }

    }

    private void initOkGo() {

        OkGo.getInstance()
                // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                .debug("OkGo", Level.INFO, true)
                //cookie持久化存储，如果cookie不过期，则一直有效
                .setCookieStore(new PersistentCookieStore())
                //如果使用默认的 60秒,以下三行也不需要传
                .setConnectTimeout(30*1000)  //全局的连接超时时间
                .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间
                .setRetryCount(0)
//                可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                .setCacheMode(CacheMode.NO_CACHE);

    }

    /**
     * 设置全局公共头
     *
     * @param headers
     */
    public void addCommonHeaders(HttpHeaders headers) {
        OkGo.getInstance().addCommonHeaders(headers);
    }

    /**
     * 设置全局公共参数
     *
     * @param params
     */
    public void addCommonParams(HttpParams params) {
        OkGo.getInstance().addCommonParams(params);
    }


    public <T> void get(String url, Object tag,HttpParams params, AbsCallback<T> callback) {
        OkGo.get(url).tag(tag).params(params).execute(callback);
    }

    public <T>void  post(String url,Object tag,HttpParams params,AbsCallback<T> callback){
        OkGo.post(url).tag(tag).params(params).execute(callback);
    }

}
