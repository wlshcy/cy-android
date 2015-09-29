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
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Utils;

/**
 * Created by apple on 15/8/18.
 */
public class RecommendAdapter extends ArrayAdapter<RecommendEntry> {

    public RecommendAdapter(Context context) {
        super(context, R.layout.recomend_dishes_item_ly);
    }

    public void buildOnClickLsn(AvoidDoubleClickListener onGoodsImgLsn, AvoidDoubleClickListener onBuyLsn) {
        this.onGoodsImgLsn = onGoodsImgLsn;
        this.onBuyLsn = onBuyLsn;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.recomend_dishes_item_ly, null);
            vh = new ViewHolder();
            vh.goods_img = (ImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.buy_tv = v.findViewById(R.id.buy_tv);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        RecommendEntry entry = getItem(position);
        if (entry != null) {
            vh.goods_img.setTag(position);
            vh.goods_img.setOnClickListener(onGoodsImgLsn);
            vh.buy_tv.setTag(position);
            vh.buy_tv.setOnClickListener(onBuyLsn);
            ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/2/w/180",vh.goods_img);
            vh.goods_name.setText(entry.title);
            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        return v;
    }

    class ViewHolder {
        public ImageView goods_img;
        public TextView goods_name;
        public TextView goods_price;
        public View buy_tv;
    }

    AvoidDoubleClickListener onGoodsImgLsn;
    AvoidDoubleClickListener onBuyLsn;
}
