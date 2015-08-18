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

/**
 * Created by apple on 15/8/8.
 */
public class MyOrderAdapter extends ArrayAdapter<HistoryOrderEntry> {
    HistoryOrderEntry entry;

    public MyOrderAdapter(Context context) {
        super(context, R.layout.my_order_item_ly);
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
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        entry = getItem(position);

        if (entry != null) {
            vh.distribution_number_tv.setText("第" + entry.times + "次配送");
//            if (entry.status == 3){
//
//            }
//                //vh.distribution_date.setText("下单日期" + Utils.getTime(entry.modified));
//            else {
//               // vh.distribution_date.setText("配送日期" + Utils.getTime(entry.modified));
//            }

            if (entry.status == 0) {
//                vh.distribution_date.setText("下单日期" + Utils.getTime(entry.modified));
                vh.distribution_date.setText("下单日期  " + Utils.getTime(entry.json.get(entry.status + "").getAsLong()));
            } else if (entry.status == 1) {
                vh.distribution_date.setText("支付日期  " + Utils.getTime(entry.json.get(entry.status + "").getAsLong()));
            } else if (entry.status == 2) {
                vh.distribution_date.setText("配送日期" + Utils.getTime(entry.json.get(entry.status + "").getAsLong()));
            } else if (entry.status == 3) {
                vh.distribution_date.setText("收货日期" + Utils.getTime(entry.json.get(entry.status + "").getAsLong()));
            }

            vh.distribution_name.setText(entry.title);

            if (entry.status == 3) {
                vh.order_status.setText("已配送");
            } else {
                vh.order_status.setText("未配送");
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
    }
}
