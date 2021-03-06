package com.lynp.ui.fragment;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lynp.R;
import com.lynp.ui.data.SmsCodeEntry;
import com.lynp.ui.data.UserLoginEntry;
import com.lynp.ui.util.CacheManager;
import com.lynp.ui.util.PersistanceManager;
import com.lynp.ui.util.ProgressDlg;
import com.lynp.ui.util.DeviceInfo;
import com.lynp.ui.util.HttpRequestUtil;
import com.lynp.ui.util.IntentUtil;
import com.lynp.ui.util.JsonUtilsParser;
import com.lynp.ui.util.LocalParams;
import com.lynp.ui.util.TimeCount;
import com.lynp.ui.util.ToastHelper;
import com.lynp.ui.util.Utils;
//import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 登录页面
 * Created by apple on 15/8/6.
 */
public class LoginAllFragment extends BaseFragment {
    private boolean isPwdLogin;
    @Bind(R.id.login_tv)
    TextView loginTv;
    @Bind(R.id.change_login_tv)
    TextView changeLoginTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_all_ly, container, false);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
//        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.login);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTime();
    }

    @Override
    protected void setWidgetLsn() {
        getBaseAct().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        input_mobile_tel.addTextChangedListener(textWatcher);
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

    @OnClick(R.id.change_login_tv)
    void changeLogin() {
        changeView();
    }

    private void changeView() {
        if (!isPwdLogin) {
            obtain_verification_code.setVisibility(View.GONE);
            sms_code_et.setHint(R.string.hint_password);
            changeLoginTv.setText("手机登录");
            sms_code_et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            isPwdLogin = true;
        } else {
            obtain_verification_code.setVisibility(View.VISIBLE);
            sms_code_et.setHint(R.string.sms_code);
            sms_code_et.setInputType(InputType.TYPE_CLASS_NUMBER);
            changeLoginTv.setText("密码登录");
            isPwdLogin = false;
        }
    }

    @OnClick(R.id.back)
    void back(View v) {
        Utils.hideVirtualKeyboard(getBaseAct(), v);
        popBackStack();
    }

    @OnClick(R.id.obtain_verification_code)
    void doGetSmsCode() {
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

    @OnClick(R.id.login_tv)
    void doLogin(View v) {
        final String mobileNumber = input_mobile_tel.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 11) {
            ToastHelper.showShort(getBaseAct(), R.string.mobile_phone_error);
            return;
        }
        final String smsCode = sms_code_et.getText().toString();
        if (TextUtils.isEmpty(smsCode)) {
            String tip;
            if (isPwdLogin) {
                tip = "请输入密码";
            } else {
                tip = "请输入合法的验证码";
            }
            Toast.makeText(getActivity(), tip, Toast.LENGTH_LONG).show();
            return;
        }
        Utils.hideVirtualKeyboard(getBaseAct(), v);
        String xXsrfToken = PersistanceManager.getCookieValue(getBaseAct());
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        //debug模式则是密码登录
//        isPwdLogin = DeviceInfo.isDebuggable(getActivity());
        if (isPwdLogin) {
            Log.e("login", "密码登录");
            params.add("password", smsCode);
        } else {
            Log.e("login", "验证码登录");
            params.add("smscode", smsCode);
        }
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
//                                IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
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

//    private void saveUserLogin(){//调试用
//        CacheManager cacheManager = new CacheManager(getActivity());
//        UserLoginEntry userLoginEntry = cacheManager.getUserLoginEntry();
//        userLoginEntry.haspwd = false;
//        String json = JsonUtilsParser.toJson(userLoginEntry);
//        cacheManager.saveUserLoginToDisk(json.getBytes());
//    }

    /**
     * 获取验证码
     */
    void doGetSnsCode() {
        final String mobileNumber = input_mobile_tel.getText().toString();
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() > 11 || mobileNumber.length() < 11) {
            ToastHelper.showShort(getBaseAct(), R.string.mobile_phone_error);
            return;
        }
        Utils.hideVirtualKeyboard(getBaseAct(), sms_code_et);
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        params.add("type", 5 + "");
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "util/smscode", params, new AsyncHttpResponseHandler() {
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
                            ToastHelper.showShort(getBaseAct(), sEntry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ToastHelper.showShort(getBaseAct(), "错误验证码" + statusCode);
                stopTime();
            }
        });
    }

    void startTime() {
        if (tCount == null) {
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
