package com.lynp.ui.adapter;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cong on 15/9/7.
 */
public class AddressListAdapter extends BaseAdapter {
    private ArrayList<AddressEntry> list = new ArrayList<AddressEntry>();
    private Context context;
    private boolean showDefaultIcon;
    private OnUpdateAddressListener onUpdateAddressListener;
    private OnChooseAddressListener onChooseAddressListener;

    public AddressListAdapter(Context context) {
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
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        AddressEntry entry = (AddressEntry) getItem(position);
        vh.name.setText(entry.name);
        vh.mobile.setText(entry.mobile);
        vh.address.setText(entry.city + entry.region + entry.address);
        if (showDefaultIcon) {
            if (entry.isDefault) {
                vh.choose.setImageResource(R.drawable.icon_choose);
            } else {
                vh.choose.setImageBitmap(null);
            }
        } else {
            vh.choose.setVisibility(View.GONE);
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
        @Bind(R.id.name_tv)
        TextView name;
        @Bind(R.id.mobile_tv)
        TextView mobile;
        @Bind(R.id.address_tv)
        TextView address;
        @Bind(R.id.update_address_tv)
        ImageView update;
        @Bind(R.id.address_rl)
        View addressRl;
        @Bind(R.id.choose_iv)
        ImageView choose;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
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
