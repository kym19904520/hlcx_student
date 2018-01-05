package com.up.study.weight.tree;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.up.study.R;


public class MyTreeListViewAdapter<T> extends TreeListViewAdapter<T> {

	public MyTreeListViewAdapter(ListView mTree, Context context,
			List<T> datas, int defaultExpandLevel,boolean isHide)
			throws IllegalArgumentException, IllegalAccessException {
		super(mTree, context, datas, defaultExpandLevel,isHide);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getConvertView(Node node, int position, View convertView,
			ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.item_list_tree, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.id_treenode_icon);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.id_treenode_name);
			viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.id_treeNode_check);
			
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() == -1)
		{
			viewHolder.icon.setVisibility(View.INVISIBLE);
		} else
		{
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
		}

		if(node.isHideChecked()){
			viewHolder.checkBox.setVisibility(View.GONE);
		}else{
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			setCheckBoxBg(viewHolder.checkBox,node.isChecked());
		}
		viewHolder.label.setText(node.getName());
		
		
		return convertView;
	}
	private final class ViewHolder
	{
		ImageView icon;
		TextView label;
		CheckBox checkBox;
	}
	
	/**
	 * checkbox是否显示
	 * @param cb
	 * @param isChecked
	 */
	private void setCheckBoxBg(CheckBox cb,boolean isChecked){
		if(isChecked){
			cb.setBackgroundResource(R.mipmap.icon_2_off);
		}else{
			cb.setBackgroundResource(R.mipmap.icon_2_on);
		}
	}

}
