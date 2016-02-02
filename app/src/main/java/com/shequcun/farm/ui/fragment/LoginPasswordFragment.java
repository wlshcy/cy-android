package com.shequcun.farm.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.HttpRequestUtil;
import com.lynp.ui.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;
//import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 登录页面
 * Created by apple on 15/8/6.
 */
public class LoginPasswordFragment extends BaseFragment {
    @Bind(R.id.mobile_et)
    EditText mobileEt;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.login_tv)
    TextView loginTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_password_ly, container, false);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
    }

    @Override
    protected void setWidgetLsn() {
        getBaseAct().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mobileEt.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0)
                enableLoginBtn();
            else
                disableLoginBtn();
        }
    };

    private void enableLoginBtn() {
        loginTv.setBackgroundColor(getResources().getColor(R.color.green_11C258));
        loginTv.setTextColor(Color.WHITE);
        loginTv.setEnabled(true);
    }

    private void disableLoginBtn() {
        loginTv.setBackgroundResource(R.drawable.edit_bg);
        loginTv.setEnabled(false);
        loginTv.setTextColor(getResources().getColor(R.color.gray_cecece));
    }

//    @OnClick(R.id.back)
//    void back(View v) {
//        Utils.hideVirtualKeyboard(getBaseAct(), v);
//        popBackStack();
//    }

    @OnClick(R.id.login_tv)
    void doGetSmsCode(final View v) {
        String xsrf = PersistanceManager.getCookieValue(getActivity());
        if (!TextUtils.isEmpty(xsrf)) {
            doLogin(v);
            return;
        }
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(
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
                                PersistanceManager.saveCookieValue(getBaseAct(), h.getValue());
                                doLogin(v);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    }
                });
    }

    void doLogin(View v) {
        final String mobileNumber = mobileEt.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            ToastHelper.showShort(getBaseAct(), R.string.mobile_phone_error);
            return;
        }
        final String smsCode = passwordEt.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            ToastHelper.showShort(getBaseAct(), "");
            return;
        }
        Utils.hideVirtualKeyboard(getBaseAct(), v);
        String xXsrfToken = PersistanceManager.getCookieValue(getBaseAct());
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        params.add("password", smsCode);
        params.add("_xsrf", xXsrfToken);
        if (!TextUtils.isEmpty(xXsrfToken)) {
            final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "登录中...");
            HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl()
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
                                new CacheManager(getBaseAct()).saveUserLoginToDisk(body);
                                IntentUtil.sendUpdateMyInfoMsg(getBaseAct());
                                IntentUtil.sendUpdateComboMsg(getBaseAct());
                                IntentUtil.sendUpdateShoppingCartMsg(getBaseAct());
                                popBackStack();
                                //umeng统计当用户使用自有账号登录时
//                                MobclickAgent.onProfileSignIn(lEntry.id + "");
                                return;
                            } else {
                                ToastHelper.showShort(getBaseAct(), lEntry.errmsg);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    ToastHelper.showShort(getBaseAct(), R.string.login_fail);
                }
            });
        }
    }
}
