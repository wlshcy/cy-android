package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.OrderDetailsAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 订单详情页
 * Created by apple on 15/8/10.
 */
public class OrderDetailsFragment extends BaseFragment implements RemarkFragment.CallBackLsn {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_details_ly, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBroadcast();
    }

    @Override
    protected void initWidget(View v) {
        titleTv.setText(R.string.cailanzi);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        v.findViewById(R.id.freight_ly).setVisibility(View.GONE);
        entry = buildEntry();
        buildUserLoginEntry();
        showBottomWidget();
        /**我的套餐，选品才需要地址，如果支付会在支付页面有地址*/
        if (isMyCombo()) {
            requestDefaultAddr();
        } else {
            pAddressView.setVisibility(View.GONE);
        }
    }

    private void addBroadcast() {
        IntentFilter intentFilter = new IntentFilter(IntentUtil.UPDATE_ADDRESS_REQUEST);
        intentFilter.addAction(IntentUtil.UPDATE_ADDRESS_REQUEST);
        getBaseAct().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void removeBroadcast() {
        getBaseAct().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestDefaultAddr();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeBroadcast();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    boolean isMyCombo() {
        /**! 这样判断是否是我的套餐是有问题的，如果从广告的套餐跳转过来的套餐id也在我的套餐里，也会当成我的套餐来处理了*/
//        if (uEntry != null) {
//            if (uEntry.mycomboids != null) {
//                int curComboId = getComboId();
//                int length = uEntry.mycomboids.length;
//                for (int i = 0; i < length; i++) {
//                    if (curComboId == uEntry.mycomboids[i]) {
//                        return true;
//                    }
//                }
//            }
//        }
        return (entry != null) ? entry.isMine() : false;
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    ComboEntry buildEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }


    void buildUserLoginEntry() {
        uEntry = new CacheManager(getBaseAct()).getUserLoginEntry();
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
                        shop_cart_total_price_tv.setVisibility(View.GONE);
                        commitOrderTv.setText(R.string.submit_immediately);
                        shop_cart_surpport_now_pay_tv.setText("您已选好菜品了!");
                        return;
                    }
                }

                if (entry != null) {
                    if (entry.prices != null && entry.prices.length > entry.getPosition()) {
                        shop_cart_total_price_tv.setText("共:" + Utils.unitPeneyToYuanEx(entry.prices[entry.getPosition()]));
                    }

                    if (entry.duration >= 52) {
                        shop_cart_surpport_now_pay_tv.setText("本套餐只支持年付!");
                    } else if (entry.duration >= 12) {
                        shop_cart_surpport_now_pay_tv.setText("本套餐只支持季付!");
                    } else if (entry.duration >= 8) {
                        shop_cart_surpport_now_pay_tv.setVisibility(View.GONE);
                    } else if (entry.duration >= 4) {
                        shop_cart_surpport_now_pay_tv.setText("本套餐只支持月付!");
                    } else {
                        shop_cart_surpport_now_pay_tv.setText(R.string.has_choosen_dishes);
                    }
                }

                commitOrderTv.setText(R.string.pay_immediately);
            } else {
                shop_cart_surpport_now_pay_tv.setText(R.string.has_choosen_dishes);
            }
        }
    }


    @Override
    protected void setWidgetLsn() {
//        UserLoginEntry
//        if (TextUtils.isEmpty(entry.name) || TextUtils.isEmpty(hEntry.address) || TextUtils.isEmpty(hEntry.mobile)) {
//            pAddressView.setVisibility(View.GONE);
//        } else {
//            addressLy.setVisibility(View.VISIBLE);
//            addressee_info.setText(hEntry.name + "  " + hEntry.mobile);
//            String addressStr = hEntry.address;
//            address.setText("地址: " + addressStr);
//        }
        buildAdapter();
    }


    @OnClick(R.id.title_right_text)
    void callServicePhone() {
        ConsultationDlg.showCallTelDlg(getBaseAct());
    }

    @OnClick(R.id.remark_ly)
    void doAddRemark() {
        RemarkFragment fragment = new RemarkFragment();
        fragment.setCallBackLsn(OrderDetailsFragment.this);
        Bundle bundle = new Bundle();
        bundle.putString("RemarkTip", remark_tv != null ? remark_tv.getText().toString() : "");
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, fragment, RemarkFragment.class.getName());
    }

    @OnClick(R.id.buy_order_tv)
    void doBuy() {
        if (commitOrderTv.getText().toString().equals(getResources().getString(R.string.pay_immediately))) {
            gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            return;
        }
        if (!checkAddress()) {
            Toast.makeText(getActivity(), "请选择地址", Toast.LENGTH_LONG).show();
            return;
        }
        //用户修改菜品时没有变更地址，弹出提示框：配送地址是否正确？确认后再提交。
        if (!isDiffAddress)
            showAddressDialog();
        else
            submit();
    }

    private void showAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseAct());
        builder.setTitle("提示");
        builder.setMessage("配送地址是否正确？");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submit();
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }


    private void submit() {
        if (isCreateOrder()) {
            createOrder();
        } else {
            modifyOrder(buildOrederno());
        }
    }

    private boolean checkAddress() {
        if (addressEntry == null) return false;
        if (TextUtils.isEmpty(addressEntry.name)) return false;
        if (TextUtils.isEmpty(addressEntry.mobile)) return false;
        if (TextUtils.isEmpty(addressEntry.address)) return false;
        return true;
    }

    void addFooter() {
        View footerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_footer_ly, null);
        String delievery = buildComboDeliveryDate();
        if (TextUtils.isEmpty(delievery)) {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:  本周五配送");
        } else {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:  本周" + delievery + "配送");
        }
        int part = mOrderController.getItemsCount();
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);
        addFooterFixedList();
        addSparesFooter();
    }

    /**
     * 添加备选菜
     */
    void addSparesFooter() {
        if (mOrderController != null && mOrderController.getOptionItems() != null && mOrderController.getOptionItems().size() > 0) {
            mLv.addFooterView(LayoutInflater.from(getBaseAct()).inflate(R.layout.remark_footer_ly, null), null, false);
            for (int i = 0; i < mOrderController.getOptionItems().size(); i++) {
                View footerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_item_ly, null);
                ImageView goodImg = (ImageView) footerView.findViewById(R.id.goods_img);
                ImageLoader.getInstance().displayImage(mOrderController.getOptionItems().get(i).imgs[0] + "?imageview2/2/w/180", goodImg);
                ((TextView) footerView.findViewById(R.id.goods_name)).setText(mOrderController.getOptionItems().get(i).title);
                ((TextView) footerView.findViewById(R.id.goods_price)).setText(Utils.unitConversion(mOrderController.getOptionItems().get(i).packw) + "/份");
                footerView.findViewById(R.id.goods_count).setVisibility(View.GONE);
                mLv.addFooterView(footerView, null, false);
            }
        }
    }

    void addHeader() {
        View v = LayoutInflater.from(getBaseAct()).inflate(R.layout.ucai_safe_tip_ly, null);
        mLv.addHeaderView(v, null, false);
    }

    void addFooterFixedList() {
        List<FixedComboEntry> aList = mOrderController.getComboMatchItems();
        if (aList != null && aList.size() > 0) {
            View view = LayoutInflater.from(getBaseAct()).inflate(R.layout.remark_footer_ly, null);
            TextView textView = (TextView) view.findViewById(R.id.title_tv);
            textView.setText("固定蔬菜");
            mLv.addFooterView(view);
            for (FixedComboEntry entry : aList) {
                if (entry == null)
                    continue;
                View headerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_item_ly, null);
                ImageView goodImg = (ImageView) headerView.findViewById(R.id.goods_img);
                if (entry.imgs != null && entry.imgs.length > 0)
                    ImageLoader.getInstance().displayImage(entry.imgs[0] + "?imageview2/2/w/180", goodImg);
                ((TextView) headerView.findViewById(R.id.goods_name)).setText(entry.title);
                ((TextView) headerView.findViewById(R.id.goods_price)).setText(entry.quantity + entry.unit + "/份");
                headerView.findViewById(R.id.goods_count).setVisibility(View.GONE);
                mLv.addFooterView(headerView);
            }
        }
    }


    void buildAdapter() {
        addFooter();
        if (adapter == null) {
            adapter = new OrderDetailsAdapter(getBaseAct());
        }
        mLv.setAdapter(adapter);
        adapter.addAll(mOrderController.buildItems());
        adapter.notifyDataSetChanged();
        addHeader();
    }


    /**
     * 构建订单配送日期
     *
     * @return
     */
    String buildComboDeliveryDate() {
        StringBuilder result = new StringBuilder();
        if (entry != null) {
            int shipday[] = entry.shipday;
            if (shipday != null && shipday.length > 0) {
                for (int i = 0; i < shipday.length; ++i) {
                    if (result.length() > 0)
                        result.append("、");
                    result.append(buildWeek(shipday[i]));
                }
            }
        }
        return result.toString();
    }

    String buildWeek(int delievery) {
        switch (delievery) {
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 4:
                return "四";
            case 3:
                return "三";
            case 2:
                return "二";
            case 1:
                return "一";
            default:
                return "抓紧拨打客服电话吧!";
        }
    }

    /**
     * 获取当前选择套餐的 ID
     *
     * @return
     */
    int getComboId() {
        return entry != null ? entry.id : -1;
    }

    int getOrderMoney() {
        return entry != null ? entry.prices[entry.getPosition()] : 0;
    }

    Bundle buildBundle(String orderno, int orderMoney, String alipay, boolean isRecoDishes, int titleId, boolean isLast) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.isLast = isLast;
        payParams.setParams(orderno, orderMoney, alipay, isRecoDishes, titleId, false);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    void modifyOrder(final String orderno) {
        RequestParams params = new RequestParams();
        params.add("orderno", orderno);
        params.add("items", mOrderController.getOrderItemsString());
        params.add("memo", remark_tv.getText().toString());
        params.add("spares", mOrderController.getOrderOptionItemString());
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("name", addressEntry.name);
        params.add("mobile", addressEntry.mobile);
        params.add("address", addressEntry.address);
        String addon = mOrderController.getComboMatchItemString();
        params.add("addon", addon);
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/altorder", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (jObj != null) {
                            String errmsg = jObj.optString("errmsg");
                            if (TextUtils.isEmpty(errmsg)) {
                                gotoFragmentByAdd(buildBundle(orderno, getOrderMoney(), "", true, R.string.order_result, false), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                            ToastHelper.showShort(getBaseAct(), errmsg);
                        }
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "修改订单失败,错误码" + sCode);
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

    String buildOrederno() {
        return entry != null ? entry.orderno : null;
    }

    String buildOrderCon() {
        return entry != null ? entry.con : null;
    }

    boolean isCreateOrder() {
        return (entry != null) ? entry.choose : false;
    }

    private void createOrder() {
        RequestParams params = new RequestParams();
        params.add("spares", mOrderController.getOrderOptionItemString());
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("items", mOrderController.getOrderItemsString());
        params.add("memo", remark_tv.getText().toString());
        params.add("name", addressEntry.name);
        params.add("mobile", addressEntry.mobile);
        params.add("address", addressEntry.address);
        params.add("orderno", buildOrderCon());
        params.add("addon", mOrderController.getComboMatchItemString());
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/choose", params, new AsyncHttpResponseHandler() {

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
                            if (TextUtils.isEmpty(entry.alipay)) {
                                gotoFragmentByAdd(buildBundle(entry.orderno, getOrderMoney(), entry.alipay, true, R.string.order_result, entry.last), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                        } else {
                            ToastHelper.showShort(getBaseAct(), entry.errmsg);
                        }
                    }
                } else {
                    ToastHelper.showShort(getBaseAct(), "异常：状态" + sCode);
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "创建订单失败.错误码" + sCode);
            }
        });
    }

    @Override
    public void updateRemarkWidget(String remark) {
        if (entry != null) {
            entry.info = new OtherInfo();
            entry.info.memo = remark;
        }
        if (remark_tv != null) {
            remark_tv.setText(remark);
        }
    }

    private void requestDefaultAddr() {
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "user/v3/address",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int sCode, Header[] h, byte[] data) {
                        String result = new String(data);
                        AddressListEntry entry = JsonUtilsParser.fromJson(result, AddressListEntry.class);
                        if (entry != null) {
                            if (TextUtils.isEmpty(entry.errmsg)) {
                                if (entry.aList != null && !entry.aList.isEmpty()) {
                                    for (AddressEntry o : entry.aList) {
                                        if (o.isDefault) {
                                            defaultAddressEntry = o;
                                            updateAddressLy(o);
                                            return;
                                        }
                                    }
                                    addAddressLy();
                                } else {
                                    /**没有地址，则添加地址*/
                                    addAddressLy();
                                }
                            } else {
                                ToastHelper.showShort(getBaseAct(), entry.errmsg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                        if (sCode == 0) {
                            ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), "请求失败,错误码" + sCode);
                    }
                });
    }

    private void addAddressLy() {
        /**选择或添加了新的地址*/
        isDiffAddress = true;
        addAddressLy.setVisibility(View.VISIBLE);
        addressLy.setVisibility(View.GONE);
    }

    @OnClick(R.id.add_address_ly)
    void doClick() {
        gotoFragmentByAdd(R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
    }

    @OnClick(R.id.addressee_ly)
    void doModifyAddress() {
        Bundle bundle = new Bundle();
        bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SELECT);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
    }

    private void updateAddressLy(AddressEntry hEntry) {
        this.addressEntry = hEntry;
        addAddressLy.setVisibility(View.GONE);
        addressLy.setVisibility(View.VISIBLE);
        addressee_info.setText(hEntry.name + "  " + hEntry.mobile);
        String addressStr = hEntry.address;
        address.setText("地址: " + addressStr);
        if (defaultAddressEntry != null && !TextUtils.isEmpty(defaultAddressEntry.address)) {
            if (!defaultAddressEntry.address.equals(hEntry.address)) {
                isDiffAddress = true;
            }
        }
    }

    ComboEntry entry;
    @Bind(R.id.title_center_text)
    TextView titleTv;
    @Bind(R.id.buy_order_tv)
    TextView commitOrderTv;
    UserLoginEntry uEntry;
    OrderDetailsAdapter adapter;
    @Bind(R.id.mLv)
    ListView mLv;

    @Bind(R.id.title_right_text)
    TextView rightTv;
    /**
     * 价格
     */
    @Bind(R.id.shop_cart_total_price_tv)
    TextView shop_cart_total_price_tv;
    @Bind(R.id.shop_cart_surpport_now_pay_tv)
    TextView shop_cart_surpport_now_pay_tv;
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();

    @Bind(R.id.remark_tv)
    TextView remark_tv;

    @Bind(R.id.addressee_ly)
    View addressLy;
    @Bind(R.id.add_address_ly)
    View addAddressLy;
    @Bind(R.id.addressee_info)
    TextView addressee_info;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.pAddressView)
    View pAddressView;
    /**
     * 是否变更了地址
     */
    private boolean isDiffAddress;
    private AddressEntry addressEntry;
    private AddressEntry defaultAddressEntry;
}
