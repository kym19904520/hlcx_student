package com.up.study.ui.errors;

import android.view.View;
import android.widget.ListView;

import com.lzy.okgo.model.HttpParams;
import com.up.common.J;
import com.up.common.conf.Urls;
import com.up.common.http.HttpCallback;
import com.up.common.http.Respond;
import com.up.study.R;
import com.up.study.TApplication;
import com.up.study.base.BaseFragment;
import com.up.study.model.ErrorKnowModel;
import com.up.study.weight.treeee.MyNodeBean;
import com.up.study.weight.treeee.MyTreeListViewAdapter;
import com.up.study.weight.treeee.Node;
import com.up.study.weight.treeee.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by dell on 2017/6/29.
 * 知识点解析
 */

public class ErrorZSDJXFragment extends BaseFragment{
    ListView treeLv;
    List<MyNodeBean> mDatas;
    MyTreeListViewAdapter<MyNodeBean> adapter;

    private String majorId;
    @Override
    protected int getContentViewId() {
        return R.layout.view_ctzsdjx;
    }

    @Override
    protected void initView() {
        majorId = getArguments().getString("majorId");
        showLog("ErrorZSDJXFragment  majorId="+majorId);
        treeLv = bind(R.id.tree_lv);
        mDatas = new ArrayList<MyNodeBean>();
        try {
            adapter = new MyTreeListViewAdapter<MyNodeBean>(treeLv, mParentActivity,mDatas, 0, true);
            adapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    if (node.isLeaf()) {
                        gotoActivityWithBean(ZsdjxDetailActivity.class,node,null);//note extends BaseBean
                    }
                }

                @Override
                public void onCheckChange(Node node, int position,
                                          List<Node> checkedNodes) {
                }

            });
            treeLv.setAdapter(adapter);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        getErrorKonewList();
    }

    @Override
    protected void initData() {}

    @Override
    protected void initEvent() {}

    @Override
    public void onClick(View v) {}

    /**
     * 获取树形图数据
     */
    private void getErrorKonewList() {
        mDatas.clear();
        HttpParams params = new HttpParams();
        params.put("class_id", TApplication.getInstant().getClassId());
        params.put("major_id", majorId);
        params.put("grade_id", TApplication.getInstant().getGradeId());
        params.put("stu_id", TApplication.getInstant().getStudentId());

        J.http().post(Urls.ERROR_KNOW_LIST, ctx, params, new HttpCallback<Respond<List<ErrorKnowModel>>>(ctx) {
            @Override
            public void success(Respond<List<ErrorKnowModel>> res, Call call, Response response, boolean isCache) {
                if(Respond.SUC.equals(res.getCode())){
                    List<ErrorKnowModel> resList = res.getData();
                    List<ErrorKnowModel> list = new ArrayList<ErrorKnowModel>();
                    //整理数据成四级模式
                    for(int i = 0;i<resList.size();i++){
                        if (resList.get(i).getTotalCount()!=0){
                            if (resList.get(i).getType()==1&&resList.get(i).getMeterialId()!=0){//小章节type设为2
                                resList.get(i).setType(2);
                                resList.get(i).setParentId(resList.get(i).getMeterialId());
                            }
                            else if(resList.get(i).getType()==2){//知识点type设为3
                                resList.get(i).setType(3);
                            }
                            list.add(resList.get(i));
                        }

                    }

                    if(list!=null&&list.size()>0){
                        for (int i = 0 ;i<list.size();i++){
                                mDatas.add(new MyNodeBean(list.get(i).getId(), list.get(i).getParentId(),
                                        list.get(i).getName()+"  ( "+list.get(i).getTotalCount()+" )",list.get(i).getType()));
                        }
                        try {
                            adapter.updateAdapter(mDatas,0,true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        ErrorZSDJXFragment.this.bind(R.id.ll_no_error).setVisibility(View.VISIBLE);
                        treeLv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
