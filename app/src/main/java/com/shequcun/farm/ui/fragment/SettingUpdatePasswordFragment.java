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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.BaseEntry;
import com.shequcun.farm.data.SmsCodeEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.TimeCount;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/10/23.
 */
public class SettingUpdatePasswordFragment extends BaseFragment {
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.title_center_text)
    TextView titleCenterText;
    @Bind(R.id.title_right_text)
    TextView titleRightText;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.new_password_et)
    EditText newPasswordEt;
    @Bind(R.id.smscode_et)
    EditText smscodeEt;
    @Bind(R.id.get_smscode_btn)
    Button getSmscodeBtn;
    @Bind(R.id.confirm_update_tv)
    TextView loginTv;
    private String password;
    private String smscode;
    private TimeCount tCount;
    private int maxLength = 6;
    private int length = 6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_pwd_ly, null);
        return view;
    }

    @Override
    protected void setWidgetLsn() {
        darkSmscode();
        passwordEt.addTextChangedListener(textWatcher);
        newPasswordEt.addTextChangedListener(textWatcher1);
        smscodeEt.addTextChangedListener(textWatcher2);
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
            if (s.length() > maxLength - 1 && newPasswordEt.getText().length() > maxLength - 1)
                lightSmscode();
            else
                darkSmscode();
        }
    };

    private TextWatcher textWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > maxLength - 1 && passwordEt.getText().length() > maxLength - 1)
                lightSmscode();
            else
                darkSmscode();
        }
    };
    private TextWatcher textWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0 && passwordEt.getText().length() > 0 && newPasswordEt.getText().length() > 0)
                enableUpdateBtn();
            else
                disableUpdateBtn();
        }
    };

    private void darkSmscode() {
        getSmscodeBtn.setTextColor(getResources().getColor(R.color.gray_cecece));
        getSmscodeBtn.setEnabled(false);
    }

    private void lightSmscode() {
        getSmscodeBtn.setTextColor(getResources().getColor(R.color.green_11C258));
        getSmscodeBtn.setEnabled(true);

    }

    private String checkInput() {
        String result = checkInputPwd();
        if (result != null) return result;
        smscode = smscodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(smscode)) return "请输入短信验证码";
        return null;
    }

    private String checkInputPwd() {
        password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < maxLength)
            return "请输入不少于" + maxLength + "位新密码";
        String newPassword = newPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword) || newPassword.length() < maxLength)
            return "请输入不少于" + maxLength + "位确认密码";
        if (TextUtils.isEmpty(password)) return "请输入" + length + "位新密码";
        newPassword = newPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) return "请输入" + length + "位确认密码";
        if (password.equals(newPassword) == false) return "确认密码与新密码不相同";
        return null;
    }

    @OnClick(R.id.back)
    public void back(View v) {
        Utils.hideVirtualKeyboard(getBaseAct(), v);
        popBackStack();
    }

    @OnClick(R.id.confirm_update_tv)
    public void updatePassword() {
        String result = checkInput();
        if (result != null) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        } else {
            requestChgpwd(password, smscode);
        }
    }

    @OnClick(R.id.get_smscode_btn)
    public void getSmscode() {
        String result = checkInputPwd();
        if (result != null) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            return;
        }
        UserLoginEntry userLoginEntry = new CacheManager(getActivity()).getUserLoginEntry();
        if (!TextUtils.isEmpty(userLoginEntry.mobile))
            requestSnsCode(userLoginEntry.mobile);
    }

    @Override
    protected void initWidget(View v) {
        titleCenterText.setText("修改密码");
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    void requestSnsCode(String mobileNumber) {
        RequestParams params = new RequestParams();
        params.add("mobile", mobileNumber);
        params.add("type", 6 + "");
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
            getSmscodeBtn.setTextColor(getResources().getColor(R.color.gray_cecece));
            tCount = new TimeCount(60000, 1000, getSmscodeBtn);
        }
        tCount.start();
    }

    private void stopTime() {
        if (tCount != null) {
            tCount.onFinish();
            tCount = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTime();
    }

    private void requestChgpwd(String password, String smscode) {
        RequestParams params = new RequestParams();
        params.add("smscode", smscode);
        params.add("password", password);
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "user/chgpwd", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                BaseEntry entry = JsonUtilsParser.fromJson(result, BaseEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        Utils.hideVirtualKeyboard(getBaseAct(), back);
                        Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT).show();
                        popBackStack();
                        saveUserLogin();
                        IntentUtil.sendUpdateMyInfoMsg(getActivity());
                    } else {
                        Toast.makeText(getActivity(), entry.errmsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void disableUpdateBtn() {
        loginTv.setBackgroundResource(R.drawable.edit_bg);
        loginTv.setTextColor(getResources().getColor(R.color.gray_cecece));
        loginTv.setEnabled(false);
    }

    private void enableUpdateBtn() {
        loginTv.setEnabled(true);
        loginTv.setBackgroundColor(getResources().getColor(R.color.green_11C258));
        loginTv.setTextColor(Color.WHITE);
    }

    private void saveUserLogin() {
        CacheManager cacheManager = new CacheManager(getActivity());
        UserLoginEntry userLoginEntry = cacheManager.getUserLoginEntry();
        userLoginEntry.haspwd = true;
        String json = JsonUtilsParser.toJson(userLoginEntry);
        cacheManager.saveUserLoginToDisk(json.getBytes());
    }
}
