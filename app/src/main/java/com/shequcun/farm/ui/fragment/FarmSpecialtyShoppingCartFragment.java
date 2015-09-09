package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.model.PhotoModel;
import com.shequcun.farm.ui.adapter.FarmSpecialtyShopCartAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 农庄特产购物车
 * Created by mac on 15/9/7.
 */
public class FarmSpecialtyShoppingCartFragment extends BaseFragment {
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
        pView = (RelativeLayout) v.findViewById(R.id.pView);
        v.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.shop_cart);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        mLv = (ExpandableHeightListView) v.findViewById(R.id.mLv);
        addressee_info = (TextView) v.findViewById(R.id.addressee_info);
        address = (TextView) v.findViewById(R.id.address);
        add_address_ly = v.findViewById(R.id.add_address_ly);
        addressLy = v.findViewById(R.id.addressee_ly);
        pAddressView = v.findViewById(R.id.pAddressView);
        pAddressView.setVisibility(View.GONE);
        pScrollView = (ScrollView) v.findViewById(R.id.pScrollView);
    }

    @Override
    protected void setWidgetLsn() {
        addWidgetToView();
        rightTv.setOnClickListener(onClick);
        add_address_ly.setOnClickListener(onClick);
        addressLy.setOnClickListener(onClick);
        doRegisterRefreshBrodcast();
    }

    void addWidgetToView() {
        if (isLogin()) {
            RecommendEntry[] rEntryArray = new CacheManager(getActivity()).getRecommendFromDisk();
            if (rEntryArray != null && rEntryArray.length > 0) {
                removeWidgetFromView();
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

        if (sChilidView != null) {
            pView.removeView(sChilidView);
            sChilidView = null;
        }
        if (pAddressView != null)
            pAddressView.setVisibility(View.GONE);
        memo = null;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == rightTv) {
                ConsultationDlg.showCallTelDlg(getActivity());
            } else if (v == add_address_ly || v == addressLy) {
                gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
            }
        }
    };

    void addFooter(final int part) {
        if (footerView == null) {
            footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:  本周五配送");
            number_copies = (TextView) footerView.findViewById(R.id.number_copies);
            mLv.addFooterView(footerView, null, false);
            requestUserAddress();
            addSchildView();
        }

//        if (sChilidView == null) {
//            sChilidView = LayoutInflater.from(getActivity()).inflate(R.layout.farm_shopping_cart_footer_ly, null);
//            mLv.addFooterView(sChilidView, null, false);
//        }

        updateWidget(part, allMoney);
    }

    void removeWidgetFromView() {
        if (shopCartView != null) {
            pView.removeView(shopCartView);
            shopCartView = null;
        }
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getActivity()).getUserLoginFromDisk() != null;
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new FarmSpecialtyShopCartAdapter(getActivity());
        adapter.clear();
        adapter.buildOnClickLsn(onGoodsImgLsn, onAddGoodsLsn, onSubGoodsLsn);
        mLv.setAdapter(adapter);
        mLv.setExpanded(true);
    }

    void addDataToAdapter(RecommendEntry[] rEntryArray) {
        allPart = 0;
        allMoney = 0;
        for (int i = 0; i < rEntryArray.length; ++i) {
            allPart += rEntryArray[i].count;
            allMoney += rEntryArray[i].count * rEntryArray[i].price;
        }
        adapter.addAll(rEntryArray);
        adapter.notifyDataSetChanged();
        addFooter(allPart);
        pScrollView.fullScroll(View.FOCUS_UP);
//        mLv.smoothScrollToPosition(0);
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
            gotoFragmentByAdd(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName());
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
        alipay = null;
        orderno = null;
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
        mHandler.removeCallbacksAndMessages(null);
    }

    void updateWidget(int part, int allMoney) {
        if (number_copies == null || shop_cart_total_price_tv == null)
            return;
        number_copies.setText("共" + part + "份");
        shop_cart_total_price_tv.setText("共付:" + Utils.unitPeneyToYuan(allMoney));
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_FARM_SHOPPING_CART_MSG);
            intentFilter.addAction(IntentUtil.UPDATE_ADDRESS_MSG);
            intentFilter.addAction(IntentUtil.UPDATE_FARM_SHOPPING_CART_MEMO);
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (action.equals(IntentUtil.UPDATE_FARM_SHOPPING_CART_MSG)) {
                addWidgetToView();
            } else if (action.equals(IntentUtil.UPDATE_ADDRESS_MSG)) {
                requestUserAddress();
            } else if (action.equals(IntentUtil.UPDATE_FARM_SHOPPING_CART_MEMO)) {
                if (remark_tv != null) {
                    memo = intent.getStringExtra("MEMO");
                    if (!TextUtils.isEmpty(memo))
                        remark_tv.setText(memo);
                }
            }
        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    void requestUserAddress() {
        if (pAddressView != null) {
            pAddressView.setVisibility(View.VISIBLE);
        }
        final UserLoginEntry uEntry = new CacheManager(getActivity()).getUserLoginEntry();
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            successUserAddress(uEntry, entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + sCode);
            }
        });
    }

    private void successUserAddress(final UserLoginEntry uEntry, List<AddressEntry> list) {
        if (list == null || list.size() <= 0) {
            addressLy.setVisibility(View.GONE);
            add_address_ly.setVisibility(View.VISIBLE);
            return;
        }

        int size = list.size();
        for (int i = 0; i < size; ++i) {
            AddressEntry entry = list.get(i);
//            if (entry.isDefault) {
            if (!TextUtils.isEmpty(entry.name) && !TextUtils.isEmpty(uEntry.address)) {
                addressEntry = entry;
                addressLy.setVisibility(View.VISIBLE);
                add_address_ly.setVisibility(View.GONE);
                addressee_info.setText(entry.name + "  " + entry.mobile);
                address.setText("地址: " + uEntry.address);
            } else {
                addressLy.setVisibility(View.GONE);
                add_address_ly.setVisibility(View.VISIBLE);
            }
            return;
//            }
        }
    }

    void addSchildView() {
        if (sChilidView == null) {
            sChilidView = LayoutInflater.from(getActivity()).inflate(R.layout.farm_shopping_cart_footer_ly, null);
            shop_cart_total_price_tv = (TextView) sChilidView.findViewById(R.id.shop_cart_total_price_tv);
            ((TextView) sChilidView.findViewById(R.id.shop_cart_surpport_now_pay_tv)).setText(R.string.has_choosen_dishes);

            sChilidView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createOrder(addressEntry);
                }
            });

            sChilidView.findViewById(R.id.remark_ly).setOnClickListener(new View.OnClickListener() {//添加备注
                @Override
                public void onClick(View v) {
                    gotoFragmentByAdd(R.id.mainpage_ly, new RemarkFragment(), RemarkFragment.class.getName());
                }
            });
            remark_tv = (TextView) sChilidView.findViewById(R.id.remark_tv);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            params.bottomMargin = ResUtil.dipToPixel(getActivity(), 5);

            pView.addView(sChilidView, params);
        }
    }

    /**
     * 创建单品订单
     */
    void createOrder(AddressEntry entry) {
        if (entry == null || TextUtils.isEmpty(entry.name) || TextUtils.isEmpty(entry.mobile) || TextUtils.isEmpty(entry.zname)) {
            ToastHelper.showShort(getActivity(), "地址获取失败,请稍后重试...");
            return;
        }
        String address = null;
        UserLoginEntry uEntry = new CacheManager(getActivity()).getUserLoginEntry();
        if (uEntry != null && !TextUtils.isEmpty(uEntry.address)) {
            address = uEntry.address;
        }
        if (!TextUtils.isEmpty(alipay) && !TextUtils.isEmpty(orderno)) {
            gotoFragmentByAdd(buildBundle(orderno, allMoney, alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            return;
        }

        RequestParams params = new RequestParams();
        params.add("type", "3");
        params.add("name", entry.name);
        params.add("mobile", entry.mobile);
        params.add("address", address);
        params.add("extras", getExtras());
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("memo", memo);


        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    OrderEntry entry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            gotoFragmentByAdd(buildBundle(orderno = entry.orderno, allMoney, alipay = entry.alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                            mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码" + sCode);
            }
        });
    }

    Bundle buildBundle(String orderno, int orderMoney, String alipay, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, false, titleId);
        payParams.type = 3;
        bundle.putSerializable("PayParams", payParams);
        return bundle;
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


    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    alipay = null;
                    orderno = null;
                    break;
            }
        }
    };

    View pAddressView;
    AddressEntry addressEntry;
    boolean mIsBind = false;
    FarmSpecialtyShopCartAdapter adapter;
    View shopCartView;
    RelativeLayout pView;
    TextView rightTv;
    TextView shop_cart_total_price_tv;
    ExpandableHeightListView mLv;
    TextView number_copies;
    TextView addressee_info;
    View add_address_ly;
    TextView address;
    TextView remark_tv;
    View addressLy;
    View footerView;
    //总份数
    private int allPart = 0;
    //总价格
    private int allMoney = 0;
    View sChilidView;
    ScrollView pScrollView;
    String orderno;
    String alipay;
    String memo;
}