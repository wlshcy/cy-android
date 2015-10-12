package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shequcun.farm.R;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选择菜品 Adapter
 * Created by apple on 15/8/10.
 */
public class ChooseDishesAdapter extends ArrayAdapter<DishesItemEntry> {

    DishesItemEntry entry;
    boolean enabled;

    public ChooseDishesAdapter(Context context) {
        super(context, R.layout.goods_item_ly);
    }

    public void buildOnClickLsn(boolean enabled, AvoidDoubleClickListener onGoodsImgLsn, View.OnClickListener onAddGoodsLsn, View.OnClickListener onSubGoodsLsn) {
        this.enabled = enabled;
        this.onGoodsImgLsn = onGoodsImgLsn;
        this.onAddGoodsLsn = onAddGoodsLsn;
        this.onSubGoodsLsn = onSubGoodsLsn;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.goods_item_ly, null);
            vh = new ViewHolder(v);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        entry = getItem(position);

        vh.goodsImg.setTag(position);
        vh.goodsImg.setOnClickListener(onGoodsImgLsn);
        vh.goodsAdd.setEnabled(enabled);
        vh.goodsAdd.setTag(position);
        vh.goodsAdd.setOnClickListener(onAddGoodsLsn);
        vh.goodsAdd.setContentDescription(String.valueOf(entry.id));
        vh.goodsSub.setTag(position);
        vh.goodsSub.setOnClickListener(onSubGoodsLsn);
        vh.goodsSub.setContentDescription(String.valueOf(entry.id));

        if (entry != null && entry.imgs != null && entry.imgs.length > 0) {
            String url = entry.imgs[0] + "?imageview2/2/w/180";
            if (vh.lastImageUrl == null || !vh.lastImageUrl.equals(url)
                    || vh.goodsImg.getDrawable() == null) {
                    /*刷新图片*/
                InnerImageLoadingListener innerImageLoadingListener = new InnerImageLoadingListener(vh);
                ImageLoader.getInstance().displayImage(url, vh.goodsImg, innerImageLoadingListener);
            } else {
                    /*不需要重新加载图片*/
            }
            vh.goodsName.setText(entry.title);
            vh.goodsPrice.setText(Utils.unitConversion(entry.packw) + "/份");
        }
        if (entry.getCount() > 0) {
            vh.goodsCount.setText(String.valueOf(entry.getCount()));
            vh.goodsCount.setVisibility(View.VISIBLE);
            vh.goodsSub.setVisibility(View.VISIBLE);
        } else {
            vh.goodsSub.setVisibility(View.GONE);
            vh.goodsCount.setText("0");
            vh.goodsCount.setVisibility(View.GONE);
        }
        return v;
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

    AvoidDoubleClickListener onGoodsImgLsn;
    View.OnClickListener onAddGoodsLsn;
    View.OnClickListener onSubGoodsLsn;

    class ViewHolder {
        @Bind(R.id.goods_img)
        ImageView goodsImg;
        @Bind(R.id.goods_name)
        TextView goodsName;
        @Bind(R.id.goods_price)
        TextView goodsPrice;
        @Bind(R.id.lookDtlLy)
        LinearLayout lookDtlLy;
        @Bind(R.id.goods_sub)
        ImageView goodsSub;
        @Bind(R.id.goods_count)
        TextView goodsCount;
        @Bind(R.id.goods_add)
        ImageView goodsAdd;

        String lastImageUrl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
