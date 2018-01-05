package com.up.common.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王剑洪 2016/1/20.
 */
public abstract class CommonAdapter<T> extends BaseAdapter implements View.OnClickListener {

	protected Context context;
	protected List<T> list;
	private int itemLyoutId;

	public CommonAdapter(Context context, List<T> list, int itemLyoutId) {
		this.itemLyoutId = itemLyoutId;
		this.context = context;
		if (list==null){
			this.list = new ArrayList<>();
		}
		else {
			this.list = list;
		}
	}

	/**
	 * 刷新
	 *
	 * @param list
	 */
	public void NotifyDataChanged(List<T> list) {
		this.list = list;
		notifyDataSetChanged();
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public T getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}

	@Override
	public void onClick(View v) {

	}

	public abstract void convert(ViewHolder vh, T item, int position);

	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(context, convertView, parent, itemLyoutId, position);
	}

	// mListView.setAdapter(mAdapter = new CommonAdapter<Bean>(
	// getApplicationContext(), mDatas, R.layout.item_list)
	// {
	// @Override
	// public void convert(ViewHolder helper, Bean item)
	// {
	// helper.setText(R.id.tv_title, item.getTitle());
	// helper.setText(R.id.tv_describe, item.getDesc());
	// helper.setText(R.id.tv_phone, item.getPhone());
	// helper.setText(R.id.tv_time, item.getTime());
	//
	//// helper.getView(R.id.tv_title).setOnClickListener(l)
	// }
	//
	// });
}
