package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsAdapter extends BaseAdapter{
    private List<CouponEntry> list = new ArrayList<>();
    private Context context;
    private long serveTime;

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
            vh.moneySymbolTv = (TextView)convertView.findViewById(R.id.money_symbol_tv);
            vh.flowerIv = (ImageView)convertView.findViewById(R.id.flower_iv);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder)convertView.getTag();
        }
        CouponEntry entry = (CouponEntry)getItem(position);
        if (entry.distype==1){
            vh.count.setText(entry.discount/100+"");
        }else if (entry.distype==2){
            vh.count.setText(((float)entry.discount)/10+"折");
            vh.moneySymbolTv.setVisibility(View.GONE);
        }
        if (entry.used||(serveTime>0&&entry.expire<=serveTime)){
            vh.count.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.moneySymbolTv.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.expiryDate.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.flowerIv.setImageResource(R.drawable.flower_stroke_gray);
        }
        vh.expiryDate.setText("有效期至"+Utils.getTime(entry.expire));
        return convertView;
    }

    public void addAll(List<CouponEntry> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public Object getLastItem(){
        if (this.list.isEmpty())return null;
        return getItem(this.getCount()-1);
    }

    static class ViewHolder{
        TextView count;
        TextView expiryDate;
        TextView moneySymbolTv;
        ImageView flowerIv;
    }

    public void setServeTime(long serveTime) {
        this.serveTime = serveTime;
    }
}
