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
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.util.Utils;

/**
 * Created by apple on 15/8/31.
 */
public class SpecialtyAdapter extends ArrayAdapter<RecommendEntry> {


    public SpecialtyAdapter(Context context) {
        super(context, R.layout.specialty_item_ly);
    }

    public void buildOnClickLsn(View.OnClickListener onGoodsImgLsn, View.OnClickListener onBuyLsn) {
        this.onGoodsImgLsn = onGoodsImgLsn;
        this.onBuyLsn = onBuyLsn;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.specialty_item_ly, null);
            vh = new ViewHolder();
            vh.goods_img = (NetworkImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.buy_tv = v.findViewById(R.id.buy_tv);
            vh.buy_textview = (TextView) v.findViewById(R.id.buy_textview);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        RecommendEntry entry = getItem(position);
        if (entry != null) {
            vh.goods_img.setTag(position);
            vh.goods_img.setOnClickListener(onGoodsImgLsn);

            if (entry.type == 2 && entry.bought) {
                vh.buy_tv.setEnabled(false);
                vh.buy_textview.setEnabled(false);
            } else {
                vh.buy_tv.setEnabled(true);
                vh.buy_textview.setEnabled(true);
                vh.buy_tv.setTag(position);
                vh.buy_tv.setOnClickListener(onBuyLsn);
            }

            vh.goods_img.setImageUrl(entry.imgs[0], ImageCacheManager.getInstance().getImageLoader());
            vh.goods_name.setText(entry.title);
            vh.goods_price.setText(Utils.unitPeneyToYuan(entry.price));
//            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        return v;
    }

    class ViewHolder {
        public NetworkImageView goods_img;
        public TextView goods_name;
        public TextView goods_price;
        public View buy_tv;
        public TextView buy_textview;
    }

    View.OnClickListener onGoodsImgLsn;
    View.OnClickListener onBuyLsn;

}
