package com.shequcun.farm.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitmap.cache.ImageCacheManager;
import com.common.widget.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.FixedListComboEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.Utils;

import java.util.List;

import org.apache.http.Header;

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

    AvoidDoubleClickListener chooseDishes;

    public void setChooseDishesLsn(AvoidDoubleClickListener onClick) {
        this.chooseDishes = onClick;
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
            vh = new ViewHolder();
            vh.combo_img = (ImageView) view.findViewById(R.id.combo_img);
            vh.combo_name = (TextView) view.findViewById(R.id.combo_name);
            vh.distribution_circle = (TextView) view.findViewById(R.id.distribution_circle);
            vh.distribution_all_times = (TextView) view.findViewById(R.id.distribution_all_times);
            vh.total_price = (TextView) view.findViewById(R.id.total_price);
            vh.choose_dishes = view.findViewById(R.id.choose_dishes);
            vh.ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        if (vh != null) {
            if (vh.combo_img != null)
                ImageCacheManager.getInstance().displayImage(vh.combo_img, TextUtils.isEmpty(entry.wimgs[position]) ? entry.img : entry.wimgs[position]);

            if (vh.combo_name != null) {
                String splits[] = entry.title.split("套餐");
                vh.combo_name.setText(Utils.getSpanableSpan(splits[0], Utils.unitConversion(entry.weights[position]), "套餐", ResUtil.dipToPixel(mContext, 14), ResUtil.dipToPixel(mContext, 25)));
            }

            if (vh.distribution_circle != null) {
                vh.distribution_circle.setText("每周配送" + entry.shipday.length + "次");
            }

            if (vh.distribution_all_times != null) {
                if (entry.duration >= 52)
                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/年");
                else
                    vh.distribution_all_times.setText(entry.duration * entry.shipday.length + "次/月");
            }


            if (vh.total_price != null)
                vh.total_price.setText("￥" + (((double) entry.prices[position]) / 100));

            if (vh.choose_dishes != null) {
                vh.choose_dishes.setTag(entry);
                vh.choose_dishes.setOnClickListener(chooseDishes);
            }

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
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {
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
        int size = aList.size();

        for (int i = 0; i < size; i += 2) {
            View childView = LayoutInflater.from(mContext).inflate(R.layout.combo_sub_child_item_ly, null);
            CircleImageView additional_goods_img_1 = (CircleImageView) childView.findViewById(R.id.additional_goods_img_1);
            additional_goods_img_1.setImageUrl(aList.get(i).img, ImageCacheManager.getInstance().getImageLoader());
            TextView additional_goods_name_1 = (TextView) childView.findViewById(R.id.additional_goods_name_1);
            additional_goods_name_1.setText(aList.get(i).title);
            TextView additional_send_weight_1 = (TextView) childView.findViewById(R.id.additional_send_weight_1);
            additional_send_weight_1.setText(aList.get(i).quantity + aList.get(i).unit + "/" + aList.get(i).freq + "周");


            if (i + 1 < size) {
                CircleImageView additional_goods_img_2 = (CircleImageView) childView.findViewById(R.id.additional_goods_img_2);
                additional_goods_img_2.setImageUrl(aList.get(i + 1).img, ImageCacheManager.getInstance().getImageLoader());
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
        /**
         * 二级套餐图片
         */
        ImageView combo_img;
        TextView combo_name;
        /**
         * 每周配送次数
         */
        TextView distribution_circle;
        /***
         * 52次/年
         */
        TextView distribution_all_times;
        /**
         * 价格
         */
        TextView total_price;
        /**
         * 去选菜
         */
        View choose_dishes;

        LinearLayout ll_container;

    }
}
