package com.up.common.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * TODO:
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/12.
 */

public class BaseBean implements Serializable {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
