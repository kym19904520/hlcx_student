package com.up.common;

import android.app.Application;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.up.common.http.GlideManager;
import com.up.common.http.HttpManager;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/16.
 */

public class J {
    public static void initHttp(Application application, HttpHeaders headers, HttpParams params) {
        HttpManager.init(application);
        if (null != headers) {
            HttpManager.getInstance().addCommonHeaders(headers);
        }
        if (null != params) {
            HttpManager.getInstance().addCommonParams(params);
        }
    }

    public static HttpManager http() {
        return HttpManager.getInstance();
    }

    public static GlideManager image() {
        return GlideManager.getInstance();
    }
}
