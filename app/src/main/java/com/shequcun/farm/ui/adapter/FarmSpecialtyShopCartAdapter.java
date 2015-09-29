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
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mac on 15/9/8.
 */
public class FarmSpecialtyShopCartAdapter extends ArrayAdapter<RecommendEntry> {
    public FarmSpecialtyShopCartAdapter(Context context) {
        super(context, R.layout.goods_item_ly);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.goods_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        RecommendEntry entry = getItem(position);
        vh.goods_img.setTag(position);
        vh.goods_img.setOnClickListener(onGoodsImgLsn);
//        vh.goods_add.setEnabled(enabled);
        vh.goods_add.setTag(position);
        vh.goods_add.setOnClickListener(onAddGoodsLsn);
        vh.goods_add.setContentDescription(String.valueOf(entry.id));
        vh.goods_sub.setTag(position);
        vh.goods_sub.setOnClickListener(onSubGoodsLsn);
        vh.goods_sub.setContentDescription(String.valueOf(entry.id));
        if (entry != null) {
            ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/2/w/180",vh.goods_img);
            vh.goods_name.setText(entry.title);
            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        if (entry.count > 0) {
            vh.goods_count.setText(String.valueOf(entry.count));
            vh.goods_count.setVisibility(View.VISIBLE);
            vh.goods_sub.setVisibility(View.VISIBLE);
        } else {
            vh.goods_sub.setVisibility(View.GONE);
            vh.goods_count.setText("0");
            vh.goods_count.setVisibility(View.GONE);
        }

        return v;
    }

    class ViewHolder {
        @Bind(R.id.goods_img)
        ImageView goods_img;
        @Bind(R.id.goods_name)
        TextView goods_name;
        @Bind(R.id.goods_price)
        TextView goods_price;
        @Bind(R.id.goods_count)
        TextView goods_count;
        @Bind(R.id.goods_sub)
        ImageView goods_sub;
        @Bind(R.id.goods_add)
        ImageView goods_add;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    public void buildOnClickLsn(View.OnClickListener onGoodsImgLsn, View.OnClickListener onAddGoodsLsn, View.OnClickListener onSubGoodsLsn) {
        this.onGoodsImgLsn = onGoodsImgLsn;
        this.onAddGoodsLsn = onAddGoodsLsn;
        this.onSubGoodsLsn = onSubGoodsLsn;
    }

    View.OnClickListener onGoodsImgLsn;
    View.OnClickListener onAddGoodsLsn;
    View.OnClickListener onSubGoodsLsn;
}
