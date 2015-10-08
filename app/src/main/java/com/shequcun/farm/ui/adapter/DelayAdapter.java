package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.DelayItemEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Util;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cong on 15/10/8.
 */
public class DelayAdapter extends BaseAdapter {
    private List<DelayItemEntry> dataList = new ArrayList<>();
    private Context context;
    private DelayClick delayClick;

    public DelayAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_delay, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DelayItemEntry entry = (DelayItemEntry) getItem(position);
        viewHolder.titleTv.setText(entry.title);
        viewHolder.indexTv.setText("我的套餐"+position+1);
        viewHolder.delayTv.setText("延期");
        //已经选过菜，不能延期
        if (entry.chosen) {
            viewHolder.descTv.setText(R.string.delay_desc1);
            viewHolder.delayTv.setText(R.string.btn_can_not_delay_a_week_delivery);
            viewHolder.delayTv.setBackgroundResource(R.drawable.btn_bg_gray_selector);
            viewHolder.delayTv.setEnabled(false);
        } else {
            //已经延期过，不能在延期
            if (entry.delay != null) {
                viewHolder.descTv.setText("下次配送时间" + Utils.getMMdd(entry.delay.date) + ",请在配送日24小时前选菜");
                viewHolder.delayTv.setText(R.string.btn_has_delaied_a_week_delivery);
                viewHolder.delayTv.setBackgroundResource(R.drawable.btn_bg_gray_selector);
                viewHolder.delayTv.setEnabled(false);
                //表示可以延期
            } else {
                viewHolder.descTv.setText(R.string.delay_desc2);
                viewHolder.delayTv.setText(R.string.btn_delay_a_week_delivery);
                viewHolder.delayTv.setBackgroundResource(R.drawable.btn_bg_red_selector);
                viewHolder.delayTv.setEnabled(true);
            }
        }

        viewHolder.delayTv.setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                if (delayClick != null)
                    delayClick.onDelay(entry);
            }
        });
        return convertView;
    }

    public void add(List<DelayItemEntry> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.titleTv)
        TextView titleTv;
        @Bind(R.id.delayTv)
        TextView delayTv;
        @Bind(R.id.descTv)
        TextView descTv;
        @Bind(R.id.indexTv)
        TextView indexTv;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface DelayClick {
        public void onDelay(DelayItemEntry entry);
    }

    public void setDelayClick(DelayClick delayClick) {
        this.delayClick = delayClick;
    }
}
