package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.Constrants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/8/6.
 */
public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private String myValues[];
    private String verName;

    public MyAdapter(Context mContext, String myValues[]) {
        this.mContext = mContext;
        this.myValues = myValues;
    }


    public void setVerName(String verName) {
        this.verName = verName;
    }

    @Override
    public int getCount() {
        return myValues == null ? 0 : myValues.length;
    }

    @Override
    public String getItem(int position) {
        return myValues == null ? null : myValues[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
//        data = getItem(position);
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.my_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        String tip = getItem(position);
        vh.my_title.setText(tip);
        if (tip.equals("检查更新")) {
            vh.tel_tv.setText(verName);
        } else {
            if ("客服电话".equals(tip))
                vh.tel_tv.setText(Constrants.Customer_Service_Phone);
            else if ("我的优惠红包".equals(tip))
                vh.tel_tv.setText("下单抵用");
        }
        return v;
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
