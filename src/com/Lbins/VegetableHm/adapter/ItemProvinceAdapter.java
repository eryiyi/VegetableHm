package com.Lbins.VegetableHm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.Lbins.VegetableHm.R;
import com.Lbins.VegetableHm.module.ProvinceObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemProvinceAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ProvinceObj> lists;
    private Context mContect;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemProvinceAdapter(List<ProvinceObj> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_area, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.msgnum = (TextView) convertView.findViewById(R.id.msgnum);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ProvinceObj cell = lists.get(position);
        if (cell != null) {
            holder.title.setTextColor(mContect.getResources().getColor(R.color.mm_top_bg));
            holder.title.setText(cell.getProvince());
//            holder.msgnum.setText((cell.get()==null?"0":cell.getMsgNum()));
        }
        return convertView;
    }

    class ViewHolder {
        TextView msgnum;
        TextView title;
    }
}
