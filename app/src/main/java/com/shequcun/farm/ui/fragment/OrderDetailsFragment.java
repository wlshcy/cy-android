package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
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
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;
import org.json.JSONObject;

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
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        titleTv = (TextView) v.findViewById(R.id.title_center_text);
        titleTv.setText(R.string.cailanzi);
        address = (TextView) v.findViewById(R.id.address);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        commitOrderTv = (TextView) v.findViewById(R.id.buy_order_tv);
        shop_cart_total_price_tv = (TextView) v.findViewById(R.id.shop_cart_total_price_tv);
        shop_cart_surpport_now_pay_tv = (TextView) v.findViewById(R.id.shop_cart_surpport_now_pay_tv);
        remark_ly = v.findViewById(R.id.remark_ly);
        remark_tv = (TextView) v.findViewById(R.id.remark_tv);
        v.findViewById(R.id.freight_ly).setVisibility(View.GONE);
        entry = buildEntry();
        buildUserLoginEntry();
        showBottomWidget();
    }

    ComboEntry buildEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }


    void buildUserLoginEntry() {
        uEntry = new CacheManager(getActivity()).getUserLoginEntry();
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
        back.setOnClickListener(onClick);
        rightTv.setOnClickListener(onClick);
        commitOrderTv.setOnClickListener(onClick);
        remark_ly.setOnClickListener(onClick);
        buildAdapter();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == rightTv)
                ConsultationDlg.showCallTelDlg(getActivity());
            else if (v == commitOrderTv) {
                if (commitOrderTv.getText().toString().equals(getResources().getString(R.string.pay_immediately))) {
                    gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                    return;
                }
                if (isCreateOrder()) {
                    createOrder();
                } else {
                    modifyOrder(buildOrederno());
                }
            } else if (v == remark_ly) {
                RemarkFragment fragment = new RemarkFragment();
                fragment.setCallBackLsn(OrderDetailsFragment.this);
                Bundle bundle = new Bundle();
                bundle.putString("RemarkTip", remark_tv != null ? remark_tv.getText().toString() : "");
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, fragment, RemarkFragment.class.getName());
            }
        }
    };

    void addFooter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        String delievery = buildComboDeliveryDate();
        if (TextUtils.isEmpty(delievery)) {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:  本周五配送");
        } else {
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:  本周" + delievery + "配送");
        }
        int part = mOrderController.getItemsCount();
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);

        addSparesFooter();
    }

    /**
     * 添加备选菜
     */
    void addSparesFooter() {
        if (mOrderController != null && mOrderController.getOptionItems() != null && mOrderController.getOptionItems().size() > 0) {
            mLv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.remark_footer_ly, null), null, false);
            for (int i = 0; i < mOrderController.getOptionItems().size(); i++) {
                View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_item_ly, null);
                ImageView goodImg =  (ImageView) footerView.findViewById(R.id.goods_img);
                ImageLoader.getInstance().displayImage(mOrderController.getOptionItems().get(i).imgs[0]+"?imageview2/2/w/180",goodImg);
                ((TextView) footerView.findViewById(R.id.goods_name)).setText(mOrderController.getOptionItems().get(i).title);
                ((TextView) footerView.findViewById(R.id.goods_price)).setText(Utils.unitConversion(mOrderController.getOptionItems().get(i).packw) + "/份");
                footerView.findViewById(R.id.goods_count).setVisibility(View.GONE);
                mLv.addFooterView(footerView, null, false);
            }
        }
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

    Bundle buildBundle(String orderno, int orderMoney, String alipay, boolean isRecoDishes, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, isRecoDishes, titleId, false);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    void modifyOrder(final String orderno) {
        RequestParams params = new RequestParams();
        params.add("orderno", orderno);
        String items = mOrderController.getOrderItemsString();
        params.add("items", items);
        params.add("memo", remark_tv.getText().toString());
        params.add("spares", mOrderController.getOrderOptionItemString());
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "cai/altorder", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (jObj != null) {
                            String errmsg = jObj.optString("errmsg");
                            if (TextUtils.isEmpty(errmsg)) {
                                gotoFragmentByAdd(buildBundle(orderno, getOrderMoney(), "", true, R.string.order_result), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                            ToastHelper.showShort(getActivity(), errmsg);
                        }
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
                ToastHelper.showShort(getActivity(), "修改订单失败,错误码" + sCode);
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
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("items", mOrderController.getOrderItemsString());
        params.add("memo", remark_tv.getText().toString());
        params.add("orderno", buildOrderCon());
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "cai/choose", params, new AsyncHttpResponseHandler() {

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
                                gotoFragmentByAdd(buildBundle(entry.orderno, getOrderMoney(), entry.alipay, true, R.string.order_result), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                                return;
                            }
                        } else {
                            ToastHelper.showShort(getActivity(), entry.errmsg);
                        }
                    }
                } else {
                    ToastHelper.showShort(getActivity(), "异常：状态" + sCode);
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "创建订单失败.错误码" + sCode);
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

    ComboEntry entry;
    TextView titleTv;
    TextView commitOrderTv;
    UserLoginEntry uEntry;
    OrderDetailsAdapter adapter;
    ListView mLv;
    View back;
    TextView address;
    TextView rightTv;
    /**
     * 价格
     */
    TextView shop_cart_total_price_tv;
    TextView shop_cart_surpport_now_pay_tv;
    DisheDataCenter mOrderController = DisheDataCenter.getInstance();
    View remark_ly;
    TextView remark_tv;
}
