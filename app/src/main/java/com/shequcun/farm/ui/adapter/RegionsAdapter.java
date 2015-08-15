package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.RegionEntry;


/**
 * Created by apple check_turn_on 15/7/20.
 */
public class RegionsAdapter extends ArrayAdapter<RegionEntry> {
    private Context mContext;
    RegionEntry data;
    boolean isShowChild = true;//是否显示右边的箭头

    public RegionsAdapter(Context context) {
        super(context, R.layout.city_region_item_ly);
        mContext = context;
    }

    public void setIsShowChild(boolean isShowChild) {
        this.isShowChild = isShowChild;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        data = getItem(position);
        ViewHolder viewHolder;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(
                    R.layout.city_region_item_ly, null);
            viewHolder = new ViewHolder();
            viewHolder.city_name = (TextView) v.findViewById(R.id.city_name);
            viewHolder.right_arrow_iv = (ImageView) v.findViewById(R.id.right_arrow_iv);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        int visibility = data.isleaf ? View.GONE : View.VISIBLE;
        visibility = isShowChild ? visibility : View.GONE;
        viewHolder.right_arrow_iv.setVisibility(visibility);
        viewHolder.city_name.setText(data.name);
        return v;
    }

    class ViewHolder {
        TextView city_name;
        ImageView right_arrow_iv;
    }

}
