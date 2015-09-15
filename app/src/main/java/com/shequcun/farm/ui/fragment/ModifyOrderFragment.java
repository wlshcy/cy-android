package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.shequcun.farm.data.AlreadyPurchasedEntry;
import com.shequcun.farm.data.AlreadyPurchasedListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.AlreadyPurchasedAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * 修改订单
 * Created by apple on 15/8/20.
 */
public class ModifyOrderFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lookup_order_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        order_btn = (TextView) v.findViewById(R.id.order_btn);
        mLv = (ListView) v.findViewById(R.id.mLv);
        hEntry = buildModifyOrderObj();
        address = (TextView) v.findViewById(R.id.address);
        addressee_info = (TextView) v.findViewById(R.id.addressee_info);
        addressLy = v.findViewById(R.id.addressee_ly);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.order_details);
//        order_btn.setVisibility(isShowFooteWgt() ? View.VISIBLE : View.GONE);
        if (getOrderStatus() == 0) {//未付款
            order_btn.setText(R.string.pay_immediately);
        } else if (getOrderStatus() == 1) {//待配送
            int orderType = getOrderType();
            order_btn.setText(orderType == 1 || orderType == 2 ? R.string.re_choose_dishes : R.string.cancel_order);
        } else if (getOrderStatus() == 2) {//订单配送中
            order_btn.setEnabled(false);
            order_btn.setText(hEntry == null ? "" : hEntry.date);
        } else {
            order_btn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setWidgetLsn() {
        requestUserAddress();
        back.setOnClickListener(onClick);
        order_btn.setOnClickListener(onClick);
        requestOrderDetails();
    }

    private ModifyOrderParams buildModifyOrderObj() {
        Bundle bundle = getArguments();
        return bundle != null ? (ModifyOrderParams) bundle.getSerializable("HistoryOrderEntry") : null;
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == order_btn) {
                int orderType = getOrderType();
                if (getOrderStatus() == 1) {//待配送
                    order_btn.setText(orderType == 1 || orderType == 2 ? R.string.re_choose_dishes : R.string.cancel_order);
                    if (orderType == 1 || orderType == 2) {
                        showConfirmDlg();
                    } else if (getOrderType() == 0) {
                        cancelOrder();
                    }
                } else if (getOrderStatus() == 0) {
                    requestAlipay(hEntry);
                }
            }
        }
    };

    String getOrderNumber() {
        return hEntry != null ? hEntry.orderno : null;
    }


    void cancelOrder() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", hEntry.id + "");
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/delorder", params, new AsyncHttpResponseHandler() {

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
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (TextUtils.isEmpty(jObj.optString("errmsg"))) {
                            ToastHelper.showShort(getActivity(), R.string.cancel_order_success);
                            popBackStack();
                            return;
                        }
                        ToastHelper.showShort(getActivity(), jObj.optString("errmsg"));

                    }
                } catch (Exception e) {

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

    /**
     * 订单类型
     *
     * @return int 1.套餐订单 2.选菜订单, 3.单品订单, 4.自动选菜订单
     */
    int getOrderType() {
        return hEntry != null ? hEntry.type : 0;
    }

    /**
     * int	订单状态
     *
     * @return 0.未付款, 1.待配送, 2.配送中, 3.配送完成, 4.取消订单, 5.套餐配送完成
     */
    int getOrderStatus() {
        return hEntry != null ? hEntry.status : 0;
    }

    void requestOrderDetails() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("orderno", getOrderNumber());
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/orderdtl", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AlreadyPurchasedListEntry entry = JsonUtilsParser.fromJson(new String(data), AlreadyPurchasedListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            buildAdapter(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }

                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码" + sCode);
            }

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
        });
    }

    void addHeaderView() {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) headView.findViewById(R.id.distribution_date)).setText(hEntry.date);
        mLv.addHeaderView(headView, null, false);
    }

    void addFooter(int part) {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        if (getOrderStatus() == 3 || getOrderStatus() == 5 || getOrderStatus() == 0) {

        } else if (getOrderStatus() == 2) {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送中");
        } else {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:本周五配送");
        }

        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);
    }

    void buildAdapter(List<AlreadyPurchasedEntry> aList) {
        if (aList == null || aList.size() <= 0 || hEntry == null)
            return;
        int part = 0;
        int allWeight = 0;
        for (AlreadyPurchasedEntry entry : aList) {
            part += entry.packs;
            allWeight += entry.packs * entry.packw;
        }
        hEntry.allWeight = allWeight;
        addFooter(part);
        addHeaderView();
        if (adapter == null) {
            adapter = new AlreadyPurchasedAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
    }

    void requestAlipay(final ModifyOrderParams entry) {
        RequestParams params = new RequestParams();
        params.add("orderno", entry.orderno);
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/payorder", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    OrderEntry oEntry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
                    if (oEntry != null) {
                        if (TextUtils.isEmpty(oEntry.errmsg)) {
                            gotoFragmentByAdd(buildBundle(entry.orderno, entry.price, oEntry.alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                            return;
                        }

                        ToastHelper.showShort(getActivity(), oEntry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                ToastHelper.showShort(getActivity(), "获取支付内容失败");
            }
        });
    }

    Bundle buildBundle(String orderno, int orderMoney, String alipay, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, false, titleId, false);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    private void showConfirmDlg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
//        builder.setMessage(R.string.choose_dishes_tip);
        builder.setMessage(R.string.re_choose_dishes_tip);
        builder.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                PhoneUtil.gotoCall(getActivity(), Constrants.Customer_Service_Phone);
                ComboEntry entry = new ComboEntry();
                entry.id = hEntry.combo_id;
                entry.setPosition(0);
                entry.weights = new int[1];
                entry.weights[0] = hEntry.allWeight;
                entry.prices = new int[1];
                entry.prices[0] = hEntry.price;
                entry.combo_idx = hEntry.combo_idx;
                entry.orderno = hEntry.orderno;
                Bundle bundle = new Bundle();
                bundle.putSerializable("ComboEntry", entry);
                popBackStack();
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            }
        });
        builder.setNeutralButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
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
        if (list == null || list.size() <= 0)
            return;
        UserLoginEntry uEntry = new CacheManager(getActivity()).getUserLoginEntry();
        if (uEntry == null)
            return;
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            AddressEntry entry = list.get(i);
            if (entry.isDefault) {
                setAddressWidgetContent(entry);
                return;
            }
        }
    }

    public void setAddressWidgetContent(AddressEntry entry) {
        if (entry == null)
            return;
        addressLy.setVisibility(View.VISIBLE);
        addressee_info.setText(entry.name + "  " + entry.mobile);
        String addressStr = entry.address;
        if (TextUtils.isEmpty(addressStr)) {
            StringBuilder builder = new StringBuilder();
            builder.append(!TextUtils.isEmpty(entry.city) ? entry.city : "");
            builder.append(!TextUtils.isEmpty(entry.region) ? entry.region : "");
            builder.append(!TextUtils.isEmpty(entry.zname) ? entry.zname : "");
            builder.append(!TextUtils.isEmpty(entry.bur) ? entry.bur : "");
            addressStr = builder.toString();
        }
        address.setText("地址: " + addressStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    View addressLy;
    ModifyOrderParams hEntry;
    AlreadyPurchasedAdapter adapter;
    View back;
    TextView order_btn;
    TextView addressee_info;
    TextView address;
    ListView mLv;
}
