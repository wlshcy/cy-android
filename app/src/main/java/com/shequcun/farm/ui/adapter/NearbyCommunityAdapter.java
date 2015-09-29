package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.ZoneEntry;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 附近小区Adapter
 *
 * @author apple
 */
public class NearbyCommunityAdapter extends ArrayAdapter<ZoneEntry> {

    ZoneEntry data;

//    int bigFontSize, smallFontSize;

    boolean isShowDistWidget = true;

    public NearbyCommunityAdapter(Context mContext) {
        super(mContext, R.layout.nearby_community_item_ly);
//        bigFontSize = ResUtil.dip2px(mContext, 18);
//        smallFontSize = ResUtil.dip2px(mContext, 14);
    }

    public void setIsShowDistWidget(boolean isShowDistWidget) {
        this.isShowDistWidget = isShowDistWidget;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        data = getItem(position);
        ViewHolder viewHolder;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.nearby_community_item_ly, null);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.community_address.setText(data.address);

//        if (isShowDistWidget) {
//            SpannableString ss = CommonUtils.getSpanableSpan(data.name, "("
//                            + CommonUtils.convertMeterToKm(data.dist) + ")", bigFontSize,
//                    smallFontSize, 0xFF343434, 0xFF808080);
//            viewHolder.community_info.setText(ss);
//        } else {
//            viewHolder.community_info.setText(data.name);
//        }
        viewHolder.community_info.setText(data.name);
        if (data.dist > 0)
            viewHolder.dist.setText("(" + data.dist + "m)");

        return v;
    }

    class ViewHolder {
        @Bind(R.id.community_info)
        TextView community_info;
        @Bind(R.id.community_address)
        TextView community_address;
        @Bind(R.id.dist_tv)
        TextView dist;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
