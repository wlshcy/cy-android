package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.SmsCodeEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.TimeCount;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;
import com.umeng.analytics.MobclickAgent;


import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 登录页面
 * Created by apple on 15/8/6.
 */
public class LoginFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.login);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTime();
    }

    @Override
    protected void setWidgetLsn() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @OnClick(R.id.back)
    void back(View v) {
        Utils.hideVirtualKeyboard(getActivity(), v);
        popBackStack();
    }

    @OnClick(R.id.obtain_verification_code)
    void doGetSmsCode() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).get(
                LocalParams.getBaseUrl() + "auth/init",
                new AsyncHttpResponseHandler() {
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
                        for (Header h : headers) {
                            if (h.getName().equals("X-Xsrftoken")) {
                                PersistanceManager.saveCookieValue(getActivity(), h.getValue());
                                doGetSnsCode();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    }
                });
    }

    @OnClick(R.id.login)
    void doLogin(View v) {
        final String mobileNumber = input_mobile_tel.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            ToastHelper.showShort(getActivity(), R.string.mobile_phone_error);
            return;
        }
        final String smsCode = sms_code_et.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            ToastHelper.showShort(getActivity(), R.string.sns_code_error);
            return;
        }
        Utils.hideVirtualKeyboard(getActivity(), v);
        String xXsrfToken = PersistanceManager.getCookieValue(getActivity());
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        params.add("smscode", smsCode);
//        params.add("password", smsCode);
        params.add("_xsrf", xXsrfToken);
        if (!TextUtils.isEmpty(xXsrfToken)) {
            final ProgressDlg pDlg = new ProgressDlg(getActivity(), "登录中...");
            HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl()
                    + "auth/login", params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (pDlg != null)
                        pDlg.show();
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (pDlg != null)
                        pDlg.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] body) {
                    if (body != null && body.length > 0) {
                        UserLoginEntry lEntry = JsonUtilsParser.fromJson(
                                new String(body), UserLoginEntry.class);
                        if (lEntry != null) {
                            if (TextUtils.isEmpty(lEntry.errmsg)) {
                                new CacheManager(getActivity()).saveUserLoginToDisk(body);
                                IntentUtil.sendUpdateMyInfoMsg(getActivity());
                                IntentUtil.sendUpdateComboMsg(getActivity());
                                IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
                                popBackStack();
                                //umeng统计当用户使用自有账号登录时
                                MobclickAgent.onProfileSignIn(lEntry.id+"");
                                return;
                            } else {
                                ToastHelper.showShort(getActivity(), lEntry.errmsg);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    ToastHelper.showShort(getActivity(), R.string.login_fail);
                }
            });
        }
    }


    /**
     * 获取验证码
     */
    void doGetSnsCode() {
        final String mobileNumber = input_mobile_tel.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() > 11 || mobileNumber.length() < 11) {
            ToastHelper.showShort(getActivity(), R.string.mobile_phone_error);
            return;
        }
        Utils.hideVirtualKeyboard(getActivity(), sms_code_et);
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        params.add("type", 5 + "");
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "util/smscode", params, new AsyncHttpResponseHandler() {
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
            public void onSuccess(int statusCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    SmsCodeEntry sEntry = JsonUtilsParser.fromJson(new String(data), SmsCodeEntry.class);
                    if (sEntry != null) {
                        if (TextUtils.isEmpty(sEntry.errmsg)) {
                            startTime();
                            return;
                        } else {
                            stopTime();
                            ToastHelper.showShort(getActivity(), sEntry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ToastHelper.showShort(getActivity(), "错误验证码" + statusCode);
                stopTime();
            }
        });
    }

    void startTime() {
        if (tCount == null){
            obtain_verification_code.setTextColor(getResources().getColor(R.color.gray_3d3d3d));
            tCount = new TimeCount(60000, 1000, obtain_verification_code);
        }
        tCount.start();
    }

    private void stopTime() {
        if (tCount != null) {
            tCount.onFinish();
            tCount = null;
        }
    }

    TimeCount tCount;
    @Bind(R.id.obtain_verification_code)
    Button obtain_verification_code;
    @Bind(R.id.sms_code_et)
    EditText sms_code_et;
    @Bind(R.id.input_mobile_tel)
    EditText input_mobile_tel;
}
