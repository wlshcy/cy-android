package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by cong on 15/9/7.
 */
public class AddressZoneFragment extends BaseFragment {
    @Bind(R.id.zone_edit)
    EditText zoneEt;
    private String zone;
    @Bind(R.id.title_center_text)
    TextView titleTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_zone, null);
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
        titleTv.setText(R.string.input_zone_address);
    }

    @OnClick(R.id.save_tv)
    void doSave() {
        if (checkInput()) {
            IntentUtil.sendUpdateMyAddressMsg(getActivity(), zone);
            popBackStack();
            popBackStack();
        }
    }

    @OnClick(R.id.back)
    void back() {
        if (checkInput1())
            alertQuitEdit();
        else
            popBackStack();
    }


    private boolean checkInput() {
        zone = zoneEt.getText().toString();
        if (TextUtils.isEmpty(zone)) {
            ToastHelper.showShort(getActivity(), R.string.hint_input_street_and_zone_name);
            return false;
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideVirtualKeyboard(getActivity(), zoneEt);
    }

    private boolean checkInput1() {
        String content = zoneEt.getText().toString();
        return !TextUtils.isEmpty(content);
    }

    private void alertQuitEdit() {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText("确定退出编辑？");
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
                        popBackStack();
                    }
                });
    }
}
