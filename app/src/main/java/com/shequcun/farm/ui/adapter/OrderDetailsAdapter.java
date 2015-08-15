package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.shequcun.farm.R;

/**
 * 订单详情页
 * Created by apple on 15/8/10.
 */
public class OrderDetailsAdapter extends ArrayAdapter<T> {
    Context mContext;

    public OrderDetailsAdapter(Context context) {
        super(context, R.layout.order_details_item_ly);
        mContext = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(mContext).inflate(R.layout.order_details_item_ly, null);
            vh.goods_img = (NetworkImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.goods_count = (TextView) v.findViewById(R.id.goods_count);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        return v;
    }

    class ViewHolder {
        NetworkImageView goods_img;
        TextView goods_name;
        TextView goods_price;
        TextView goods_count;
    }

}
