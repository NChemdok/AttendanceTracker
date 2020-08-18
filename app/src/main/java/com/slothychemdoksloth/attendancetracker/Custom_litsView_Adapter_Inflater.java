package com.slothychemdoksloth.attendancetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class Custom_litsView_Adapter_Inflater extends BaseAdapter {

    private Context mContext;
    private ArrayList<Custom_listView_Adapter> subjects_status;
    private LayoutInflater inflater;

    public Custom_litsView_Adapter_Inflater(Context mContext, ArrayList<Custom_listView_Adapter> subjects_status) {
        this.mContext = mContext;
        this.subjects_status = subjects_status;
    }

    @Override
    public int getCount() {
        return subjects_status.size();
    }

    @Override
    public Object getItem(int position) {
        return subjects_status.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(inflater == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.custom_listview_status, viewGroup, false);
        }

        Custom_Subject_Status_Holder holder = new Custom_Subject_Status_Holder(convertView);
        holder.view_name.setText(subjects_status.get(position).getName());
        holder.view_total.setText(subjects_status.get(position).getTotal());
        holder.view_present.setText(subjects_status.get(position).getPresent());
        holder.view_avg.setText(subjects_status.get(position).getAvg());

        return convertView;
    }
}
