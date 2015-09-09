package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.DelayEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

/**
 * Created by cong on 15/9/7.
 */
public class OrderDelayFragment extends BaseFragment {
    private TextView titleTv, delayTv;
    private View leftIv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_delay, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestDelayState();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        titleTv = (TextView) v.findViewById(R.id.title_center_text);
        delayTv = (TextView) v.findViewById(R.id.delay_tv);
        titleTv.setText(R.string.order_delay_delivery);
        leftIv = v.findViewById(R.id.back);
    }

    @Override
    protected void setWidgetLsn() {
        leftIv.setOnClickListener(onClickListener);
        titleTv.setOnClickListener(onClickListener);
        delayTv.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == leftIv) {
                popBackStack();
            } else if (v == delayTv) {
                alertDelay();
            }
        }
    };

    private void alertDelay() {
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
                        requestDelayOrder();
                    }
                });
    }

    private void requestDelayState() {
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/delay", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                DelayEntry entry = JsonUtilsParser.fromJson(result, DelayEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        successDelay(entry.delayed);
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

    private void requestDelayOrder() {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/delay", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                DelayEntry entry = JsonUtilsParser.fromJson(result, DelayEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        successDelay(entry.delayed);
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

    private void successDelay(boolean delay) {
        if (delay) {
            delayTv.setBackgroundResource(R.drawable.btn_bg_gray_selector);
            delayTv.setEnabled(false);
        } else {
            delayTv.setBackgroundResource(R.drawable.btn_bg_red_selector);
            delayTv.setEnabled(true);
        }
    }
}
