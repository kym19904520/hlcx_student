package com.up.study.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.up.common.utils.DateUtils;
import com.up.study.R;
import com.up.study.model.ErrorSubjectContentModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/5/26.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private List<ErrorSubjectContentModel> list;
    private Context context;

    private String today;
    private String yesterday;
    private String beforeYesterday;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();//更新数据
            super.handleMessage(msg);
        }
    };

    public ExpandableListAdapter(Context context, List<ErrorSubjectContentModel> list) {
        this.context = context;
        this.list = list;
        today = DateUtils.getDateFormat(0);
        yesterday = DateUtils.getDateFormat(-1);
        beforeYesterday = DateUtils.getDateFormat(-2);
    }


    //父列表刷新
    public void refresh(List<ErrorSubjectContentModel> parentList, ExpandableListView expandableListView, int groupPosition) {
        this.list = parentList;
        handler.sendMessage(new Message());
        //必须重新伸缩之后才能更新数据
        expandableListView.collapseGroup(groupPosition);
        expandableListView.expandGroup(groupPosition);
        expandableListView.collapseGroup(groupPosition);
    }

    //得到子item需要关联的数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChild().get(childPosition);
    }

    //得到子item的ID
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //设置子item的组件
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //String info = map.get(key).get(childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expand_lv_child, null);
        }
        TextView tv_child_name = (TextView) convertView.findViewById(R.id.tv_child_name);
        tv_child_name.setText(list.get(groupPosition).getChild().get(childPosition).getSourceName());

        TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
        tv_num.setText(list.get(groupPosition).getChild().get(childPosition).getNumber() + "");

        return convertView;
    }

    //获取当前父item下的子item的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).getChild().size();
    }

    //获取当前父item的数据
    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //设置父item组件
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expand_lv_parent, null);
        }

        TextView tv_parent_name = (TextView) convertView.findViewById(R.id.tv_parent_name);
        String date = list.get(groupPosition).getCreateDate();
        if (date.equals(today)) {
            tv_parent_name.setText("今天");
        } else if (date.equals(yesterday)) {
            tv_parent_name.setText("昨天");
        } else if (date.equals(beforeYesterday)) {
            tv_parent_name.setText("前天");
        } else {
            tv_parent_name.setText(date);
        }

        TextView tv_parent_num = (TextView) convertView.findViewById(R.id.tv_parent_num);
        tv_parent_num.setText(list.get(groupPosition).getTotal() + "");
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
