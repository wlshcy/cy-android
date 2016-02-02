package com.lynp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nmg on 16/1/29.
 */
public class MineAdapter extends ArrayAdapter<MineAdapter.MyItem> {
    public String items[] = {"我的订单","地址管理","客服电话","设置"};
    public String tips[] = {"", "",Constrants.Customer_Service_Phone, ""};

    public MineAdapter(Context context) {
        super(context, R.layout.mine_item_ui);
        for(int i = 0; i<items.length; i++){
            add(new MyItem(items[i], tips[i]));
        }
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.mine_item_ui, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        MyItem myItem = getItem(position);
        vh.title.setPadding(ResUtil.dipToPixel(getContext(),30),0,0,0);
        vh.title.setText(myItem.name);
        vh.tips.setText(myItem.tip);
        return v;
    }

    static class MyItem {
        String name;
        String tip;

        public MyItem(String name, String tip) {
            this.name = name;
            this.tip = tip;
        }
    }

    class ViewHolder {
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.tips)
        TextView tips;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
