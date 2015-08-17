package com.shequcun.farm.ui.fragment;

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
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.OrderDetailsAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

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
        buildUserLoginEntry();
        v.findViewById(R.id.addressee_ly).setVisibility(uEntry == null ? View.GONE : View.VISIBLE);
    }

    void buildUserLoginEntry() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        }
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        rightTv.setOnClickListener(onClick);
        re_choose_dishes.setOnClickListener(onClick);
        commitOrderTv.setOnClickListener(onClick);
        buildAdapter();
        setWidgetContent();
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
                makeOrder();
            }
        }
    };

    void setWidgetContent() {
        if (uEntry == null)
            return;
        String addInfo = (uEntry.name == null ? "" : uEntry.name) + "   " + uEntry.mobile;
        addressee_info.setText(addInfo);
        address.setText("地址: " + uEntry.address);
    }

    void addFooter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:");
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
        adapter.addAll(mOrderController.getItems());
        adapter.notifyDataSetChanged();
    }

    int getComboIdxParams() {
        Bundle bundle = getArguments();
        if (bundle==null)return -1;
        return bundle.getInt("comboIdx");
    }

    private void makeOrder() {
        int combo_id = mOrderController.getItems().get(0).combo_id;
        int type = 1;
        int combo_idx = getComboIdxParams();
        String items = mOrderController.getOrderItemsString();
        String name = uEntry.name;
        String mobile = uEntry.mobile;
        String address = uEntry.address;
        if (TextUtils.isEmpty(name)) {
            gotoAddressFragment();
            return;
        }
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

    private void gotoAddressFragment() {
        gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
    }

    private void gotoOrderSuccess() {
        gotoFragmentByAdd(R.id.mainpage_ly, new OrderSuccessFragment(), OrderSuccessFragment.class.getName());
    }

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
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();
}
