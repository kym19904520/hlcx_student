package com.up.study.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.up.study.R;
import com.up.study.weight.MyGridView;

/**
 * 我的主页GridView图片适配器
 * @author yy_cai
 *
 * 2015年8月31日
 */
public class MyHomeGridViewAdapter extends BaseAdapter {

	private List<Bitmap> imgs;
	private Context context;
	public MyHomeGridViewAdapter(List<Bitmap> imgs,Context context) {
		this.imgs=imgs;
		this.context=context;
	}

	@Override
	public int getCount() {
		return imgs.size();
	}

	@Override
	public Object getItem(int position) {
		return imgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh=null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.simple_one_image_item, parent,false);
			vh = new ViewHolder();
			vh.ivImage=(ImageView)convertView.findViewById(R.id.image);
			convertView.setTag(vh);
		}
		else{
			vh=(ViewHolder)convertView.getTag();
		}
		MyGridView gv =(MyGridView)parent;
//		int horizontalSpacing = gv.getHorizontalSpacing();//item之间的水平宽度
		int horizontalSpacing = 3;
		int numColumns = gv.getNumColumns();//gridView的列数
		int itemWidth = (gv.getWidth()-(numColumns-1)*horizontalSpacing
				-gv.getPaddingLeft()-gv.getPaddingRight()) / numColumns;
		LayoutParams params = new LayoutParams(itemWidth,itemWidth);
		vh.ivImage.setLayoutParams(params);
		vh.ivImage.setImageBitmap(imgs.get(position));
		return convertView;
	}
	private static class ViewHolder{
		ImageView ivImage;
	}

}
