package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.lynp.ui.data.RecommendEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;

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
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        RecommendEntry entry = getItem(position);
//        if (entry != null) {
//            vh.goods_img.setTag(position);
//            vh.goods_img.setOnClickListener(onGoodsImgLsn);
//            vh.buy_tv.setTag(position);
//            vh.buy_tv.setOnClickListener(onBuyLsn);
//            ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/2/w/180",vh.goods_img);
//            vh.goods_name.setText(entry.title);
//            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
//        }
        return v;
    }

    class ViewHolder {
        @Bind(R.id.photo)
        ImageView photo;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.buy_tv)
        View buy_tv;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    AvoidDoubleClickListener onGoodsImgLsn;
    AvoidDoubleClickListener onBuyLsn;
}
