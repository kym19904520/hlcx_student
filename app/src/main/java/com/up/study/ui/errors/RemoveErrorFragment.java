package com.up.study.ui.errors;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.base.CommonAdapter;
import com.up.common.base.ViewHolder;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragment;
import com.up.study.model.HasRemoveErrorsModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017/7/3.
 * 已移除错题数
 */

public class RemoveErrorFragment extends BaseFragment{

    String majorId;
    ListView treeLv;
    List<HasRemoveErrorsModel> list= new ArrayList<HasRemoveErrorsModel>();
    CommonAdapter<HasRemoveErrorsModel> adapter;
    private LinearLayout ll_no_error;

    @Override
    protected int getContentViewId() {
        return R.layout.view_ctzsdjx;
    }

    @Override
    protected void initView() {
        majorId = getArguments().getString("majorId");
        TApplication.getInstant().setMarjorId(majorId);
        treeLv = bind(R.id.tree_lv);
        ll_no_error = bind(R.id.ll_no_error);

        adapter = new CommonAdapter<HasRemoveErrorsModel>(ctx, list, R.layout.item_expand_lv_parent) {
            @Override
            public void convert(ViewHolder vh, HasRemoveErrorsModel item, int position) {
                // TextView tv11 = vh.getView(R.id.tv_offer_item_11);
                vh.setText(R.id.tv_parent_name,item.getSourceName());
                vh.setText(R.id.tv_parent_num,item.getNumber()+"");
            }
        };
        treeLv.setAdapter(adapter);
        treeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivityWithBean(RemoveErrorsDetailActivity.class,list.get(position),null);
            }
        });

    }

    @Override
    protected void initData() {
        getHasRemoveErrors();
    }

    @Override
    protected void initEvent() {}

    @Override
    public void onClick(View v) {}

    /**
     * 获取已移除错题数量
     */
    private void getHasRemoveErrors() {
        list.clear();
        HttpParams params = new HttpParams();
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", majorId);
        J.http().post(Urls.HAS_REMOVE_ERROR, ctx, params, new HttpCallback<Respond<List<HasRemoveErrorsModel>>>(null) {
            @Override
            public void success(Respond<List<HasRemoveErrorsModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<HasRemoveErrorsModel> data = res.getData();
                    if(data!=null){
                        list.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
