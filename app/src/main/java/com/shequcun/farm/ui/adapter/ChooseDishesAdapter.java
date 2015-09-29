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
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.Utils;

/**
 * 选择菜品 Adapter
 * Created by apple on 15/8/10.
 */
public class ChooseDishesAdapter extends ArrayAdapter<DishesItemEntry> {

    DishesItemEntry entry;
    boolean enabled;

    public ChooseDishesAdapter(Context context) {
        super(context, R.layout.goods_item_ly);
    }

    public void buildOnClickLsn(boolean enabled, AvoidDoubleClickListener onGoodsImgLsn, View.OnClickListener onAddGoodsLsn, AvoidDoubleClickListener onSubGoodsLsn) {
        this.enabled = enabled;
        this.onGoodsImgLsn = onGoodsImgLsn;
        this.onAddGoodsLsn = onAddGoodsLsn;
        this.onSubGoodsLsn = onSubGoodsLsn;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(R.layout.goods_item_ly, null);
            vh.goods_img = (ImageView) v.findViewById(R.id.goods_img);
            vh.goods_name = (TextView) v.findViewById(R.id.goods_name);
            vh.goods_price = (TextView) v.findViewById(R.id.goods_price);
            vh.goods_count = (TextView) v.findViewById(R.id.goods_count);
            vh.goods_sub = (ImageView) v.findViewById(R.id.goods_sub);
            vh.goods_add = (ImageView) v.findViewById(R.id.goods_add);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        entry = getItem(position);
        vh.goods_img.setTag(position);
        vh.goods_img.setOnClickListener(onGoodsImgLsn);
        vh.goods_add.setEnabled(enabled);
        vh.goods_add.setTag(position);
        vh.goods_add.setOnClickListener(onAddGoodsLsn);
        vh.goods_add.setContentDescription(String.valueOf(entry.id));
        vh.goods_sub.setTag(position);
        vh.goods_sub.setOnClickListener(onSubGoodsLsn);
        vh.goods_sub.setContentDescription(String.valueOf(entry.id));
        if (entry != null) {
            ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/1/w/180",vh.goods_img, Constrants.image_display_options_disc);
            vh.goods_name.setText(entry.title);
            vh.goods_price.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        if (entry.getCount() > 0) {
            vh.goods_count.setText(String.valueOf(entry.getCount()));
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
        ImageView goods_img;
        TextView goods_name;
        TextView goods_price;
        TextView goods_count;
        ImageView goods_sub;
        ImageView goods_add;
    }

    AvoidDoubleClickListener onGoodsImgLsn;
    View.OnClickListener onAddGoodsLsn;
    AvoidDoubleClickListener onSubGoodsLsn;
}
