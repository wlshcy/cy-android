package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.model.PhotoModel;
import com.shequcun.farm.ui.adapter.FarmSpecialtyShopCartAdapter;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 农庄特产购物车
 * Created by mac on 15/9/7.
 */
public class FarmSpecialtyShoppingCartFragment extends BaseFragment implements RemarkFragment.CallBackLsn {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.farm_specialty_shopping_cart_ly, container, false);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        v.findViewById(R.id.back).setVisibility(View.GONE);
    }

    @Override
    protected void setWidgetLsn() {
        title_center_text.setText(R.string.shop_cart);
        rightTv.setText(R.string.consultation);
        addWidgetToView();
        doRegisterRefreshBrodcast();
    }

    @OnClick(R.id.title_right_text)
    void doClick() {
        ConsultationDlg.showCallTelDlg(getActivity());
    }

    void addWidgetToView() {
        removeWidgetFromView();
        if (isLogin()) {
            RecommendEntry[] rEntryArray = new CacheManager(getActivity()).getRecommendFromDisk();
            if (rEntryArray != null && rEntryArray.length > 0) {
                buildAdapter();
                addDataToAdapter(rEntryArray);
                return;
            } else {
                resetWidgetStatus();
            }
            shopCartView = LayoutInflater.from(getActivity()).inflate(R.layout.farm_shopping_cart_widget_ly, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            pView.addView(shopCartView, params);
        } else {
            resetWidgetStatus();
            noLoginView = LayoutInflater.from(getActivity()).inflate(R.layout.no_login_ly, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            pView.addView(noLoginView, params);
            noLoginView.findViewById(R.id.go_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
                }
            });
        }
    }


    void resetWidgetStatus() {
        if (adapter != null) {
            adapter.clear();
        }
        if (footerView != null) {
            mLv.removeFooterView(footerView);
            footerView = null;
        }

        if (headView != null) {
            mLv.removeHeaderView(headView);
            headView = null;
        }
        if (sChilidView != null) {
            pView.removeView(sChilidView);
            sChilidView = null;
        }
        memo = null;
    }

    void addFooter(final int part) {
        if (footerView == null) {
            footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
            number_copies = (TextView) footerView.findViewById(R.id.number_copies);
            mLv.addFooterView(footerView, null, false);
            addSchildView();
        }
        updateWidget(part, allMoney);
    }

    void removeWidgetFromView() {
        if (shopCartView != null) {
            pView.removeView(shopCartView);
            shopCartView = null;
        }

        if (noLoginView != null) {
            pView.removeView(noLoginView);
            noLoginView = null;
        }
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getActivity()).getUserLoginEntry() != null;
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new FarmSpecialtyShopCartAdapter(getActivity());
        adapter.clear();
        adapter.buildOnClickLsn(onLookDtlLsn, onGoodsImgLsn, onAddGoodsLsn, onSubGoodsLsn);
        mLv.setAdapter(adapter);
        mLv.setExpanded(true);
    }

    View.OnClickListener onLookDtlLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (adapter == null || position >= adapter.getCount() || adapter.getItem(position) == null)
                return;
            RecommendEntry entry = adapter.getItem(position);
            entry.isShowDtlFooter = true;
