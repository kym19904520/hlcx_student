package com.up.study.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.up.common.J;
import com.up.common.utils.Logger;
import com.up.study.R;
import com.up.study.bean.HomePageBean;
import com.up.study.model.ImgUrl;
import com.up.study.model.MesModel;
import com.up.study.weight.MyGridView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 我的主页动态
 * @author yy_cai
 *
 * 2015年8月31日
 */
public class MesAdapter extends BaseAdapter {

	private List<MesModel> dynamicLists;
	private Context context;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	List<String> showTimes = new ArrayList<>();

	public MesAdapter(List<MesModel> dynamicLists,
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
			vh.iv_head=(ImageView)convertView.findViewById(R.id.iv_head);
			vh.tv_top_time=(TextView)convertView.findViewById(R.id.tv_top_time);
			vh.content=(TextView)convertView.findViewById(R.id.tv_content);
			vh.tv_major=(TextView)convertView.findViewById(R.id.tv_major);
			vh.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
			vh.gvImages=(MyGridView)convertView.findViewById(R.id.gv_my_home_imgs);
			convertView.setTag(vh);
		}
		else{
			vh=(ViewHolder)convertView.getTag();
		}
		List<ImgUrl> imgs = null;
		try {
			String imgUrls =  dynamicLists.get(position).getAttached();
			Type type=new TypeToken<List<ImgUrl>>(){}.getType();
			imgs = new Gson().fromJson(imgUrls,type);
		}catch (Exception e){
			Logger.e(Logger.TAG,"图片格式异常");
		}

		//List<Bitmap> imgs = dynamicLists.get(position).getAttached();
		if(imgs!=null&&imgs.size()>1){
			vh.ivImage.setVisibility(View.GONE);
			vh.gvImages.setVisibility(View.VISIBLE);
			vh.gvImages.setAdapter(new MesGridViewAdapter(imgs, context));
			vh.gvImages.setClickable(false);
			vh.gvImages.setPressed(false);
			vh.gvImages.setEnabled(false);
		}
		else if(imgs!=null&&imgs.size()==1){
			vh.ivImage.setVisibility(View.VISIBLE);
			vh.gvImages.setVisibility(View.GONE);
			//vh.ivImage.setImageBitmap(imgs.get(0));
			J.image().load(context,imgs.get(0).getUrl(),vh.ivImage);
		}
		else{
			vh.gvImages.setVisibility(View.GONE);
			vh.ivImage.setVisibility(View.GONE);
		}
		String createTime = dynamicLists.get(position).getCreateDate();
		String topTime = "";
		if(createTime != null&&createTime.length()>10){
			topTime = createTime.substring(0,10);
//			if ()
		}
		if (!showTimes.contains(topTime)) {
			showTimes.add(topTime);
			vh.tv_top_time.setVisibility(View.VISIBLE);

			Date date=new Date();//取时间
			String today = sdf.format(date);

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
			date=calendar.getTime(); //这个时间就是日期往后推一天的结果
			String yesterday = sdf.format(date);

			if(today.equals(topTime)){
				vh.tv_top_time.setText("今天");
			}
			else if (yesterday.equals(topTime)){
				vh.tv_top_time.setText("昨天");
			}
			else{
				vh.tv_top_time.setText(topTime);
			}

		}

		vh.content.setText(dynamicLists.get(position).getContent().toString());
		vh.tv_major.setText(dynamicLists.get(position).getMajor());
		vh.tv_name.setText(dynamicLists.get(position).getName());
		J.image().loadCircle(context,dynamicLists.get(position).getHead(),vh.iv_head);
		return convertView;
	}
	private static class ViewHolder{
		ImageView ivImage;
		TextView tv_top_time;
		TextView content;
		TextView tv_major;
		TextView tv_name;
		ImageView iv_head;
		MyGridView gvImages;
	}

}
