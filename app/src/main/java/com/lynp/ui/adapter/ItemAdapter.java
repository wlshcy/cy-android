package com.lynp.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import com.lynp.R;

import com.lynp.ui.data.ItemEntry;
import com.lynp.ui.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nmg on 15/10/26.
 */
public class ItemAdapter extends ArrayAdapter<ItemEntry> {

    public ItemAdapter(Context context) {
        super(context, R.layout.item_cell);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_cell, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ItemEntry entry = getItem(position);
        if (entry != null && vh != null) {
            if (entry.photo != null ) {
                String url = entry.photo;
                InnerImageLoadingListener innerImageLoadingListener = new InnerImageLoadingListener(vh);
                ImageLoader.getInstance().displayImage(url, vh.photo, innerImageLoadingListener);
            } else {
                    /*不需要重新加载图片*/
            }
            vh.name.setText(entry.name);
            vh.size.setText(Utils.humanSize(entry.size));
            vh.price.setText(Utils.unitPeneyToYuan(entry.price));
            Paint paint = vh.mprice.getPaint();
            paint.setAntiAlias(true);//抗锯齿
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            vh.mprice.setText(Utils.unitPeneyToYuan(entry.mprice));
        }

        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.photo)
        ImageView photo;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.size)
        TextView size;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.mprice)
        TextView mprice;

        ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        String lastImageUrl;
    }

    class InnerImageLoadingListener implements ImageLoadingListener {
        private ViewHolder viewHolder;

        public InnerImageLoadingListener(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = imageUri;
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = null;
        }
    }
}
