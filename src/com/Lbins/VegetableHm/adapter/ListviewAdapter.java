package com.Lbins.VegetableHm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.VegetableHm.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/12.
 */
public class ListviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;

    public ListviewAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ItemRecordAdapter.ViewHolder viewHolder = null;
        if (arg1 == null && list.size() != 0) {
            viewHolder = new ItemRecordAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            arg1 = inflater.inflate(R.layout.spinner_item, null);
            viewHolder.nickname = (TextView) arg1.findViewById(R.id.label);
            arg1.setTag(viewHolder);
        } else
            viewHolder = (ItemRecordAdapter.ViewHolder) arg1.getTag();
        viewHolder.nickname.setText(list.get(arg0));
        return arg1;
    }
}
