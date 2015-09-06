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
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.Utils;

/**
 * Created by apple on 15/8/13.
 */
public class FillInAddressFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fill_in_address_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.fill_in_address);
        community_name_edit = (EditText) v.findViewById(R.id.community_name_edit);
        address_et = (EditText) v.findViewById(R.id.address_et);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
    }

    View.OnClickListener onClick = new  View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                Utils.hideVirtualKeyboard(getActivity(), v);
                String comName = community_name_edit.getText().toString();
                String addresses = address_et.getText().toString();

                if (TextUtils.isEmpty(comName) || TextUtils.isEmpty(addresses))
                    popBackStack();
                else if (!TextUtils.isEmpty(comName) && !TextUtils.isEmpty(addresses)) {
                    IntentUtil.sendUpdateMyAddressMsg(getActivity(), comName, addresses);
                    popBackStack();
                    popBackStack();
                    popBackStack();
//                    clearStack();
//                    gotoFragment(buildBundle(comName, addresses), R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
                }
            }
        }
    };

    Bundle buildBundle(String communityName, String addresses) {
        Bundle bundle = new Bundle();
        bundle.putString("Community_Name", communityName);
        bundle.putString("Details_Address", addresses);
        return bundle;
    }

    View back;
    EditText community_name_edit;
    EditText address_et;
}
