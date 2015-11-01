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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsAdapter extends BaseAdapter {
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
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_red_packets, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        CouponEntry entry = (CouponEntry) getItem(position);
        if (entry.distype == 1) {
            vh.count.setText(entry.discount / 100 + "");
            vh.zheTv.setVisibility(View.GONE);
        } else if (entry.distype == 2) {
            vh.count.setText(((float) entry.discount) / 10 + "");
            vh.moneySymbolTv.setVisibility(View.GONE);
        }
        if (entry.type == 1) {
            vh.nameTv.setText("宅配套餐优惠红包");
//            vh.targetTv.setText("仅用于购买宅配套餐");
        } else {
            vh.nameTv.setText("农庄优选优惠红包");
//            vh.targetTv.setText("仅用于农庄优选商品");
        }

        if (entry.charge > 0 && entry.type != 1) {
            vh.requiredCountTv.setVisibility(View.VISIBLE);
            vh.requiredCountTv.setText("满" + entry.charge / 100 + "元使用");
        } else {
            vh.requiredCountTv.setVisibility(View.GONE);
        }
        if (entry.used || (serveTime > 0 && entry.expire <= serveTime)) {
            vh.count.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.moneySymbolTv.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.zheTv.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.expiryDate.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.requiredCountTv.setTextColor(context.getResources().getColor(R.color.gray_cccccc));
            vh.flowerIv.setBackgroundResource(R.drawable.flower_stroke_gray);
            vh.logoIv.setImageResource(R.drawable.logo_gray);
        } else {
            vh.count.setTextColor(context.getResources().getColor(R.color.red_fe786b));
            vh.moneySymbolTv.setTextColor(context.getResources().getColor(R.color.red_fe786b));
            vh.zheTv.setTextColor(context.getResources().getColor(R.color.red_fe786b));
            vh.expiryDate.setTextColor(context.getResources().getColor(R.color.red_fe786b));
            vh.requiredCountTv.setTextColor(context.getResources().getColor(R.color.red_fe786b));
            vh.flowerIv.setBackgroundResource(R.drawable.flower_stroke);
            vh.logoIv.setImageResource(R.drawable.logo);
        }
        vh.expiryDate.setText("有效期至" + Utils.getTime(entry.expire));
        return convertView;
    }

    public void addAll(List<CouponEntry> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public Object getLastItem() {
        if (this.list.isEmpty()) return null;
        return getItem(this.getCount() - 1);
    }

    static class ViewHolder {
        @Bind(R.id.money_count_tv)
        TextView count;
        @Bind(R.id.expiry_date_tv)
        TextView expiryDate;
        @Bind(R.id.required_count_tv)
        TextView requiredCountTv;
        @Bind(R.id.money_symbol_tv)
        TextView moneySymbolTv;
        @Bind(R.id.zhe_tv)
        TextView zheTv;
        @Bind(R.id.red_packets_name_tv)
        TextView nameTv;
        @Bind(R.id.target_tv)
        TextView targetTv;
        @Bind(R.id.flower_iv)
        ImageView flowerIv;
        @Bind(R.id.logo_iv)
        ImageView logoIv;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }

    public void setServeTime(long serveTime) {
        this.serveTime = serveTime;
    }
}
