package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Utils;

/**
 * 购买订单
 * Created by apple on 15/8/20.
 */
public class ShoppingOrderAdapter extends ArrayAdapter<HistoryOrderEntry> {
    AvoidDoubleClickListener payOnClickLsn;

    public ShoppingOrderAdapter(Context context) {
        super(context, R.layout.my_order_item_ly);
    }

    public void buildPayOnClickLsn(AvoidDoubleClickListener payOnClickLsn) {
        this.payOnClickLsn = payOnClickLsn;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh = null;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(getContext()).inflate(R.layout.my_order_item_ly, null);
            vh.distribution_number_tv = (TextView) v.findViewById(R.id.distribution_number_tv);
            vh.distribution_date = (TextView) v.findViewById(R.id.distribution_date);
            vh.distribution_name = (TextView) v.findViewById(R.id.distribution_name);
            vh.order_status = (TextView) v.findViewById(R.id.order_status);
//            vh.order_status_ly = v.findViewById(R.id.order_status_ly);
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
//                vh.order_status.setText("已配送");
//                vh.order_status_ly.setTag(position);
//                vh.order_status_ly.setOnClickListener(payOnClickLsn);
                vh.order_status.setText("配送完成");
            } else if (entry.status == 0) {
//                vh.order_status_ly.setTag(position);
//                vh.order_status_ly.setOnClickListener(payOnClickLsn);
                vh.order_status.setText("去付款");
            } else if (entry.status == 1) {
//                vh.order_status_ly.setTag(position);
//                vh.order_status_ly.setOnClickListener(payOnClickLsn);
                vh.order_status.setText("未配送");
            } else if (entry.status == 2) {
                vh.order_status.setText("配送中");
            } else if (entry.status == 4) {
                vh.order_status.setText("订单取消");
            }

            if (entry.status == 0) {
                entry.date = "下单日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
                vh.distribution_date.setText(entry.date);
            } else if (entry.status == 1) {
                entry.date = "支付日期:" + Utils.getTime(entry.json.get(entry.status + "").getAsLong());
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
        }


//        vh.order_status.setBackgroundResource(R.drawable.gray_f0f0f0_corner_bg);
//        vh.order_status.setBackgroundResource(R.drawable.green_94d6c0_corner_bg);

        return v;
    }

    class ViewHolder {
        /**
         * 选品次数
         */
        TextView distribution_number_tv;
        /**
         * 配送日期
         */
        TextView distribution_date;
        /**
         * 套餐名称
         */
        TextView distribution_name;
        /**
         * 订单状态
         */
        TextView order_status;

//        View order_status_ly;
    }
}
