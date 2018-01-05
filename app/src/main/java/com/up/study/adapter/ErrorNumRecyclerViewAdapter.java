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
import com.up.study.model.WrongMsgSubModel;

import java.util.List;

/**
 * 错题量
 * RecyclerView适配器
 */
public class ErrorNumRecyclerViewAdapter extends RecyclerView.Adapter<ErrorNumRecyclerViewAdapter.ViewHolder> {

    private List<WrongMsgSubModel> mDataList;

    public ErrorNumRecyclerViewAdapter(List<WrongMsgSubModel>  list) {
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
        holder.mTvName.setText(mDataList.get(position).getSourceName());
        holder.mTvNum.setText(mDataList.get(position).getWrongNum()+"");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_error_num, parent, false));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvName;
        private TextView mTvNum;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvNum = (TextView) itemView.findViewById(R.id.tv_num);
        }

    }


}