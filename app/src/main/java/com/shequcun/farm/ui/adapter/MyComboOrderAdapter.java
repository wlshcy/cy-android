package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//import com.shequcun.farm.R;
import com.lynp.R;
import com.shequcun.farm.data.MyComboOrder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mac on 15/10/8.
 */
public class MyComboOrderAdapter extends ArrayAdapter<MyComboOrder> {
    public MyComboOrderAdapter(Context context) {
        super(context, R.layout.my_combo_order_item_ly);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_combo_order_item_ly, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        MyComboOrder data = getItem(position);
        if (data != null) {
            vh.my_combo_name.setText(data.title);
//            "status": 3  // 1.待配送, 2.配送中, 3.配送中, 5.套餐配送完成
            vh.my_combo_status.setVisibility(View.VISIBLE);
            if (data.status == 5) {
                vh.my_combo_status.setText("状态:套餐配送完成");
            } else if (data.status == 3) {
                vh.my_combo_status.setText("状态:进行中");
            } else if (data.status == 1) {
                vh.my_combo_status.setText("状态:未配送");
            } else if (data.status == 2) {
                vh.my_combo_status.setText("状态:进行中");
            } else {
                vh.my_combo_status.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.my_combo_name)
        TextView my_combo_name;
        @Bind(R.id.my_combo_status)
        TextView my_combo_status;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }
}
