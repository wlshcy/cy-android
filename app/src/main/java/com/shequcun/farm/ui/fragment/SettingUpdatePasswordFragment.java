package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shequcun.farm.R;

import butterknife.Bind;
import butterknife.OnClick;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_pwd_ly, null);
        return view;
    }

    @Override
    protected void setWidgetLsn() {

    }

    @OnClick(R.id.back)
    public void back() {
        popBackStack();
    }

    @OnClick(R.id.confirm_update_tv)
    public void updatePassword() {

    }

    @OnClick(R.id.get_smscode_btn)
    public void getSmscode() {

    }

    @Override
    protected void initWidget(View v) {
        titleCenterText.setText("修改密码");
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
