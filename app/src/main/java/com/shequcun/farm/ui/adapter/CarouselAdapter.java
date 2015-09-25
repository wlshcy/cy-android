package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.DeviceInfo;

import java.util.List;

/**
 * 轮播图Adapter
 * Created by apple on 15/8/4.
 */
public class CarouselAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SlidesEntry> mImageList;
    private View.OnClickListener onClick;
    private int width;

    public CarouselAdapter(Context context, List<SlidesEntry> list) {
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mImageList = list;
    }

    public void buildOnClick(View.OnClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public SlidesEntry getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SlidesEntry item = mImageList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.image_item, null);
            holder.coverView = (ImageView) convertView
                    .findViewById(R.id.imgView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(item.img)) {
            holder.coverView.setImageResource(R.drawable.icon_combo_default);
        } else {
            ImageCacheManager.getInstance().displayImage(holder.coverView , item.img+ "?imageView2/2/w/" + width);
        }
//        holder.coverView.setImageUrl(item.img, ImageCacheManager.getInstance()
//                .getImageLoader());


        holder.coverView.setTag(item);
        holder.coverView.setOnClickListener(onClick);
        return convertView;
    }

    class ViewHolder {
        public ImageView coverView;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
