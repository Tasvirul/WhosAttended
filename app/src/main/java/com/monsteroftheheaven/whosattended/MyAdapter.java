package com.monsteroftheheaven.whosattended;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tasvir on 8/3/2015.
 */
public class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<StudentInfo> list;
    public MyAdapter(Context context, ArrayList<StudentInfo> list) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public StudentInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.list_item, parent,
                    false);
            holder.name = (TextView)convertView.findViewById(R.id.tv_name);
            holder.id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.status = (TextView) convertView.findViewById(R.id.tv_status);


            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(list.size()>0){
            holder.name.setText(list.get(position).getStudentName());
            holder.id.setText(list.get(position).getStudentId());
            if(list.get(position).isStatus()){
                holder.status.setText("present");
                holder.status.setTextColor(Color.GREEN);
            }else {
                holder.status.setText("absent");
                holder.status.setTextColor(Color.RED);
            }
        }
        return convertView;
    }

    class ViewHolder{
            TextView name,id,status;

    }
}
