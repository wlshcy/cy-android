package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/8.
 */
public class MyOrderAdapter extends ArrayAdapter<T> {

    private Context mContext;

    public MyOrderAdapter(Context context) {
        super(context, R.layout.my_order_item_ly);
        this.mContext=context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh = null;
        if (v == null) {
            vh = new ViewHolder();
            v = LayoutInflater.from(mContext).inflate(R.layout.my_order_item_ly, null);
            vh.distribution_number_tv = (TextView) v.findViewById(R.id.distribution_number_tv);
            vh.distribution_date = (TextView) v.findViewById(R.id.distribution_date);
            vh.distribution_name = (TextView) v.findViewById(R.id.distribution_name);
            vh.order_status = (TextView) v.findViewById(R.id.order_status);
        } else {
            vh = (ViewHolder) v.getTag();
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
    }
}
