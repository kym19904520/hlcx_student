package com.up.study.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.up.study.R;
import com.up.study.bean.HomePageBean;
import com.up.study.weight.MyGridView;

/**
 * 我的主页动态
 * @author yy_cai
 *
 * 2015年8月31日
 */
public class MyHomeAdapter extends BaseAdapter {

	private List<HomePageBean> dynamicLists;
	private Context context;
	public MyHomeAdapter(List<HomePageBean> dynamicLists,
			Context context) {
		this.dynamicLists=dynamicLists;
		this.context=context;
	}

	@Override
	public int getCount() {
		return dynamicLists.size();
	}

	@Override
	public Object getItem(int position) {
		return dynamicLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh=null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_my_home3, parent,false);
			vh = new ViewHolder();
			vh.ivImage=(ImageView)convertView.findViewById(R.id.iv_my_home_img);
			vh.time=(TextView)convertView.findViewById(R.id.tv_time);
			vh.content=(TextView)convertView.findViewById(R.id.tv_content);
			vh.gvImages=(MyGridView)convertView.findViewById(R.id.gv_my_home_imgs);
			convertView.setTag(vh);
		}
		else{
			vh=(ViewHolder)convertView.getTag();
		}
		List<Bitmap> imgs = dynamicLists.get(position).getImgs();
		if(imgs.size()>1){
			vh.ivImage.setVisibility(View.GONE);
			vh.gvImages.setVisibility(View.VISIBLE);
			vh.gvImages.setAdapter(new MyHomeGridViewAdapter(imgs, context));
		}
		else if(imgs.size()==1){
			vh.ivImage.setVisibility(View.VISIBLE);
			vh.gvImages.setVisibility(View.GONE);
			vh.ivImage.setImageBitmap(imgs.get(0));
		}
		else{
			vh.gvImages.setVisibility(View.GONE);
			vh.ivImage.setVisibility(View.GONE);
		}
		vh.time.setText(dynamicLists.get(position).getTime().toString());
		vh.content.setText(dynamicLists.get(position).getContent().toString());
		return convertView;
	}
	private static class ViewHolder{
		ImageView ivImage;
		TextView time;
		TextView content;
		MyGridView gvImages;
	}

}
