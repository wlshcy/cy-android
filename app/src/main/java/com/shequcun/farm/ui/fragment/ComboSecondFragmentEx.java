package com.shequcun.farm.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.ui.adapter.ComboSecondDlgPagerAdapter;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by mac on 15/10/10.
 */
public class ComboSecondFragmentEx extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_second_dlg_ly, container, false);
    }

    @Override
    protected void setWidgetLsn() {
        addViewsToList(buildComboEntry());
        viewpager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    protected void initWidget(View v) {
        aList = new ArrayList<>();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void addViewsToList(ComboEntry entry) {
        if (entry == null || entry.weights == null)
            return;
        m_Count = entry.weights.length;
        for (int i = 0; i < m_Count; ++i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.combo_second_item_ly, null);
            ImageView combo_img = (ImageView) v.findViewById(R.id.combo_img);

//                if (combo_img != null && entry.wimgs != null && entry.wimgs.length > 0) {
//                    String url = (TextUtils.isEmpty(entry.wimgs[i]) ? entry.img : entry.wimgs[i])+"?imageview2/2/w/180";
//                    if (vh.lastImageUrl == null || !vh.lastImageUrl.equals(url)
//                            || vh.combo_img.getDrawable() == null) {
//                        vh.imgProgress.setTag(position);
//                    /*刷新图片*/
//                        InnerImageLoadingListener innerImageLoadingListener = new InnerImageLoadingListener(vh.imgProgress,position);
//                        innerImageLoadingListener.setViewHolder(vh);
//                        ImageLoader.getInstance().displayImage(url, vh.combo_img, innerImageLoadingListener);
//                    } else {
//                    /*不需要重新加载图片*/
//                    }
//                }
            String url = (TextUtils.isEmpty(entry.wimgs[i]) ? entry.img : entry.wimgs[i]) + "?imageview2/2/w/180";
            ImageLoader.getInstance().displayImage(url, combo_img);
            TextView combo_name = (TextView) v.findViewById(R.id.combo_name);

            if (combo_name != null) {
                combo_name.setText(entry.title);
//                    String splits[] = entry.title.split("套餐");
//                    String midStr = Utils.unitConversion(entry.weights[position]).replace("斤", "");
//                    combo_name.setText(Utils.getSpanableSpan(splits[0] + " ", midStr, " 斤套餐", ResUtil.dipToPixel(mContext, 14), ResUtil.dipToPixel(mContext, 35)));
            }


            TextView distribution_circle = (TextView) v.findViewById(R.id.distribution_circle);

            if (distribution_circle != null) {
                distribution_circle.setText(entry.shipday.length + "次/周");
            }
            TextView distribution_all_times = (TextView) v.findViewById(R.id.distribution_all_times);

            if (distribution_all_times != null) {
                if (entry.duration >= 52)
                    distribution_all_times.setText(entry.duration * entry.shipday.length + "次/年");
                else if (entry.duration >= 12)
                    distribution_all_times.setText(entry.duration * entry.shipday.length + "次/季");
                else
                    distribution_all_times.setText(entry.duration * entry.shipday.length + "次/月");
            }
            TextView total_price = (TextView) v.findViewById(R.id.total_price);
            if (total_price != null)
                total_price.setText(Utils.unitPeneyToYuan(entry.prices[i]));//entry.mprices[position] -
            TextView market_price_tv = (TextView) v.findViewById(R.id.market_price_tv);
            if (market_price_tv != null) {
                Paint paint = market_price_tv.getPaint();
                paint.setAntiAlias(true);//抗锯齿
                paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                market_price_tv.setText(Utils.unitPeneyToYuan(entry.mprices[i]));
            }


            TextView distribution_weight = (TextView) v.findViewById(R.id.distribution_weight);
            String str = Utils.unitConversion(entry.weights[i]).replace("斤", "");
            distribution_weight.setText(Utils.getSpanableSpan(str, "斤", ResUtil.dipToPixel(getActivity(), 35), ResUtil.dipToPixel(getActivity(), 14), 0xFF31C27C, 0xFF31C27C));

//            if (vh.choose_dishes != null) {
//                vh.choose_dishes.setTag(position);
//                vh.choose_dishes.setOnClickListener(chooseDishes);
//            }

//                requestFixedCombo(entry.id, vh.ll_container);

            aList.add(v);
        }
        ComboSecondDlgPagerAdapter adapter = new ComboSecondDlgPagerAdapter(aList);
        viewpager.setAdapter(adapter);
        initPointLayout();
    }


    ComboEntry buildComboEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }

    private void initPointLayout() {
        try {
            int mPadding = ResUtil.dip2px(getActivity(), 3);
            m_PointImgs = new ImageView[m_Count];
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) point_ayout.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);

            int bottomMargin = ((DeviceInfo.getDeviceHeight(getActivity()) - ResUtil.dipToPixel(getActivity(), 424)) >> 1) * 2 / 3;
            params.bottomMargin = bottomMargin;

            point_ayout.setLayoutParams(params);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            for (int i = 0; i < m_Count; i++) {
                m_PointImgs[i] = new ImageView(getActivity());
                m_PointImgs[i].setLayoutParams(lp);
                m_PointImgs[i].setPadding(mPadding, mPadding, mPadding,
                        mPadding);
                m_PointImgs[i].setImageResource(R.drawable.icon_dot);
//                m_PointImgs[i].setEnabled(true);
//                m_PointImgs[i].setTag(i);
                point_ayout.addView(m_PointImgs[i], i);
            }
            m_CurrentItem = 0;
//            m_PointImgs[m_CurrentItem].setEnabled(false);
            m_PointImgs[m_CurrentItem]
                    .setImageResource(R.drawable.icon_dot_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setcurrentPoint(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void setcurrentPoint(int position) {
        try {
            for (int i = 0; i < m_Count; i++) {
                m_PointImgs[i].setImageResource(R.drawable.icon_dot);
            }
//            m_PointImgs[m_CurrentItem].setEnabled(true);
            m_PointImgs[position].setImageResource(R.drawable.icon_dot_selected);
//            m_PointImgs[position].setEnabled(false);
            m_CurrentItem = position;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    int m_Count;

    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.point_ayout)
    LinearLayout point_ayout;
    ArrayList<View> aList = null;
    private ImageView[] m_PointImgs;
    private int m_CurrentItem;
}
