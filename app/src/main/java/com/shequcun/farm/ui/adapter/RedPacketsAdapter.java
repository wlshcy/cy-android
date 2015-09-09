package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.data.RedPacketsEntry;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsAdapter extends BaseAdapter{
    private ArrayList<CouponEntry> list = new ArrayList<>();
    private Context context;

    public RedPacketsAdapter(Context context) {
        this.context = context;
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
        ViewHolder vh= null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_red_packets,null);
            vh = new ViewHolder();
            vh.count = (TextView)convertView.findViewById(R.id.money_count_tv);
            vh.expiryDate = (TextView)convertView.findViewById(R.id.expiry_date_tv);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder)convertView.getTag();
        }
        CouponEntry entry = (CouponEntry)getItem(position);
        vh.count.setText(entry.par+"");
        vh.expiryDate.setText(Utils.getTime(entry.created));
        return convertView;
    }

    public void addAll(ArrayList<CouponEntry> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder{
        TextView count;
        TextView expiryDate;
    }
}
