package com.shequcun.farm.ui.fragment;

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

/**
 * Created by cong on 15/9/7.
 */
public class AddressZoneFragment extends BaseFragment {
    private EditText zoneEt;
    private View saveTv, back;
    private String zone;
    private TextView titleTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_zone,null);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        zoneEt = (EditText) v.findViewById(R.id.zone_edit);
        saveTv = v.findViewById(R.id.save_tv);
        back = v.findViewById(R.id.back);
        titleTv = (TextView)v.findViewById(R.id.title_center_text);
        titleTv.setText(R.string.input_zone_address);
    }

    @Override
    protected void setWidgetLsn() {
        saveTv.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                popBackStack();
            } else if (v == saveTv) {
                if (checkInput()) {
                    Utils.hideVirtualKeyboard(getActivity(), v);
                    IntentUtil.sendUpdateMyAddressMsg(getActivity(),zone);
                    popBackStack();
                    popBackStack();
//                    clearStack();
//                    gotoFragment(buildBundle(comName, addresses), R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
                }
            }
        }
    };

    private boolean checkInput() {
        zone = zoneEt.getText().toString();
        if (TextUtils.isEmpty(zone)) {
            ToastHelper.showShort(getActivity(), R.string.hint_input_street_and_zone_name);
            return false;
        }
        return true;
    }
}
