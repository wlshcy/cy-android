package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/7.
 */
public class GoodsItemAdapter extends ArrayAdapter<T> {

    public GoodsItemAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.goods_item_ly, null);
            vh = new ViewHolder();
            vh.goods_add = (ImageView) v.findViewById(R.id.goods_add);
            vh.goods_sub = (ImageView) v.findViewById(R.id.goods_sub);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_img = (NetworkImageView) v.findViewById(R.id.goods_img);
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
        ImageView goods_sub;
        ImageView goods_add;
    }
}
