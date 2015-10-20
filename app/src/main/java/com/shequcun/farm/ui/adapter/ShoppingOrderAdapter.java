package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 购买订单
 * Created by apple on 15/8/20.
 */
public class ShoppingOrderAdapter extends ArrayAdapter<HistoryOrderEntry> {

    public ShoppingOrderAdapter(Context context) {
        super(context, R.layout.my_order_item_ly);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh = null;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.my_order_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        HistoryOrderEntry entry = getItem(position);

        if (entry != null) {
            vh.distribution_date.setVisibility(View.VISIBLE);
            vh.distribution_number_tv.setText(entry.title);
            vh.distribution_name.setVisibility(View.GONE);
            if (entry.status == 3) {// 0.未付款, 1.待配送, 2.配送中, 3.配送完成,
                vh.order_status.setText("配送完成");
                vh.order_status.setBackgroundResource(R.drawable.gray_d1d1d1_corner_bg);
            } else if (entry.status == 0) {
                vh.order_status.setText(R.string.unpaid);
                vh.order_status.setBackgroundResource(R.drawable.red_f36043_corner_bg);
            } else if (entry.status == 1) {
                vh.order_status.setText("未配送");
                vh.order_status.setBackgroundResource(R.drawable.green_94d6c0_corner_bg);
            } else if (entry.status == 2) {
                vh.order_status.setText("配送中");
                vh.order_status.setBackgroundResource(R.drawable.green_94d6c0_corner_bg);
            } else if (entry.status == 4) {
                vh.order_status.setText("订单取消");
                vh.order_status.setBackgroundResource(R.drawable.gray_d1d1d1_corner_bg);
            }

            if (entry.status == 0) {
//                entry.date = "下单日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
                vh.distribution_date.setText("下单日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong()));
            } else if (entry.status == 1) {
                entry.date = "付款日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
                vh.distribution_date.setText(entry.date);
            } else if (entry.status == 2) {
                entry.date = "配送日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
                vh.distribution_date.setText(entry.date);
            } else if (entry.status == 3) {
                entry.date = "收货日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
                vh.distribution_date.setText(entry.date);
            } else {
                vh.distribution_date.setVisibility(View.GONE);
            }
//            if (entry.status != 0)
            entry.placeAnOrderDate = "下单日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());


        }
        return v;
    }

    class ViewHolder {
        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

        /**
         * 选品次数
         */
        @Bind(R.id.distribution_number_tv)
        TextView distribution_number_tv;
        /**
         * 配送日期
         */
        @Bind(R.id.distribution_date)
        TextView distribution_date;
        /**
         * 套餐名称
         */
        @Bind(R.id.distribution_name)
        TextView distribution_name;
        /**
         * 订单状态
         */
        @Bind(R.id.order_status)
        TextView order_status;
    }
}
