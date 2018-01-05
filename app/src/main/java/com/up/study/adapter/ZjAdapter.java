package com.up.study.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.up.study.R;
import com.up.study.model.JcAndJZModel;

import java.util.List;

/**
 * 智能组卷----高级---选择章节，知识点的adapter
 * Created by kym on 2017/10/31.
 */

public class ZjAdapter extends BaseAdapter {
    private List<JcAndJZModel> list;
    private Context context;
    public boolean flage = false;
    public LayoutInflater inflater;

    public ZjAdapter(Context context, List<JcAndJZModel> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.zj_item_view, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final JcAndJZModel mode = list.get(position);
        viewHolder.title.setText(list.get(position).getName());
        viewHolder.checkBox.setChecked(mode.isCheck());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.isCheck()) {
                    mode.isCheck = false;
                } else {
                    mode.isCheck = true;
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        CheckBox checkBox;
    }
}
