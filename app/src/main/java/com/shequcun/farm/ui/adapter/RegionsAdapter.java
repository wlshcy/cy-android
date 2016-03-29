package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.shequcun.farm.R;
import com.lynp.R;
import com.shequcun.farm.data.RegionEntry;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by apple check_turn_on 15/7/20.
 */
public class RegionsAdapter extends ArrayAdapter<RegionEntry> {
    RegionEntry data;
    boolean isShowChild = true;//是否显示右边的箭头

    public RegionsAdapter(Context context) {
        super(context, R.layout.city_region_item_ly);
    }

    public void setIsShowChild(boolean isShowChild) {
        this.isShowChild = isShowChild;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        data = getItem(position);
        ViewHolder viewHolder;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.city_region_item_ly, null);
            viewHolder = new ViewHolder(v);
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
        @Bind(R.id.city_name)
        TextView city_name;
        @Bind(R.id.right_arrow_iv)
        ImageView right_arrow_iv;

        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }

}
