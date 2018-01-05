package com.up.study.ui.my;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.utils.TestDatas;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/4/21.
 * 聊天窗口
 */

public class ChatActivity extends BaseFragmentActivity {
    private ListView lv;
    private CommonAdapter<String> adapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.act_chat;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
    }

    @Override
    protected void initData() {
        TestDatas.add(list);
        adapter = new CommonAdapter<String>(ctx, list, R.layout.item_chat) {
            @Override
            public void convert(ViewHolder vh, String item, final int position) {
                LinearLayout ll_chat_left = vh.getView(R.id.ll_chat_left);
                LinearLayout ll_chat_right = vh.getView(R.id.ll_chat_right);
                if ("1".equals(item)){
                    ll_chat_left.setVisibility(View.GONE);
                    ll_chat_right.setVisibility(View.VISIBLE);
                }
                else{
                    ll_chat_left.setVisibility(View.VISIBLE);
                    ll_chat_right.setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    public void onClick(View v) {
    }
}
