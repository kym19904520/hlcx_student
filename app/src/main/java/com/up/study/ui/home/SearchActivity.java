package com.up.study.ui.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Constants;
import com.up.common.utils.SPUtil;
import com.up.study.R;
import com.up.study.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 手动查询
 */
public class SearchActivity extends BaseFragmentActivity {
    private ListView lv ;
    private CommonAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private TextView tv_right;
    private EditText et_search;
    @Override
    protected int getContentViewId() {
        return R.layout.act_search;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.lv);
        tv_right = bind(R.id.tv_right);
        et_search = bind(R.id.et_search);
    }

    @Override
    protected void initEvent() {
        tv_right.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        String history = SPUtil.getString(this,Constants.SP_HISTORY_SEARCH,"");
        String []  historyList = history.split(";");
        dataList  = Arrays.asList(historyList);
        adapter = new CommonAdapter<String>(ctx, dataList, R.layout.item_simple_text) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                // TextView tv11 = vh.getView(R.id.tv_offer_item_11);
                vh.setText(R.id.text,item);
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_search.setText(dataList.get(i));
                et_search.setSelection(dataList.get(i).length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==tv_right){
            String search = et_search.getText().toString().trim();
            if (TextUtils.isEmpty(search)){
                showToast("查询文本不能为空");
                return;
            }
            gotoActivity(SearchResultActivity.class,"search",search);
            String history = SPUtil.getString(this,Constants.SP_HISTORY_SEARCH,"");
            String []  historyList = history.split(";");
            boolean has = false;//是否已经有了
            for (int i = 0;i<historyList.length;i++){
                if (historyList[i].equals(search)){
                    has  = true;
                    break;
                }
            }
            if(!has){
                history += search+";";
                SPUtil.putString(this, Constants.SP_HISTORY_SEARCH,history);
            }
            this.finish();
        }
    }

}
