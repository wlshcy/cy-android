package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.PayEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.data.WxPayResEntry;
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
import com.shequcun.farm.util.WxPayUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 套餐支付界面
 * Created by mac on 15/9/11.
 */
public class PayFragment extends BaseFragment {

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
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.pay);
        pay_money.setText(Utils.unitPeneyToYuan(getOrderMoney()));
        if (getChangeAddressEnable()) {
            addressLy.setEnabled(true);
            ((ImageView) v.findViewById(R.id.right_arrow_iv)).setImageResource(R.drawable.icon_more);
        } else {
            addressLy.setEnabled(false);
        }
    }

    @Override
    protected void setWidgetLsn() {
        alipay_ly.setOnClickListener(onClick);
        wx_pay_ly.setOnClickListener(onClick);
        OtherInfo info = buildOtherInfo();
        red_packets_money_ly.setVisibility((info != null && info.isSckill) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @OnClick(R.id.add_address_ly)
    void doClick() {
        gotoFragmentByAdd(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
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

    boolean getChangeAddressEnable() {
        Bundle bundle = getArguments();
        if (bundle == null) return false;
        return bundle.getBoolean(PARAMS_CHANGE_ADDRESS_ENABLE);
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
        if (addressEntry == null) {
            ToastHelper.showShort(getBaseAct(), "请完善您的收货地址!");
            return;
        }

        if (!TextUtils.isEmpty(alipay) && isAlipayPay) {
            doPay(alipay, payRes);
            return;
        }

        if (payRes != null && !isAlipayPay) {
            doPay(alipay, payRes);
            return;
        }

        if (entry != null && !TextUtils.isEmpty(entry.orderno)) {
            requestAlipay();
            return;
        }

        RequestParams params = new RequestParams();
        params.add("combo_id", mOrderController.getItems().get(0).combo_id + "");
        params.add("type", String.valueOf(isMyCombo() ? 2 : 1));
        params.add("combo_idx", getComboIdxParams());
        params.add("items", mOrderController.getOrderItemsString());
        params.add("name", addressEntry.name);
        params.add("mobile", addressEntry.mobile);
        params.add("address", addressStr);
        params.add("spares", mOrderController.getOrderOptionItemString());
        params.add("addon", mOrderController.getComboMatchItemString());
        params.add("paytype", isAlipayPay ? "2" : "3");
        if (coupon_id >= 0)
            params.add("coupon_id", coupon_id + "");
        if (entry != null && entry.info != null) {
            params.add("memo", entry.info.memo);
        }
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {

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
                    PayEntry orderEntry = JsonUtilsParser.fromJson(result, PayEntry.class);
                    if (orderEntry != null) {
                        if (TextUtils.isEmpty(orderEntry.errmsg)) {
                            alipay = orderEntry.alipay;
                            if (entry != null && !TextUtils.isEmpty(orderEntry.orderno)) {
                                entry.orderno = orderEntry.orderno;
                            }
                            if (isAlipayPay && TextUtils.isEmpty(orderEntry.alipay)) {
                                gotoFragmentByAdd(buildBundle(orderEntry.orderno, getOrderMoney(), orderEntry.alipay, true, R.string.order_result), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                            doPay(alipay, orderEntry.wxpay);
                            mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
                        } else {
                            ToastHelper.showShort(getBaseAct(), orderEntry.errmsg);
                        }
                    }
                } else {
                    ToastHelper.showShort(getBaseAct(), "异常：状态" + sCode);
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "创建订单失败.错误码" + sCode);
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
        payParams.setParams(orderno, orderMoney, alipay, isRecoDishes, titleId, true);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }


    void buildUserLoginEntry() {
        uEntry = new CacheManager(getBaseAct()).getUserLoginEntry();
    }


    void initAlipay() {
        if (aUtils == null)
            aUtils = new AlipayUtils();
        aUtils.setHandler(mHandler);
        aUtils.initAlipay(getBaseAct());
    }


    void initWxPay() {
        if (wxPayUtils == null)
            wxPayUtils = new WxPayUtils();
        wxPayUtils.initWxAPI(getBaseAct());
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    alipay = null;
                    payRes = null;
                    break;
                case Constrants.SDK_PAY_FLAG: {
                    AlipayUtils.PayResult payResult = new AlipayUtils.PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastHelper.showShort(getBaseAct(), "支付成功");
                        doPaySuccessful();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastHelper.showShort(getBaseAct(), "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastHelper.showShort(getBaseAct(), "支付失败");
                        }
                    }
                    break;
                }
                case Constrants.SDK_CHECK_FLAG: {
                    ToastHelper.showShort(getBaseAct(), "检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

    };


    void requestUserAddress() {
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "user/v3/address", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            successUserAddress(entry.aList);
                            return;
                        } else {
                            ToastHelper.showShort(getBaseAct(), entry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                setAddressWidgetContent(new CacheManager(getBaseAct()).getUserReceivingAddress());
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
        add_address_ly.setVisibility(View.GONE);
        addressLy.setVisibility(View.VISIBLE);
        addressee_info.setText(entry.name + "  " + entry.mobile);
        addressStr = entry.address;
        if (TextUtils.isEmpty(addressStr)) {
            StringBuilder builder = new StringBuilder();
            builder.append(!TextUtils.isEmpty(entry.city) ? entry.city : "");
            builder.append(!TextUtils.isEmpty(entry.region) ? entry.region : "");
            builder.append(!TextUtils.isEmpty(entry.address) ? entry.address : "");
//            builder.append(!TextUtils.isEmpty(entry.bur) ? entry.bur : "");
            addressStr = builder.toString();
        }
        address.setText("地址: " + addressStr);
    }


    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == alipay_ly || v == wx_pay_ly) {
                isAlipayPay = v == alipay_ly ? true : false;
                if (!isAlipayPay) {
                    doWxPayResultRegister();
                    initWxPay();
                } else {
                    initAlipay();
                }
                OtherInfo info = buildOtherInfo();
                if (info != null && info.item_type != 0) {
                    requestAlipay();
                    return;
                }
                if (info != null && info.type == 3) {//创建单品订单
                    createSingleOrder(info);
                    return;
                }
                makeOrder();
            }
        }
    };


    public void updateRedPackets(CouponEntry entry) {
        if (entry == null)
            return;
        alipay = null;
        payRes = null;
        int money = 0;
        if (red_packets_money_tv != null) {
            if (entry.distype == 1) {
                money = getOrderMoney() - entry.discount;
                red_packets_money_tv.setText("-" + entry.discount / 100 + "元");
            } else if (entry.distype == 2) {
                int discount = entry.discount / 10;
                money = getOrderMoney() * discount;
                red_packets_money_tv.setText("我要打" + discount + "折");
            }
        }
        if (coupon_id != entry.id)
            alipay = null;
        coupon_id = entry.id;
        pay_money.setText(Utils.unitPeneyToYuan(money));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        doUnWxPayResultRegister();
    }

    private OtherInfo buildOtherInfo() {
        if (entry != null)
            return entry.info;
        return null;
    }


    /**
     * 创建单品订单
     */
    void createSingleOrder(final OtherInfo info) {
        if (addressEntry == null) {
            ToastHelper.showShort(getBaseAct(), "请完善您的收货地址!");
            return;
        }

        if (!TextUtils.isEmpty(alipay) && isAlipayPay) {
            doPay(alipay, payRes);
            return;
        }

        if (payRes != null && !isAlipayPay) {
            doPay(alipay, payRes);
            return;
        }

        if (entry != null && !TextUtils.isEmpty(entry.orderno)) {
            requestAlipay();
            return;
        }

        RequestParams params = new RequestParams();
        params.add("type", "3");
        params.add("name", addressEntry.name);
        params.add("mobile", addressEntry.mobile);
        params.add("address", addressStr);
        params.add("extras", info.extras);
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("memo", info.memo);
        params.add("paytype", isAlipayPay ? "2" : "3");
        if (coupon_id > -1)
            params.add("coupon_id", coupon_id + "");

        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {

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
                    OrderEntry orderEntry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
                    if (orderEntry != null) {
                        if (TextUtils.isEmpty(orderEntry.errmsg)) {
                            if (info != null && info.type == 3) {
                                new CacheManager(getBaseAct()).delRecommendToDisk();
                                IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
                            }

                            alipay = orderEntry.alipay;
                            entry.orderno = orderEntry.orderno;
                            doPay(alipay, orderEntry.wxpay);
                            mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), orderEntry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "错误码" + sCode);
            }
        });
    }

    void requestAlipay() {
        RequestParams params = new RequestParams();
        params.add("orderno", entry.orderno);
        if (coupon_id > -1)
            params.add("coupon_id", coupon_id + "");
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("paytype", isAlipayPay ? "2" : "3");
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/payorder", params, new AsyncHttpResponseHandler() {

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
                    OrderEntry oEntry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
                    if (oEntry != null) {
                        if (TextUtils.isEmpty(oEntry.errmsg)) {
//                            if (isAlipayPay) {
//                                aUtils.doAlipay(oEntry.alipay);
//                            }
                            doPay(oEntry.alipay, oEntry.wxpay);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), oEntry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                ToastHelper.showShort(getBaseAct(), "获取支付内容失败");
            }
        });
    }

    void doWxPayResultRegister() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_WX_PAY_RESULT_MSG);
            getBaseAct().registerReceiver(mUpdateReceiver, intentFilter);
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
            if (action.equals(IntentUtil.UPDATE_WX_PAY_RESULT_MSG)) {
                int payCode = intent.getIntExtra("PayCode", -5);
                if (payCode == 0) {//表示支付成功
                    doPaySuccessful();
                } else if (payCode == -1) {//表示支付失败
                    ToastHelper.showShort(getBaseAct(), "微信支付失败,请稍后重试...");
                }
//                else if (payCode == -2) {//取消支付
//
//                }
            }
        }
    };

    void doUnWxPayResultRegister() {
        if (mIsBind) {
            getBaseAct().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    void doPaySuccessful() {
        gotoFragmentByAdd(buildBundle(entry.orderno, getOrderMoney(), alipay, true, R.string.order_result), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
    }

    void doPay(String alipay, WxPayResEntry payRes) {
        if (isAlipayPay) {
            aUtils.doAlipay(alipay);
        } else {
            if (payRes == null) {
                alertWxPayDlg();
                return;
            }

            if (payRes != null) {
                payRes.appId = LocalParams.getWxAppId();
            }
            this.payRes = payRes;
            wxPayUtils.doWxPay(payRes);
        }
    }

    private void alertWxPayDlg() {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText("微信支付失败.请改用支付宝试试吧!");
        alert.getWindow().findViewById(R.id.no)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
        alert.getWindow().findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        isAlipayPay = true;
                        requestAlipay();
                    }
                });
    }

    @OnClick(R.id.addressee_ly)
    void doModifyAddress() {
        Bundle bundle = new Bundle();
        bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SELECT);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
    }

    @OnClick(R.id.red_packets_money_ly)
    void doUseRedPackets() {
        OtherInfo info = buildOtherInfo();
        Bundle bundle = new Bundle();
        if (info != null && info.item_type == 0)
            bundle.putInt(RedPacketsListFragment.KEY_TYPE, info != null && info.type == 3 ? 2 : 1);
        else
            bundle.putInt(RedPacketsListFragment.KEY_TYPE, info != null ? info.item_type : 1);

        int payMoney = getOrderMoney();
        payMoney = (payMoney - 1000) / 1000 >= 99 ? payMoney : payMoney - 1000;
        bundle.putInt("PayMoney", payMoney);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new RedPacketsListFragment(), RedPacketsListFragment.class.getName());
    }

    WxPayResEntry payRes;
    /**
     * 微信支付
     */
    @Bind(R.id.wx_pay_ly)
    View wx_pay_ly;
    @Bind(R.id.alipay_ly)
    View alipay_ly;
    @Bind(R.id.pay_money)
    TextView pay_money;
    @Bind(R.id.red_packets_money_tv)
    TextView red_packets_money_tv;
    @Bind(R.id.addressee_info)
    TextView addressee_info;
    @Bind(R.id.addressee_ly)
    View addressLy;
    @Bind(R.id.add_address_ly)
    View add_address_ly;
    @Bind(R.id.address)
    TextView address;
    //使用优惠红包
    @Bind(R.id.red_packets_money_ly)
    View red_packets_money_ly;
    /**
     * true,支付宝支付  false 微信支付
     */
    boolean isAlipayPay = false;
    WxPayUtils wxPayUtils;
    //优惠券id
    int coupon_id = -1;
    String alipay;
    ComboEntry entry;
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();
    UserLoginEntry uEntry;
    AddressEntry addressEntry;
    AlipayUtils aUtils;
    boolean mIsBind = false;
    String addressStr;
    public static final String PARAMS_CHANGE_ADDRESS_ENABLE = "params_change_address_enable";
}
