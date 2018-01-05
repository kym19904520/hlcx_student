package com.up.study.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.study.R;

import java.util.List;

/**
 * Created by dell on 2017/7/30.
 */

public class ImgSelectorAdpter extends CommonAdapter<String> {
    List<String> imageList;

    public ImgSelectorAdpter(Context context, List<String> list, int itemLyoutId,List<String> imageList) {
        super(context, list, itemLyoutId);
        this.imageList = imageList;
    }

    @Override
    public void convert(ViewHolder vh, String item, int position) {
        final int index = position;
        ImageView ivDel = vh.getView(R.id.iv_del);
        if ("add".equals(item)) {
            ivDel.setVisibility(View.GONE);
            //vh.setImageResource(R.id.iv, R.mipmap.sy_add_img);
        } else {
            ivDel.setVisibility(View.VISIBLE);
        }
        vh.setImageByUrl(R.id.iv, item);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageList.remove(index);
                if (!"add".equals(imageList.get(imageList.size() - 1))) {
                    imageList.add("add");
                }
                NotifyDataChanged(imageList);
            }
        });
    }
}
