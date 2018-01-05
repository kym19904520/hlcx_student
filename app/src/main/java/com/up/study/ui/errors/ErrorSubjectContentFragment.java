package com.up.study.ui.errors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Constants;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.common.utils.Logger;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.adapter.ExpandableListAdapter;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorSubjectContentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/6/23.
 */

public class ErrorSubjectContentFragment extends BaseFragment {

    private ExpandableListView lv;
    private ExpandableListAdapter adapter;
    private List<ErrorSubjectContentModel> list = new ArrayList<>();

    private LinearLayout ll_no_error;

    private String major_id;

    public static ErrorSubjectContentFragment newInstance(String firstTypeId) {
        ErrorSubjectContentFragment newFragment = new ErrorSubjectContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("major_id", firstTypeId);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            major_id = args.getString("major_id");
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.view_errors;
    }

    @Override
    protected void initView() {
        lv = bind(R.id.expandableListView);
        ll_no_error = bind(R.id.ll_no_error);
    }

    @Override
    protected void initData() {
        adapter = new ExpandableListAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        loadList();
    }

    @Override
    protected void initEvent() {
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*Intent intent = new Intent();
                intent.setClass(getActivity(), ErrorsClearActivity.class);
                ErrorSubjectContentModel.ListContent listContent = list.get(groupPosition).getChild().get(childPosition);
                intent.putExtra("date",listContent.getCreateDate());//时间
                intent.putExtra("source",listContent.getSource());//来源
                intent.putExtra("majorId",major_id);//科目id
                startActivity(intent);*/

                Intent intent = new Intent();
                intent.setClass(getActivity(), ErrorDetailActivity.class);
                ErrorSubjectContentModel.ListContent listContent = list.get(groupPosition).getChild().get(childPosition);
                intent.putExtra("date",listContent.getCreateDate());//时间
                intent.putExtra("source",listContent.getSource());//来源
                intent.putExtra("majorId",major_id);//科目id
                startActivity(intent);

                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void loadList() {
        list.clear();
        HttpParams params = new HttpParams();
//        params.put("stu_id", TApplication.getInstant().getStudentId());
//        params.put("major_id", major_id);
        params.put("stu_id", TApplication.getInstant().getStudentId());
        params.put("major_id", major_id);
        J.http().post(Urls.ERROR_SUBJECT_CONTENT, ctx, params, new HttpCallback<Respond<List<ErrorSubjectContentModel.ListContent>>>(null) {
            @Override
            public void success(Respond<List<ErrorSubjectContentModel.ListContent>> res, Call call, Response response, boolean isCache) {
                List<ErrorSubjectContentModel.ListContent> listContents = res.getData();
                for (int i = 0; i < listContents.size(); i++) {
                    boolean has = false;
                    for (ErrorSubjectContentModel e : list) {
                        if (e.getCreateDate().equals(listContents.get(i).getCreateDate())) {
                            has = true;
                        }
                    }
                    if (!has) {
                        ErrorSubjectContentModel e = new ErrorSubjectContentModel();
                        e.setCreateDate(listContents.get(i).getCreateDate());
                        list.add(e);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    List<ErrorSubjectContentModel.ListContent> child = new ArrayList<ErrorSubjectContentModel.ListContent>();
                    int total = 0;
                    for (int j = 0; j < listContents.size(); j++) {
                        if(list.get(i).getCreateDate()==null){//时间不能为空
                            showLoge("错题拍照时间为空");
                            break;
                        }
                        if (listContents.get(j).getCreateDate().equals(list.get(i).getCreateDate())) {
                            child.add(listContents.get(j));
                            total += listContents.get(j).getNumber();
                        }
                    }
                    list.get(i).setChild(child);
                    list.get(i).setTotal(total);
                    adapter.refresh(list, lv, i);
                }
                if(list.size()>0){
                    TApplication.getInstant().getErrors().put(major_id,list.size());
                    showLog("错题数："+TApplication.getInstant().getErrors().toString());
                    lv.setVisibility(View.VISIBLE);
                    ll_no_error.setVisibility(View.GONE);
                }
                else{
                    lv.setVisibility(View.GONE);
                    ll_no_error.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
