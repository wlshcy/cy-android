package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.util.Utils;

/**
 * 订单详情页
 * Created by apple on 15/8/10.
 */
public class OrderDetailsAdapter extends ArrayAdapter<DishesItemEntry> {

    public OrderDetailsAdapter(Context context) {
        super(context, R.layout.order_details_item_ly);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_details_item_ly, null);
            vh.goods_img = (ImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.goods_count = (TextView) v.findViewById(R.id.goods_count);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        DishesItemEntry entry = getItem(position);
        if (entry != null) {
            ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/2/w/180",vh.goods_img);
            vh.goods_name.setText(entry.title);
            int count = entry.getCount();
            if (count <= 0) {
                vh.goods_count.setVisibility(View.GONE);
            } else {
                vh.goods_count.setVisibility(View.VISIBLE);
                vh.goods_count.setText("x" + count);
            }

            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        return v;
    }

    class ViewHolder {
        ImageView goods_img;
        TextView goods_name;
        TextView goods_price;
        TextView goods_count;
    }

}
