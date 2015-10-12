package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shequcun.farm.R;
import com.shequcun.farm.data.SlidesEntry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 轮播图Adapter
 * Created by apple on 15/8/4.
 */
public class CarouselAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SlidesEntry> mImageList;
    private View.OnClickListener onClick;
    private int width;
    private ImageLoaderListener imageLoaderListener;
    private int curVisibleIndex;

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
            holder = new ViewHolder(convertView);
            convertView = mInflater.inflate(R.layout.image_item, null);
            holder.imgView = (ImageView) convertView
                    .findViewById(R.id.imgView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(item.img)) {
            holder.imgView.setImageResource(R.drawable.icon_combo_default);
        } else {
            ImageLoader.getInstance().displayImage(item.img + "?imageView2/2/w/" + width, holder.imgView, new InnerImageLoadingListener(position));
        }
        holder.imgView.setTag(item);
        holder.imgView.setOnClickListener(onClick);
        return convertView;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setCurVisibleIndex(int curVisibleIndex) {
        this.curVisibleIndex = curVisibleIndex;
    }

    public interface ImageLoaderListener {
        void loadFinish();

        void loadStart();
    }

    public void setImageLoaderListener(ImageLoaderListener imageLoaderListener) {
        this.imageLoaderListener = imageLoaderListener;
    }

    class InnerImageLoadingListener implements ImageLoadingListener {
        private int position;

        public InnerImageLoadingListener(int position) {
            this.position = position;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if (curVisibleIndex == position)
                if (imageLoaderListener != null)
                    imageLoaderListener.loadStart();
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            loadFinish(view, imageUri);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            loadFinish(view, imageUri);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            loadFinish(view, imageUri);
        }

        private void loadFinish(View view, String uri) {
            /*表明已经加载完毕*/
            view.setTag(uri);
            if (curVisibleIndex == position)
                if (imageLoaderListener != null)
                    imageLoaderListener.loadFinish();
        }
    }

    static class ViewHolder {
        @Bind(R.id.imgView)
        ImageView imgView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
