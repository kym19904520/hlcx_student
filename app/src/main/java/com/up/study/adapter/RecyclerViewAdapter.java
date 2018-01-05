package com.up.study.adapter;

/**
 * Created by dell on 2017/6/15.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.up.study.R;

import java.util.List;

/**
 * 知识点
 * RecyclerView适配器
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> mDataList;

    public RecyclerViewAdapter(List<String> list) {
        mDataList = list;
    }

    @Override
    public int getItemCount() {
        // 返回数据集合大小
        return mDataList == null ? 0 : mDataList.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //获取这个TextView
        TextView tv= holder.mTvTitle;
        tv.setText(mDataList.get(position));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_know_name, parent, false));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_know_name);
        }

    }


}