package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cong on 15/10/22.
 */
public class MyMainAdapter extends ArrayAdapter<MyMainAdapter.MyItem> {
    public int imgIds[] = {R.drawable.icon_my_order, R.drawable.icon_my_delay, R.drawable.icon_my_red_packets, R.drawable.icon_my_phone_person, R.drawable.icon_my_address, R.drawable.icon_my_settings};
    public String itemNames[] = {"我的订单", "延期配送蔬菜", "我的优惠红包", "客服电话", "地址管理", "设置"};
    public String tips[] = {"", "", "下单抵用", Constrants.Customer_Service_Phone, "", ""};

    public MyMainAdapter(Context context) {
        super(context, R.layout.my_item_ly);
        for (int i = 0; i < imgIds.length; i++) {
            add(new MyItem(imgIds[i], itemNames[i], tips[i]));
        }
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.my_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        MyItem myItem = getItem(position);
        vh.my_title.setPadding(ResUtil.dipToPixel(getContext(),10),0,0,0);
        vh.my_title.setText(myItem.name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            vh.my_title.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(myItem.imgId), null, null, null);
        } else {
            vh.my_title.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(myItem.imgId), null, null, null);
        }
        vh.tel_tv.setText(myItem.tip);
        return v;
    }

    static class MyItem {
        int imgId;
        String name;
        String tip;

        public MyItem(int imgId, String name, String tip) {
            this.imgId = imgId;
            this.name = name;
            this.tip = tip;
        }
    }

    class ViewHolder {
        @Bind(R.id.my_title)
        TextView my_title;
        @Bind(R.id.tel_tv)
        TextView tel_tv;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
