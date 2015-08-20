package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AlreadyPurchasedEntry;
import com.shequcun.farm.util.Utils;

/**
 * Created by apple on 15/8/20.
 */
public class AlreadyPurchasedAdapter extends ArrayAdapter<AlreadyPurchasedEntry> {
    public AlreadyPurchasedAdapter(Context context) {
        super(context, R.layout.order_details_item_ly);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_details_item_ly, null);
            vh.goods_img = (NetworkImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.goods_count = (TextView) v.findViewById(R.id.goods_count);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        AlreadyPurchasedEntry entry = getItem(position);
        if (entry != null) {
            vh.goods_img.setImageUrl(entry.img, ImageCacheManager.getInstance().getImageLoader());
            vh.goods_name.setText(entry.title);
            vh.goods_count.setText("x" + entry.packs);
            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
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
