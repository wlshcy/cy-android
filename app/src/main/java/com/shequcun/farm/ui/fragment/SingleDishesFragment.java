package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.SingleDishesAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.List;

/**
 * 单品详情页
 * Created by apple on 15/8/19.
 */
public class SingleDishesFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_details_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        requestUserAddress();
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        titleTv = (TextView) v.findViewById(R.id.title_center_text);
        titleTv.setText(R.string.shop_cart);
        addressee_info = (TextView) v.findViewById(R.id.addressee_info);
        address = (TextView) v.findViewById(R.id.address);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        re_choose_dishes = v.findViewById(R.id.re_choose_dishes);
        commitOrderTv = (TextView) v.findViewById(R.id.bug_order_tv);
        addressLy = v.findViewById(R.id.addressee_ly);
        add_address_ly = v.findViewById(R.id.add_address_ly);
        shop_cart_total_price_tv = (TextView) v.findViewById(R.id.shop_cart_total_price_tv);
        shop_cart_surpport_now_pay_tv = (TextView) v.findViewById(R.id.shop_cart_surpport_now_pay_tv);
        buildUserLoginEntry();
        setWidgetContent();
    }


    void setWidgetContent() {
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            shop_cart_total_price_tv.setText("共付:" + Utils.unitPeneyToYuan(entry.price));
            shop_cart_surpport_now_pay_tv.setText("您已选好菜品了!");
        }
    }

    void buildUserLoginEntry() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        } else {
            uEntry = null;
        }

    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        rightTv.setOnClickListener(onClick);
        re_choose_dishes.setOnClickListener(onClick);
        commitOrderTv.setOnClickListener(onClick);
        addressLy.setOnClickListener(onClick);
        add_address_ly.setOnClickListener(onClick);
        buildAdapter();
    }

    void buildAdapter() {
        addFooter();
        if (adapter == null) {
            adapter = new SingleDishesAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
        adapter.add(buildRecommendEntry());
        adapter.notifyDataSetChanged();
    }

    RecommendEntry buildRecommendEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (RecommendEntry) bundle.getSerializable("RecommendEntry") : null;
    }


    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == rightTv)
                ConsultationDlg.showCallTelDlg(getActivity());
            else if (re_choose_dishes == v) {//重新选择菜品

            } else if (v == commitOrderTv) {
                createSingleDishesOrder(addressEntry);
//                makeOrder();
//                gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            } else if (v == add_address_ly) {
                gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
            }
        }
    };

    void addFooter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:本周周五");
        int part = 1;
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);
    }

    void requestUserAddress() {
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            successUserAddress(entry.aList);
                            return;
                        } else {
                            ToastHelper.showShort(getActivity(), entry.errmsg);
                        }
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

    private void successUserAddress(List<AddressEntry> list) {
        if (list == null || list.size() <= 0) {
            addressLy.setVisibility(View.GONE);
            add_address_ly.setVisibility(View.VISIBLE);
            doRegisterRefreshBrodcast();
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            AddressEntry entry = list.get(i);
            if (entry.isDefault) {
                if (!TextUtils.isEmpty(entry.name) && !TextUtils.isEmpty(uEntry.address)) {
                    addressEntry = entry;
                    addressLy.setVisibility(View.VISIBLE);
                    add_address_ly.setVisibility(View.GONE);
                    addressee_info.setText(entry.name + "  " + entry.mobile);
                    address.setText("地址: " + uEntry.address);
                } else {
                    doRegisterRefreshBrodcast();
                    addressLy.setVisibility(View.GONE);
                    add_address_ly.setVisibility(View.VISIBLE);
                }
                return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
        mHandler.removeCallbacksAndMessages(null);
    }


    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_ADDRESS_MSG);
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    boolean mIsBind = false;

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(IntentUtil.UPDATE_ADDRESS_MSG)) {
                buildUserLoginEntry();
                requestUserAddress();
            }
        }
    };

    /**
     * 创建单品订单
     */
    void createSingleDishesOrder(AddressEntry entry) {
        if (entry == null || TextUtils.isEmpty(entry.name) || TextUtils.isEmpty(entry.mobile) || TextUtils.isEmpty(entry.zname)) {
            ToastHelper.showShort(getActivity(), "地址获取失败,请稍后重试...");
            return;
        }
        String address = null;
        if (uEntry != null && !TextUtils.isEmpty(uEntry.address)) {
            address = uEntry.address;
        }
        final RecommendEntry rEntry = buildRecommendEntry();
        if (!TextUtils.isEmpty(alipay) && !TextUtils.isEmpty(orderno)) {
            gotoFragmentByAdd(buildBundle(orderno, rEntry.price, alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            return;
        }

        RequestParams params = new RequestParams();
        params.add("type", "3");
        params.add("name", entry.name);
        params.add("mobile", entry.mobile);
        params.add("address", address);
        params.add("extras", getExtras());
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));


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
                            if (rEntry != null) {
                                gotoFragmentByAdd(buildBundle(orderno = entry.orderno, rEntry.price, alipay = entry.alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                                mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
                            }
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
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    public String getExtras() {
        String result = "";
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            result += entry.id + ":" + 1;
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

    String alipay;
    String orderno;
    AddressEntry addressEntry;
    View addressLy;
    TextView titleTv;
    TextView commitOrderTv;
    UserLoginEntry uEntry;
    SingleDishesAdapter adapter;
    ListView mLv;
    View back;
    TextView addressee_info;
    TextView address;
    TextView rightTv;
    View re_choose_dishes;
    View add_address_ly;
    TextView shop_cart_total_price_tv;
    TextView shop_cart_surpport_now_pay_tv;
}
