package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.util.Utils;

/**
 * 套餐介绍 Adapter
 * Created by apple on 15/8/10.
 */
public class ComboAdapter extends ArrayAdapter<ComboEntry> {

    ComboEntry entry;

    boolean isMyCombo;

    public ComboAdapter(Context context) {
        super(context, R.layout.combo_item_ly);
    }

    public void setIsMyCombo(boolean isMyCombo) {
        this.isMyCombo = isMyCombo;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            vh = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.combo_item_ly, null);
            vh.my_combo = (TextView) view.findViewById(R.id.my_combo);
            vh.combo_name = (TextView) view.findViewById(R.id.combo_name);
            vh.per_weight = (TextView) view.findViewById(R.id.per_weight);
            vh.times = (TextView) view.findViewById(R.id.times);
            vh.dis_cycle = (TextView) view.findViewById(R.id.dis_cycle);
            vh.all_weight = (TextView) view.findViewById(R.id.all_weight);
            vh.combo_price = (TextView) view.findViewById(R.id.combo_price);
            vh.combo_img = (ImageView) view.findViewById(R.id.combo_img);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        entry = getItem(position);
        if (entry != null) {
            //每月配送几次
//            int perMonth=0;
            if (entry.isMine()) {
                vh.my_combo.setVisibility(View.VISIBLE);
                vh.combo_name.setVisibility(View.GONE);
            } else {
                vh.my_combo.setVisibility(View.GONE);
                vh.combo_name.setVisibility(View.VISIBLE);
            }

            vh.combo_name.setText(entry.title);
            ImageCacheManager.getInstance().displayImage(vh.combo_img,entry.img);
//            vh.combo_img.setImageUrl(entry.img, ImageCacheManager.getInstance().getImageLoader());
            if (entry.shipday != null) {
//                StringBuilder result = new StringBuilder();
//                for (int i = 0; i < entry.shipday.length; i++) {
//                    if (result.length() > 0)
//                        result.append("、");
//                    result.append(entry.shipday[i]);
//                }
//                vh.dis_cycle.setText("每周" + result.toString() + "配送");
                vh.dis_cycle.setText(entry.shipday.length + "次/周");//"每周配送" +
//                perMonth=4*entry.shipday.length;

            }

            if (entry.weights != null) {
//                StringBuilder result = new StringBuilder();
//                for (int i = 0; i < entry.weights.length; i++) {
//                    if (result.length() > 0)
//                        result.append("、");
//                    result.append(Utils.unitConversion(entry.weights[i]));
//                }
//                vh.per_weight.setText("每次配送" + result.toString());
                vh.per_weight.setText(Utils.unitConversion(entry.weights[entry.index]) + "/次");//"每次配送" +
                vh.all_weight.setText("共" + Utils.unitConversion(entry.duration * entry.weights[entry.index] * entry.shipday.length));
            }

            vh.times.setText("送" + entry.duration + "周");
            vh.combo_price.setText( Utils.unitPeneyToYuan(entry.prices[entry.index]));
        }

        return view;
    }

    class ViewHolder {
        /**
         * 我的套餐
         */
        TextView my_combo;
        /**
         * 套餐名称
         */
        TextView combo_name;
        /**
         * 配送斤数例如每次6斤次
         */
        TextView per_weight;
        /**
         * 配送周期例如每周配送1次
         */
        TextView dis_cycle;
        /**
         * 4次/月
         */
        TextView times;
        /**
         * 套餐斤数
         */
        TextView all_weight;
        /**
         * 套餐价格
         */
        TextView combo_price;
        /**
         * 套餐图片
         */
        ImageView combo_img;
    }
}
