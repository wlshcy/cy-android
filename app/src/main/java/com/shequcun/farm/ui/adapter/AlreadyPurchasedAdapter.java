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
import com.shequcun.farm.data.AlreadyPurchasedEntry;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

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
            vh = new ViewHolder(v);
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_details_item_ly, null);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        AlreadyPurchasedEntry entry = getItem(position);
        if (entry != null) {
            ImageLoader.getInstance().displayImage(entry.img + "?imageView2/1/w/180", vh.goodsImg, Constrants.image_display_options_disc);
            vh.goodsName.setText(entry.title);
            vh.goodsCount.setText("x" + entry.packs);
            vh.goodsPrice.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        return v;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'order_details_item_ly.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.goods_img)
        ImageView goodsImg;
        @Bind(R.id.goods_name)
        TextView goodsName;
        @Bind(R.id.goods_price)
        TextView goodsPrice;
        @Bind(R.id.goods_count)
        TextView goodsCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
