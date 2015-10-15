package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
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
import com.shequcun.farm.data.CouponShareEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.platform.ShareContent;
import com.shequcun.farm.platform.ShareManager;
import com.shequcun.farm.ui.adapter.RecommendAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 支付结果界面
 * Created by apple on 15/8/18.
 */
public class PayResultFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_result_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        clearStack();
        return false;
    }

    @Override
    protected void initWidget(View v) {
//        mLv = (ListView) v.findViewById(R.id.mLv);
//        back = v.findViewById(R.id.back);
        int titleId = getTitleId();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.order_result);
//        recoTv = (TextView) v.findViewById(R.id.common_small_tv);
        recoTv.setVisibility(View.GONE);
//        result_tip = (TextView) v.findViewById(R.id.result_tip);
        result_tip.setText(titleId == R.string.pay_success ? R.string.order_pay_success : R.string.order_submit_success);
    }

    @Override
    protected void setWidgetLsn() {
    }

    @OnClick(R.id.back)
    void back() {
        clearStack();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PayParams payParams = getPayParams();
        if (payParams == null) return;
        if (!TextUtils.isEmpty(payParams.orderno) && payParams.isSendRedPackage)
            requestRedPacktetShareUrl(payParams.orderno);
        if (payParams.isLast)
            showMyComboExpireAlertDlg();
    }


    boolean isRequesetRedPackage() {
        PayParams payParams = getPayParams();
        return payParams == null ? false : payParams.isSendRedPackage;
    }

    String getOrderNoFromParams() {
        PayParams payParams = getPayParams();
        return payParams == null ? null : payParams.orderno;
    }

    PayParams getPayParams() {
        Bundle bundle = getArguments();
        return bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
    }


    boolean isRecomDishes() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null) {
            return entry.isRecoDishes;
        }
        return false;
    }

    /**
     * 剩余量不足
     */
    private void alertOutOfRemains() {
        String content = getResources().getString(R.string.out_of_remains);
        alertDialog(content);
    }

    /**
     * 库存不足
     *
     * @param maxpacks
     */
    private void alertOutOfMaxpacks(int maxpacks) {
        String content = getResources().getString(R.string.out_of_maxpacks);
        content = content.replace("A", maxpacks + "");
        alertDialog(content);
    }

    private void alertDialog(String content) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setContentView(R.layout.alert_dialog);
        TextView tv = (TextView) alert.getWindow().findViewById(R.id.content_tv);
        tv.setText(content);
        alert.getWindow().findViewById(R.id.ok_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
    }

    Bundle buildBundle(RecommendEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecommendEntry", entry);
        return bundle;
    }

    private int getTitleId() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null)
            return entry.titleId;

        return R.string.pay_success;
    }

    private void alertRedPacketsShare(int count, final String url, final String title, final String content) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_redpackets_share);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView countTv = (TextView) alert.getWindow().findViewById(R.id.red_packets_count_tv);
        countTv.setText(countTv.getText().toString().replace("A", count > 0 ? count + "" : "N"));
        alert.getWindow().findViewById(R.id.share_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(url, title, content);
                alert.dismiss();
            }
        });
        alert.getWindow().findViewById(R.id.close_iv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
    }

    private void requestRedPacktetShareUrl(String orderNo) {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("orderno", orderNo);
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "cai/coupon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CouponShareEntry entry = JsonUtilsParser.fromJson(result, CouponShareEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errmsg)) {
                        alertRedPacketsShare(entry.count, entry.url, entry.title, entry.content);
                    }
//                    else {
//                        ToastHelper.showShort(getActivity(), entry.errmsg);
//                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                if (statusCode == 0) {
//                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(getActivity(), "请求失败,错误码" + statusCode);
            }
        });
    }

    private void share(String url, String title, String content) {
        ShareContent shareContent = new ShareContent();
        shareContent.setImageId(R.drawable.icon_share_redpackets_logo);
        shareContent.setTargetUrl(url);
        shareContent.setTitle(title);
        shareContent.setContent(content);
        ShareManager.shareByFrame(getActivity(), shareContent);
    }

    public void showMyComboExpireAlertDlg() {
        final android.app.AlertDialog alert = new android.app.AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.my_combo_expire_alert_ly);
        alert.getWindow().findViewById(R.id.close_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        clearStack();
                    }
                });
    }

    @Bind(R.id.common_small_tv)
    TextView recoTv;
    RecommendAdapter adapter;
    @Bind(R.id.back)
    View back;
    @Bind(R.id.result_tip)
    TextView result_tip;
}
