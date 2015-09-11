package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AlipayUtils;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.List;

/**
 * 套餐支付界面
 * Created by mac on 15/9/11.
 */
public class PayComboFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        entry = buildEntry();
        buildUserLoginEntry();
        requestUserAddress();
        back = v.findViewById(R.id.back);
        alipay_ly = v.findViewById(R.id.alipay_ly);
        pay_money = (TextView) v.findViewById(R.id.pay_money);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.pay);
        pay_money.setText(Utils.unitPeneyToYuan(getOrderMoney()));
        addressee_info = (TextView) v.findViewById(R.id.addressee_info);
        address = (TextView) v.findViewById(R.id.address);
        add_address_ly = v.findViewById(R.id.add_address_ly);
        addressLy = v.findViewById(R.id.addressee_ly);
    }

    @Override
    protected void setWidgetLsn() {
        initAlipay();
        back.setOnClickListener(onClick);
        alipay_ly.setOnClickListener(onClick);
        add_address_ly.setOnClickListener(onClick);
        addressLy.setOnClickListener(onClick);
    }

    int getOrderMoney() {
        return entry != null ? entry.prices[entry.getPosition()] : 0;
    }


    String getComboIdxParams() {
        if (entry != null) {
            if (!TextUtils.isEmpty(entry.combo_idx))
                return entry.combo_idx;
            return entry.getPosition() + "";
        }
        if (!TextUtils.isEmpty(entry.combo_idx))
            return entry.combo_idx;
        return entry.getPosition() + "";
    }

    boolean isMyCombo() {
        if (uEntry != null) {
            if (uEntry.mycomboids != null) {
                int curComboId = getComboId();
                int length = uEntry.mycomboids.length;
                for (int i = 0; i < length; i++) {
                    if (curComboId == uEntry.mycomboids[i]) {
                        return true;
                    }
                }
            }
        }
        return (entry != null) ? entry.isMine() : false;
    }

    private void makeOrder() {
        if (!TextUtils.isEmpty(alipay)) {
            aUtils.doAlipay(alipay);
            return;
        }
        int combo_id = mOrderController.getItems().get(0).combo_id;
        int type = isMyCombo() ? 2 : 1;
        String combo_idx = getComboIdxParams();
        String items = mOrderController.getOrderItemsString();
        String name = addressEntry.name;
        String mobile = addressEntry.mobile;
        String address = addressStr;
        String spares = mOrderController.getOrderOptionItemString();
        requestCaiOrder(combo_id, type, combo_idx, spares, items, name, mobile, address);
    }

    private void requestCaiOrder(int combo_id, int type, String combo_idx, String spares, String items, String name, String mobile, String address) {
        RequestParams params = new RequestParams();
        params.add("combo_id", combo_id + "");
        params.add("type", type + "");
        params.add("combo_idx", combo_idx);
        params.add("items", items);
        params.add("name", name);
        params.add("mobile", mobile);
        params.add("address", address);
        params.add("spares", spares);
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
            public void onSuccess(int sCode, Header[] headers, byte[] data) {
                if (sCode == 200) {
                    String result = new String(data);
                    OrderEntry entry = JsonUtilsParser.fromJson(result, OrderEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            alipay = entry.alipay;
                            if (TextUtils.isEmpty(entry.alipay)) {
                                gotoFragmentByAdd(buildBundle(entry.orderno, getOrderMoney(), entry.alipay, true, R.string.order_result), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                            aUtils.doAlipay(alipay);
                            mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
                        } else {
                            ToastHelper.showShort(getActivity(), entry.errmsg);
                        }
                    }
                } else {
                    ToastHelper.showShort(getActivity(), "异常：状态" + sCode);
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "创建订单失败.错误码" + sCode);
            }
        });
    }

    ComboEntry buildEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }

    /**
     * 获取当前选择套餐的 ID
     *
     * @return
     */
    int getComboId() {
        return entry != null ? entry.id : -1;
    }

    Bundle buildBundle(String orderno, int orderMoney, String alipay, boolean isRecoDishes, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, isRecoDishes, titleId);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }


    void buildUserLoginEntry() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        } else {
            uEntry = null;
        }
    }


    void initAlipay() {
        aUtils = new AlipayUtils();
        aUtils.setHandler(mHandler);
        aUtils.initAlipay(getActivity());
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    alipay = null;
                    break;
                case Constrants.SDK_PAY_FLAG: {
                    AlipayUtils.PayResult payResult = new AlipayUtils.PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

//                        Bundle bundle = getArguments();
//                        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
//                        if (entry != null && entry.type == 3) {
//                            new CacheManager(getActivity()).delRecommendToDisk();
//                            IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
//                        }

                        ToastHelper.showShort(getActivity(), "支付成功");
                        gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastHelper.showShort(getActivity(), "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastHelper.showShort(getActivity(), "支付失败");
                        }
                    }
                    break;
                }
                case Constrants.SDK_CHECK_FLAG: {
                    ToastHelper.showShort(getActivity(), "检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    void requestUserAddress() {
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/v2/address", new AsyncHttpResponseHandler() {
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
            return;
        }

        int size = list.size();
        for (int i = 0; i < size; ++i) {
            AddressEntry entry = list.get(i);
            if (entry.isDefault) {
                setAddressWidgetContent(entry);
                break;
            }
        }
    }

    public void setAddressWidgetContent(AddressEntry entry) {
        if (entry == null)
            return;
        addressEntry = entry;
        addressLy.setVisibility(View.GONE);
        addressLy.setVisibility(View.VISIBLE);
        addressee_info.setText(entry.name + "  " + entry.mobile);
        addressStr = entry.address;
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


    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == alipay_ly)
                makeOrder();
            else if (add_address_ly == v) {
                gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
            } else if (addressLy == v) {
                Bundle bundle = new Bundle();
                bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SELECT);
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    String alipay;
    ComboEntry entry;
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();
    UserLoginEntry uEntry;
    AddressEntry addressEntry;
    AlipayUtils aUtils;
    View back;
    View alipay_ly;
    TextView pay_money;
    TextView addressee_info;
    View addressLy;
    View add_address_ly;
    TextView address;
    String addressStr;
}
