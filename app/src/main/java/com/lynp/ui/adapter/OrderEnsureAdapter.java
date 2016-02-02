package com.lynp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.lynp.ui.data.ItemDetailEntry;
import com.shequcun.farm.util.Utils;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nmg on 16/1/31.
 */
public class OrderEnsureAdapter extends ArrayAdapter<ItemDetailEntry> {
    public OrderEnsureAdapter(Context context) {
        super(context, R.layout.order_ensure_item);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_ensure_item, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        ItemDetailEntry entry = getItem(position);

        vh.name.setText(entry.name);
        vh.price.setText(Utils.unitPeneyToYuan(entry.price));
        vh.count.setText(String.valueOf(entry.count));

        return v;
    }

    class ViewHolder {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.count)
        TextView count;

        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
