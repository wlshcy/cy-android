package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.OrderDetailsAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.List;

/**
 * 订单详情页
 * Created by apple on 15/8/10.
 */
public class OrderDetailsFragment extends BaseFragment {
    @Nullable
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
        buildUserLoginEntry();
        showBottomWidget();
    }

    @Override
    public void onStart() {
        super.onStart();
        requestUserAddress();
    }

    void buildUserLoginEntry() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        }
    }

    /**
     * 显示底部对应的控件
     */
    void showBottomWidget() {
        if (uEntry != null) {
            if (uEntry.mycomboids != null) {
                int curComboId = getComboId();
                int length = uEntry.mycomboids.length;
                for (int i = 0; i < length; i++) {
                    if (curComboId == uEntry.mycomboids[i]) {
                        commitOrderTv.setText(R.string.submit_immediately);
                        return;
                    }
                }
                commitOrderTv.setText(R.string.pay_immediately);
            }
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

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == rightTv)
                ConsultationDlg.showCallTelDlg(getActivity());
            else if (re_choose_dishes == v) {//重新选择菜品

            } else if (v == commitOrderTv) {
//                makeOrder();

                gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            } else if (v == add_address_ly) {
                gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
            }
        }
    };

    void addFooter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:本周周五");
        int part = mOrderController.getItemsCount();
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);
    }

    void buildAdapter() {
        addFooter();
        if (adapter == null) {
            adapter = new OrderDetailsAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
        adapter.addAll(mOrderController.buildItems());
        adapter.notifyDataSetChanged();
    }

    int getComboIdxParams() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            ComboEntry entry = (ComboEntry) bundle.getSerializable("ComboEntry");
            if (entry != null)
                return entry.getPosition();
        }
        return -1;
    }

    /**
     * 获取当前选择套餐的 ID
     *
     * @return
     */
    int getComboId() {
        Bundle bundle = getArguments();
        return bundle != null ? ((ComboEntry) bundle.getSerializable("ComboEntry")).id : -1;
    }

    private void makeOrder() {
        if (addressEntry == null)
            return;
        int combo_id = mOrderController.getItems().get(0).combo_id;
        int type = 1;
        int combo_idx = getComboIdxParams();
        String items = mOrderController.getOrderItemsString();
        String name = addressEntry.name;
        String mobile = addressEntry.mobile;
        String address = uEntry.address;
        requestCaiOrder(combo_id, type, combo_idx, items, name, mobile, address);
    }

    private void requestCaiOrder(int combo_id, int type, int combo_idx, String items, String name, String mobile, String address) {
        RequestParams params = new RequestParams();
        params.add("combo_id", combo_id + "");
        params.add("type", type + "");
        params.add("combo_idx", combo_idx + "");
        params.add("items", items);
        params.add("name", mobile);
        params.add("mobile", mobile);
        params.add("address", address);
        params.add("_xsrf", PersistanceManager.INSTANCE.getCookieValue());
        HttpRequestUtil.getClient().post(LocalParams.INSTANCE.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    String result = new String(responseBody);
                    OrderEntry entry = JsonUtilsParser.fromJson(result, OrderEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errcode)) {
                            gotoOrderSuccess();
                        } else {
                            ToastHelper.showShort(getFragmentActivity(), entry.errmsg);
                        }
                    }
                } else {
                    ToastHelper.showShort(getFragmentActivity(), "异常：状态" + statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ToastHelper.showShort(getFragmentActivity(), error.getMessage());
            }
        });
    }

    void requestUserAddress() {
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {

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
            doRegisterRefreshBrodcast();
            addressLy.setVisibility(View.GONE);
            add_address_ly.setVisibility(View.VISIBLE);
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


    private void gotoOrderSuccess() {
        gotoFragmentByAdd(R.id.mainpage_ly, new OrderSuccessFragment(), OrderSuccessFragment.class.getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_ORDER_DETAILS_MSG);
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
            if (action.equals(IntentUtil.UPDATE_ORDER_DETAILS_MSG)) {
                buildUserLoginEntry();
                requestUserAddress();
            }
        }
    };

    AddressEntry addressEntry;

    View addressLy;
    TextView titleTv;
    TextView commitOrderTv;
    UserLoginEntry uEntry;
    OrderDetailsAdapter adapter;
    ListView mLv;
    View back;
    TextView addressee_info;
    TextView address;
    TextView rightTv;
    View re_choose_dishes;
    View add_address_ly;
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();
}
