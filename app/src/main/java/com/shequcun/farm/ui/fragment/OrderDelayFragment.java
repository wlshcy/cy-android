package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
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
import com.shequcun.farm.data.DelayEntry;
import com.shequcun.farm.data.DelayItemEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.DelayAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/9/7.
 */
public class OrderDelayFragment extends BaseFragment {
    @Bind(R.id.title_center_text)
    TextView titleTv;
    @Bind(R.id.comboLv)
    ListView comboLv;
    @Bind(R.id.emptyTv)
    TextView emptyTv;
    private DelayAdapter delayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_delay, null);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        titleTv.setText(R.string.order_delay_delivery);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.delay_list_header_textview, null);
        comboLv.addHeaderView(view);
        requestGetDelayState(null);
    }

    @Override
    protected void setWidgetLsn() {
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

//    @OnClick(R.id.delay_tv)
//    void doClick() {
//        if (TextUtils.isEmpty(orderNo)) {
//            ToastHelper.showShort(getActivity(), R.string.you_have_not_buy_combo);
//            return;
//        }
//        alertDelay();
//    }

    private void alertDelay(final String orderNo) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText(R.string.prompt_order_delivery);
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
                        requestPostDelayOrder(orderNo);
                    }
                });
    }

    private String readOrderNoFromDisk() {
        UserLoginEntry userLoginEntry = new CacheManager(getActivity()).getUserLoginEntry();
        return userLoginEntry == null ? null : userLoginEntry.orderno;
    }

//    private void requestMycombo() {
//        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/mycombo", new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String result = new String(responseBody);
//                MyComboEntry entry = JsonUtilsParser.fromJson(result, MyComboEntry.class);
//                if (entry != null) {
//                    if (TextUtils.isEmpty(entry.errcode)) {
//                        if (entry.combos != null && !entry.combos.isEmpty())
////                            requestGetDelayState(entry.combos.get(0).con);
//                            successMyCombo(entry.combos);
//                        else
//                            disableDelayView(R.string.you_have_not_buy_combo);
//                    } else {
//                        ToastHelper.showShort(getActivity(), entry.errmsg);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                disableDelayView(R.string.btn_delay_a_week_delivery);
//                if (statusCode == 0) {
//                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(getActivity(), "请求失败,错误码" + statusCode);
//            }
//        });
//    }

//    private void successMyCombo(List<ComboEntry> list) {
//        if (delayAdapter == null) {
//            delayAdapter = new DelayAdapter(getActivity());
//            delayAdapter.setDelayClick(delayClick);
//            comboLv.setAdapter(delayAdapter);
//        }
//        delayAdapter.add(list);
//    }

    private DelayAdapter.DelayClick delayClick = new DelayAdapter.DelayClick() {
        @Override
        public void onDelay(DelayItemEntry entry) {
            if (entry == null) return;
            alertDelay(entry.orderno);
        }
    };

    private void requestGetDelayState(String orderNo) {
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(orderNo))
            params.add("orderno", orderNo);
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/delay", params, new AsyncHttpResponseHandler() {

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
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                DelayEntry entry = JsonUtilsParser.fromJson(result, DelayEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        successDelayInfo(entry);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + statusCode);
            }
        });
    }

    private void successDelayInfo(DelayEntry entry) {
        if (entry.data == null || entry.data.isEmpty()) {
            comboLv.setEmptyView(emptyTv);
            return;
        }
        if (delayAdapter == null) {
            delayAdapter = new DelayAdapter(getActivity());
            delayAdapter.setDelayClick(delayClick);
            comboLv.setAdapter(delayAdapter);
        }
        delayAdapter.add(entry.data);
    }

    private void requestPostDelayOrder(String orderNo) {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("orderno", orderNo);
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "cai/delay", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                DelayEntry entry = JsonUtilsParser.fromJson(result, DelayEntry.class);
                if (entry != null && TextUtils.isEmpty(entry.errmsg)) {
                    successDelay(true);
                }
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

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + statusCode);
            }
        });
    }

    private void successDelay(boolean delay) {
        requestGetDelayState(null);
    }
}