//            gotoFragmentByAnimation(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName(), R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailViewPagerFragment(), FarmSpecialtyDetailViewPagerFragment.class.getName());
        }
    };

    Bundle buildBundle(RecommendEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecommentEntry", entry);
        return bundle;
    }

    void addDataToAdapter(RecommendEntry[] rEntryArray) {
        allPart = 0;
        allMoney = 0;
        cEntry = null;

        for (int i = 0; i < rEntryArray.length; ++i) {
            allPart += rEntryArray[i].count;
            allMoney += rEntryArray[i].count * rEntryArray[i].price;
        }
        adapter.addAll(rEntryArray);
        adapter.notifyDataSetChanged();
        addHeader();
        addFooter(allPart);
        pScrollView.fullScroll(View.FOCUS_UP);
    }

    View.OnClickListener onGoodsImgLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
            for (int i = 0; i < adapter.getItem(position).imgs.length; ++i) {
                photos.add(new PhotoModel(true, adapter.getItem(position).imgs[i]));
            }
            Bundle budle = new Bundle();
            budle.putSerializable(BrowseImageFragment.KEY_PHOTOS, photos);
            budle.putInt(BrowseImageFragment.KEY_INDEX, position);
            gotoFragmentByAnimation(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName(), R.anim.puff_in, R.anim.puff_out);
        }
    };


    View.OnClickListener onAddGoodsLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            updateListItemContent((int) v.getTag(), true);
        }
    };
    View.OnClickListener onSubGoodsLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            updateListItemContent((int) v.getTag(), false);
        }
    };

    void updateListItemContent(int position, boolean isAdd) {
        if (position >= adapter.getCount() || adapter.getItem(position) == null)
            return;
        View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount()
                - mLv.getFirstVisiblePosition());
        if (pView == null)
            return;
        TextView tvCount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
        if (tvCount.getVisibility() == View.GONE) {
            tvCount.setVisibility(View.VISIBLE);
        }
        ImageView ivDown = (ImageView) pView.findViewById(R.id.goods_sub);
        if (ivDown.getVisibility() == View.GONE) {
            ivDown.setVisibility(View.VISIBLE);
        }
        int count = Integer.parseInt(tvCount.getText().toString());
        int intCount = isAdd ? ++count : --count;
        RecommendEntry goodItem = adapter.getItem(position);
        goodItem.count = intCount;
        RecommendItemKey zItem = new RecommendItemKey();
        zItem.object = goodItem;
        if (intCount == 0) {
            adapter.remove(goodItem);
            adapter.notifyDataSetChanged();
            ivDown.setVisibility(View.GONE);
            tvCount.setVisibility(View.GONE);
            new CacheManager(getActivity()).delRecommendItemToDisk(zItem);
            if (adapter.getCount() < 1) {
                addWidgetToView();
            }
        } else {
            new CacheManager(getActivity()).saveRecommendToDisk(zItem);
        }
        allMoney = isAdd ? allMoney + goodItem.price : allMoney - goodItem.price;
        updateWidget(isAdd ? ++allPart : --allPart, allMoney);
        tvCount.setText(String.valueOf(intCount));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }

    void updateWidget(int part, int allMoney) {
        if (number_copies == null || shop_cart_total_price_tv == null || freight_money_tv == null)
            return;
        number_copies.setText("共" + part + "份");
        boolean isAddFreight = allMoney / 100 >= 99;
        allMoney = isAddFreight ? allMoney : allMoney + 1000;
        Spannable spannable = Utils.getSpanableSpan("共付:", Utils.unitPeneyToYuan(allMoney), ResUtil.dip2px(getActivity(), 14), ResUtil.dip2px(getActivity(), 14), getResources().getColor(R.color.gray_a9a9a9), getResources().getColor(R.color.red_f36043));
        shop_cart_total_price_tv.setText(spannable);
        freight_money_tv.setText(isAddFreight ? R.string.no_freight : R.string.freight_money);
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_FARM_SHOPPING_CART_MSG);
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }

                if (action.equals(IntentUtil.UPDATE_FARM_SHOPPING_CART_MSG)) {
                    addWidgetToView();
                }
            } catch (Exception e) {

            }

        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }


    /**
     * 添加备注优惠红包至界面
     */
    void addSchildView() {
        if (sChilidView == null) {
            sChilidView = LayoutInflater.from(getActivity()).inflate(R.layout.farm_shopping_cart_footer_ly, null);
            shop_cart_total_price_tv = (TextView) sChilidView.findViewById(R.id.shop_cart_total_price_tv);
            (sChilidView.findViewById(R.id.shop_cart_surpport_now_pay_tv)).setVisibility(View.GONE);

            sChilidView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ComboEntry entry = new ComboEntry();
                    entry.setPosition(0);
                    entry.prices = new int[1];
                    entry.prices[0] = allMoney / 100 >= 99 ? allMoney : allMoney + 10 * 10 * 10;
                    entry.info = new OtherInfo();
                    entry.info.extras = getExtras();
                    entry.info.memo = memo;
                    entry.info.type = 3;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ComboEntry", entry);
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                }
            });

            sChilidView.findViewById(R.id.remark_ly).setOnClickListener(new View.OnClickListener() {//添加备注
                @Override
                public void onClick(View v) {
                    RemarkFragment fragment = new RemarkFragment();
                    fragment.setCallBackLsn(FarmSpecialtyShoppingCartFragment.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("RemarkTip", remark_tv != null ? remark_tv.getText().toString() : "");
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, fragment, RemarkFragment.class.getName());
                }
            });


            remark_tv = (TextView) sChilidView.findViewById(R.id.remark_tv);
            freight_money_tv = (TextView) sChilidView.findViewById(R.id.freight_money_tv);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = ResUtil.dipToPixel(getActivity(), 5);
            pView.addView(sChilidView, params);
        }
    }

    public String getExtras() {
        String result = "";
        RecommendEntry[] rEntryArray = new CacheManager(getActivity()).getRecommendFromDisk();
        if (rEntryArray != null && rEntryArray.length > 0) {
            for (int i = 0; i < rEntryArray.length; ++i) {
                RecommendEntry entry = rEntryArray[i];
                if (entry != null) {
                    if (result.length() > 0) {
                        result += ",";
                    }
                    result += entry.id + ":" + entry.count;
                }
            }
        }
        return result;
    }

    @Override
    public void updateRemarkWidget(String remark) {
        memo = remark;
        if (remark_tv != null)
            remark_tv.setText(remark);
    }

    void addHeader() {
        if (headView == null) {
            headView = LayoutInflater.from(getActivity()).inflate(R.layout.ucai_safe_tip_ly, null);
            mLv.addHeaderView(headView, null, false);
        }
    }

    boolean mIsBind = false;
    FarmSpecialtyShopCartAdapter adapter;
    View shopCartView;
    @Bind(R.id.pView)
    RelativeLayout pView;
    @Bind(R.id.title_right_text)
    TextView rightTv;
    TextView shop_cart_total_price_tv;
    @Bind(R.id.mLv)
    ExpandableHeightListView mLv;
    @Bind(R.id.title_center_text)
    TextView title_center_text;
    TextView number_copies;
    TextView remark_tv;
    TextView freight_money_tv;
    //    View addressLy;
    View footerView;
    //总份数
    private int allPart = 0;
    //总价格
    private int allMoney = 0;
    View sChilidView;
    @Bind(R.id.pScrollView)
    ScrollView pScrollView;
    String memo;
    CouponEntry cEntry;
    View noLoginView;

    View headView;
}
