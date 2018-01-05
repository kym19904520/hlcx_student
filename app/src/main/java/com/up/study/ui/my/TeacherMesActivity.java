package com.up.study.ui.my;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;
import com.up.study.ui.home.HomeworkActivity;
import com.up.study.ui.home.TaskTestInputActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 老师留言
 */
public class TeacherMesActivity extends BaseFragmentActivity {
    private ListView lv ;
    private CommonAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    @Override
    protected int getContentViewId() {
        return R.layout.act_teacher_mes;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        dataList.add("");
        adapter = new CommonAdapter<String>(ctx, dataList, R.layout.item_teacher_mes) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                // TextView tv11 = vh.getView(R.id.tv_offer_item_11);
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            gotoActivity(ChatActivity.class,null);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

}
