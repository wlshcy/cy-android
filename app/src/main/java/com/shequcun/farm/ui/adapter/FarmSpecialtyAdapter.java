package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
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
 * Created by mac on 15/9/6.
 */
public class FarmSpecialtyAdapter extends ArrayAdapter<RecommendEntry> {
    public FarmSpecialtyAdapter(Context context) {
        super(context, R.layout.farm_specialty_item_ly);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.farm_specialty_item_ly, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        RecommendEntry entry = getItem(position);
        if (entry != null && vh != null) {
            if (entry.imgs != null && entry.imgs.length > 0)
                ImageLoader.getInstance().displayImage(entry.imgs[0]+"?imageview2/2/w/400",vh.goods_img);
            vh.goods_name.setText(entry.title);
            if (entry.type == 2) {
                vh.spike_tv.setVisibility(View.VISIBLE);
            } else {
                vh.spike_tv.setVisibility(View.GONE);
            }
            vh.price_tv.setText(Utils.unitPeneyToYuan(entry.price));
            Paint paint = vh.mprice_tv.getPaint();
            paint.setAntiAlias(true);//抗锯齿
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            vh.mprice_tv.setText(Utils.unitPeneyToYuan(entry.mprice));
            vh.sale_tv.setText(entry.sales + "人选择");
            vh.merge_unit_tv.setText(Utils.unitConversion(entry.packw) + "/份");
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.goods_img)
        ImageView goods_img;
        @Bind(R.id.goods_name)
        TextView goods_name;
        @Bind(R.id.merge_unit_tv)
        TextView merge_unit_tv;//计量单位
        @Bind(R.id.price_tv)
        TextView price_tv;//现价
        @Bind(R.id.mprice_tv)
        TextView mprice_tv;//市场价
        @Bind(R.id.sale_tv)
        TextView sale_tv;//销量
        @Bind(R.id.spike_tv)
        TextView spike_tv;

        ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
