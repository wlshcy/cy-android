package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.RedPacketsEntry;

import java.util.ArrayList;

/**
 * Created by cong on 15/9/7.
 */
public class MyAddressAdapter extends BaseAdapter {
    private ArrayList<AddressEntry> list = new ArrayList<AddressEntry>();
    private Context context;
    private boolean showDefaultIcon;
    private OnUpdateAddressListener onUpdateAddressListener;
    private OnChooseAddressListener onChooseAddressListener;

    public MyAddressAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_address, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.name_tv);
            vh.mobile = (TextView) convertView.findViewById(R.id.mobile_tv);
            vh.address = (TextView) convertView.findViewById(R.id.address_tv);
            vh.update = (TextView) convertView.findViewById(R.id.update_address_tv);
            vh.addressRl = convertView.findViewById(R.id.address_rl);
            vh.choose = (ImageView) convertView.findViewById(R.id.choose_iv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        AddressEntry entry = (AddressEntry) getItem(position);
        vh.name.setText(entry.name);
        vh.mobile.setText(entry.mobile);
        vh.address.setText(entry.city + entry.region + entry.zname + entry.bur);
        if (entry.isDefault && showDefaultIcon) {
            vh.choose.setImageResource(R.drawable.icon_choose);
        }else {
            if (!showDefaultIcon){
                vh.choose.setVisibility(View.GONE);
            }
        }
        vh.update.setTag(entry);
        vh.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUpdateAddressListener != null)
                    onUpdateAddressListener.onUpdate((AddressEntry) v.getTag());
            }
        });
        vh.addressRl.setTag(entry);
        vh.addressRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChooseAddressListener != null)
                    onChooseAddressListener.onChoose((AddressEntry) v.getTag());
            }
        });
        return convertView;
    }

    public void addAll(ArrayList<AddressEntry> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        this.list.remove(pos);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name;
        TextView mobile;
        TextView address;
        TextView update;
        View addressRl;
        ImageView choose;
    }

    public void setShowDefaultIcon(boolean showDefaultIcon) {
        this.showDefaultIcon = showDefaultIcon;
    }

    public interface OnUpdateAddressListener {
        void onUpdate(AddressEntry entry);
    }

    public interface OnChooseAddressListener {
        void onChoose(AddressEntry entry);
    }

    public void setOnUpdateAddressListener(OnUpdateAddressListener onUpdateAddressListener) {
        this.onUpdateAddressListener = onUpdateAddressListener;
    }

    public void setOnChooseAddressListener(OnChooseAddressListener onChooseAddressListener) {
        this.onChooseAddressListener = onChooseAddressListener;
    }
}
