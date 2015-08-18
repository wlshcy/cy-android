//package com.shequcun.farm.datacenter;
//
//import android.app.Activity;
//import android.text.TextUtils;
//
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//import com.shequcun.farm.R;
//import com.shequcun.farm.data.OrderListEntry;
//import com.shequcun.farm.ui.adapter.MyOrderViewPagerAdapter;
//import com.shequcun.farm.util.HttpRequestUtil;
//import com.shequcun.farm.util.JsonUtilsParser;
//import com.shequcun.farm.util.LocalParams;
//import com.shequcun.farm.util.ToastHelper;
//
//import org.apache.http.Header;
//
///**
// * 我的订单数据中心
// * Created by apple on 15/8/17.
// */
//public class MyOrderDataCenter {
//    private Activity mAct;
//
//    public MyOrderDataCenter(Activity act) {
//        this.mAct = act;
//    }
//
//    /**
//     * 请求我的订单
//     *
//     * @param orderAdapter
//     * @param lastid
//     * @param length
//     */
//    public void requestMyOrder(final MyOrderViewPagerAdapter orderAdapter, final int lastid, final int length) {
//        RequestParams params = new RequestParams();
//        params.add("lastid", lastid + "");
//        params.add("length", length + "");
//        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int sCode, Header[] h, byte[] data) {
//                if (data != null && data.length > 0) {
//                    OrderListEntry entry = JsonUtilsParser.fromJson(new String(data), OrderListEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
//                            beArranged(orderAdapter, entry);
//                        } else {
//                            ToastHelper.showShort(mAct, entry.errmsg);
//                        }
//                        beArranged(orderAdapter, entry);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] responseBody, Throwable error) {
//                if (sCode == 0) {
//                    ToastHelper.showShort(mAct, R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(mAct, "请求失败,错误码:" + sCode);
//            }
//        });
//    }
//
//    /**
//     * 对订单进行分类
//     *
//     * @param orderAdapter
//     * @param entry
//     */
//    void beArranged(final MyOrderViewPagerAdapter orderAdapter, OrderListEntry entry) {
//        orderAdapter.buildOrderListEntry(entry);
//    }
//
//
//}
