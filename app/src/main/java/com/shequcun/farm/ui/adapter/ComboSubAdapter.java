package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.shequcun.farm.R;
import com.lynp.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.FixedListComboEntry;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.Utils;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 二级套餐适配器
 * Created by apple on 15/8/13.
 */
public class ComboSubAdapter extends BaseAdapter {
    private Context mContext;
    private ComboEntry entry;

    public ComboSubAdapter(Context context, ComboEntry entry) {
        this.mContext = context;
        this.entry = entry;
    }

    @Override
    public int getCount() {
        return entry == null || entry.weights == null ? 0 : entry.weights.length;
    }

    @Override
    public ComboEntry getItem(int position) {
        return entry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_item_ly, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        if (vh != null) {
            if (vh.combo_img != null && entry.wimgs != null && entry.wimgs.length > 0) {
                String url = (TextUtils.isEmpty(entry.wimgs[position]) ? entry.img : entry.wimgs[position]) + "?imageview2/2/w/180";
                if (vh.lastImageUrl == null || !vh.lastImageUrl.equals(url)
                        || vh.combo_img.getDrawable() == null) {
                    vh.imgProgress.setTag(position);
                    /*刷新图片*/
                    InnerImageLoadingListener innerImageLoadingListener = new InnerImageLoadingListener(vh.imgProgress, position);
                    innerImageLoadingListener.setViewHolder(vh);
                    ImageLoader.getInstance().displayImage(url, vh.combo_img, innerImageLoadingListener);
                } else {
                    /*不需要重新加载图片*/
                }
            }

            if (vh.combo_name != null) {
                String splits[] = entry.title.split("套餐");
                String midStr = Utils.unitConversion(entry.weights[position]).replace("斤", "");
                vh.combo_name.setText(Utils.getSpanableSpan(splits[0] + " ", midStr, " 斤套餐", ResUtil.dipToPixel(mContext, 14), ResUtil.dipToPixel(mContext, 35)));
            }

            if (vh.distribution_circle != null) {
                vh.distribution_circle.setText(entry.shipday.length + "次/周");
            }

            if (vh.distribution_all_times != null) {
//                if (entry.duration >= 52)
//                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/年");
//                else if (entry.duration >= 12)
//                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/季");
//                else
//                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/月");

                vh.distribution_all_times.setText("共" + entry.duration * entry.shipday.length + "次");
            }


            if (vh.total_price != null)
                vh.total_price.setText(Utils.unitPeneyToYuan(entry.prices[position]));//entry.mprices[position] -

            if (vh.market_price_tv != null) {
                Paint paint = vh.market_price_tv.getPaint();
                paint.setAntiAlias(true);//抗锯齿
                paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                vh.market_price_tv.setText(Utils.unitPeneyToYuan(entry.mprices[position]));
            }


//            if (vh.choose_dishes != null) {
//                vh.choose_dishes.setTag(position);
//                vh.choose_dishes.setOnClickListener(chooseDishes);
//            }

            requestFixedCombo(entry.id, vh.ll_container);
        }


        return view;
    }

    /**
     * 请求固定套餐
     *
     * @param ll_container
     */

    private void requestFixedCombo(int id, final LinearLayout ll_container) {
        if (ll_container.getChildCount() > 0)
            return;
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.getHttpClient(mContext).get(LocalParams.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    FixedListComboEntry entry = JsonUtilsParser.fromJson(new String(data), FixedListComboEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (entry.aList == null || entry.aList.size() <= 0)
                                return;
                            addChildToContainer(ll_container, entry.aList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {

            }
        });
    }

    void addChildToContainer(LinearLayout ll_container, List<FixedComboEntry> aList) {
        if (ll_container == null)
            return;
        ll_container.removeAllViews();

        int size = aList.size();

        for (int i = 0; i < size; i += 2) {
            View childView = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_child_item_ly, null);
//            CircleImageView additional_goods_img_1 = (CircleImageView) childView.findViewById(R.id.additional_goods_img_1);
//            additional_goods_img_1.setImageUrl(aList.get(i).img, ImageCacheManager.getInstance().getImageLoader());
            TextView additional_goods_name_1 = (TextView) childView.findViewById(R.id.additional_goods_name_1);
            additional_goods_name_1.setText(aList.get(i).title);
            TextView additional_send_weight_1 = (TextView) childView.findViewById(R.id.additional_send_weight_1);
            additional_send_weight_1.setText(aList.get(i).quantity + aList.get(i).unit + "/" + aList.get(i).freq + "周");

            childView.findViewById(R.id.fix_ly_2).setVisibility(View.GONE);

            if (i + 1 < size) {
                childView.findViewById(R.id.fix_ly_2).setVisibility(View.VISIBLE);
//                CircleImageView additional_goods_img_2 = (CircleImageView) childView.findViewById(R.id.additional_goods_img_2);
//                additional_goods_img_2.setImageUrl(aList.get(i + 1).img, ImageCacheManager.getInstance().getImageLoader());
                TextView additional_goods_name_2 = (TextView) childView.findViewById(R.id.additional_goods_name_2);
                additional_goods_name_2.setText(aList.get(i + 1).title);

                TextView additional_send_weight_2 = (TextView) childView.findViewById(R.id.additional_send_weight_2);
                additional_send_weight_2.setText(aList.get(i + 1).quantity + aList.get(i + 1).unit + "/" + aList.get(i + 1).freq + "周");
            }
            ll_container.addView(childView);
        }


//        View childViewex = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_child_item_ly, null);
//        ll_container.addView(childViewex);
    }

    class ViewHolder {
        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

        /**
         * 二级套餐图片
         */
        @Bind(R.id.combo_img)
        ImageView combo_img;
        @Bind(R.id.imgProgress)
        View imgProgress;
        @Bind(R.id.combo_name)
        TextView combo_name;
        /**
         * 每周配送次数
         */
        @Bind(R.id.distribution_circle)
        TextView distribution_circle;
        /**
         * 52次/年
         */
        @Bind(R.id.distribution_all_times)
        TextView distribution_all_times;
        /**
         * 价格
         */
        @Bind(R.id.total_price)
        TextView total_price;
        /**
         * 去选菜
         */
        @Bind(R.id.choose_dishes)
        View choose_dishes;
        @Bind(R.id.market_price_tv)
        TextView market_price_tv;
        @Bind(R.id.ll_container)
        LinearLayout ll_container;
        String lastImageUrl;
    }

    class InnerImageLoadingListener implements ImageLoadingListener {
        private View imgProgress;
        private int position;
        private ViewHolder viewHolder;

        public InnerImageLoadingListener(View imgProgress, int position) {
            this.imgProgress = imgProgress;
            this.position = position;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            setProgress(true);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            setProgress(false);
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            setProgress(false);
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = imageUri;
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            setProgress(false);
            if (viewHolder != null)
                this.viewHolder.lastImageUrl = null;
        }

        private void setProgress(boolean visible) {
            if (imgProgress.getTag() == null) return;
            if (!(imgProgress.getTag() instanceof Integer)) return;
            if ((int) imgProgress.getTag() != position) return;
            /*表明已经加载完毕*/
            if (imgProgress == null) return;
            if (visible)
                imgProgress.setVisibility(View.VISIBLE);
            else
                imgProgress.setVisibility(View.GONE);

        }

        public void setViewHolder(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }
}
