package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboParam;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;

/**
 * 二级套餐适配器
 * Created by apple on 15/8/13.
 */
public class ComboSubAdapter extends BaseAdapter {

    private Context mContext;
    //    private ComboEntry entry;
    private ArrayList<ComboParam> dataList;

    //    public ComboSubAdapter(Context context, ComboEntry entry) {
//        this.mContext = context;
//        this.entry = entry;
//    }
    public ComboSubAdapter(Context context, ArrayList<ComboParam> params) {
        this.mContext = context;
        this.dataList = params;
    }

    //    @Override
//    public int getCount() {
//        return entry == null || entry.weights == null ? 0 : entry.weights.length;
//    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    AvoidDoubleClickListener chooseDishes;

    public void setChooseDishesLsn(AvoidDoubleClickListener onClick) {
        this.chooseDishes = onClick;
    }

    //    @Override
//    public ComboEntry getItem(int position) {
//        return entry;
//    }
    @Override
    public ComboParam getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_item_ly, null);
            vh = new ViewHolder();
            vh.combo_img = (ImageView) view.findViewById(R.id.combo_img);
            vh.combo_name = (TextView) view.findViewById(R.id.combo_name);
            vh.distribution_circle = (TextView) view.findViewById(R.id.distribution_circle);
            vh.distribution_all_times = (TextView) view.findViewById(R.id.distribution_all_times);
            vh.total_price = (TextView) view.findViewById(R.id.total_price);
            vh.choose_dishes = view.findViewById(R.id.choose_dishes);
            vh.ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        ComboParam data = getItem(position);
        if (vh != null) {
            if (vh.combo_img != null)
                ImageCacheManager.getInstance().displayImage(vh.combo_img,data.getWimg());

            if (vh.combo_name != null) {
//                String splits[] = entry.title.split("套餐");
                String title = data.getTitle().replace("套餐","");
                vh.combo_name.setText(Utils.getSpanableSpan(title, data.getWeights()+"斤", "套餐", ResUtil.dipToPixel(mContext, 14), ResUtil.dipToPixel(mContext, 25)));
            }

            if (vh.distribution_circle != null) {
                vh.distribution_circle.setText("每周配送" + data.getShipday().length + "次");
            }

            if (vh.distribution_all_times != null) {
                if (data.getDuration()>= 52)
                    vh.distribution_all_times.setText(data.getDuration() * data.getShipday().length + "次/年");
                else
                    vh.distribution_all_times.setText(data.getDuration() * data.getShipday().length + "次/月");
            }

            if (vh.total_price != null)
                vh.total_price.setText("￥" + data.getTotalPrices() / 100);

            if (vh.choose_dishes != null) {
                vh.choose_dishes.setTag(data);
                vh.choose_dishes.setOnClickListener(chooseDishes);
            }
        }

//        if (vh != null) {
//            if (vh.combo_img != null)
//                ImageCacheManager.getInstance().displayImage(vh.combo_img, TextUtils.isEmpty(entry.wimgs[position]) ? entry.img : entry.wimgs[position]);
//
//            if (vh.combo_name != null) {
//                String splits[] = entry.title.split("套餐");
//                vh.combo_name.setText(Utils.getSpanableSpan(splits[0], entry.weights[position] + "", "套餐", ResUtil.dipToPixel(mContext, 14), ResUtil.dipToPixel(mContext, 25)));
//            }
//
//            if (vh.distribution_circle != null) {
//                vh.distribution_circle.setText("每周配送" + entry.shipday.length + "次");
//            }
//
//            if (vh.distribution_all_times != null) {
//                if (entry.duration >= 52)
//                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/年");
//                else
//                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/月");
//            }
//
//            if (vh.total_price != null)
//                vh.total_price.setText("￥" + (((double) entry.prices[position]) / 100));
//
//            if (vh.choose_dishes != null) {
//                entry.setPosition(position);
//                vh.choose_dishes.setTag(entry);
//                vh.choose_dishes.setOnClickListener(chooseDishes);
//            }
//        }


        return view;
    }


    void addChildToContainer(LinearLayout ll_container) {
        if (ll_container == null)
            return;
        View childView = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_child_item_ly, null);
        ll_container.addView(childView);
    }

    class ViewHolder {
        /**
         * 二级套餐图片
         */
        ImageView combo_img;
        TextView combo_name;
        /**
         * 每周配送次数
         */
        TextView distribution_circle;
        /**
         * 52次/年
         */
        TextView distribution_all_times;
        /**
         * 价格
         */
        TextView total_price;
        /**
         * 去选菜
         */
        View choose_dishes;

        LinearLayout ll_container;

    }
}
