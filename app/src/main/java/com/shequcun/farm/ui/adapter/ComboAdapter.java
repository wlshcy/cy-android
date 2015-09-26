package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
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

import butterknife.Bind;
import butterknife.ButterKnife;

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
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.combo_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        entry = getItem(position);
        if (entry != null) {
            if (entry.isMine()) {
                vh.my_combo.setVisibility(View.VISIBLE);
                vh.combo_name.setVisibility(View.GONE);
            } else {
                vh.my_combo.setVisibility(View.GONE);
                vh.combo_name.setVisibility(View.VISIBLE);
            }

            vh.combo_name.setText(entry.title);
            ImageCacheManager.getInstance().displayImage(vh.combo_img, entry.img);

            if (entry.shipday != null) {
                vh.dis_cycle.setText(entry.shipday.length + "次/周");//"每周配送" +
                vh.times.setText("共" + entry.duration * entry.shipday.length + "次");
            }

            if (entry.weights != null) {
                vh.per_weight.setText(Utils.unitConversion(entry.weights[entry.index]) + "/次");//"每次配送" +
            }
            vh.farm_tv.setText("来自:" + entry.farm);
            Paint paint = vh.combo_mprice.getPaint();
            paint.setAntiAlias(true);//抗锯齿
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            vh.combo_mprice.setText(Utils.unitPeneyToYuan(entry.mprices[entry.index]));
            vh.combo_price.setText(Utils.unitPeneyToYuan(entry.prices[entry.index]));
        }

        return v;
    }

    class ViewHolder {
        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

        /**
         * 我的套餐
         */
        @Bind(R.id.my_combo)
        TextView my_combo;
        /**
         * 套餐名称
         */
        @Bind(R.id.combo_name)
        TextView combo_name;
        /**
         * 配送斤数例如每次6斤次
         */
        @Bind(R.id.per_weight)
        TextView per_weight;
        /**
         * 配送周期例如每周配送1次
         */
        @Bind(R.id.dis_cycle)
        TextView dis_cycle;
        /**
         * 4次/月
         */
        @Bind(R.id.times)
        TextView times;
        /**
         * 套餐价格
         */
        @Bind(R.id.combo_price)
        TextView combo_price;
        @Bind(R.id.combo_mprice)
        TextView combo_mprice;
        /**
         * 套餐图片
         */
        @Bind(R.id.combo_img)
        ImageView combo_img;
        @Bind(R.id.farm_tv)
        TextView farm_tv;
    }
}
