package com.up.common.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * TODO:Banner 图片加载器
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by 王剑洪
 * On 2017/3/10.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }
}
