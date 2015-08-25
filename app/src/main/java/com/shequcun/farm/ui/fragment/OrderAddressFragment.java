package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by cong check_turn_on 15/7/23.
 */
public class OrderAddressFragment extends BaseFragment {
    private TextView phoneTv, addressTv;
    private ImageView editIv;
    private View addressRl;
    private String addressJson;
    private ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_address, null);
        return v;
    }

    @Override
    protected void initWidget(View view) {
        phoneTv = (TextView) view.findViewById(R.id.phone_tv);
        addressTv = (TextView) view.findViewById(R.id.address_tv);
        TextView title = (TextView) view.findViewById(R.id.title_center_text);
        title.setText(R.string.title_receive_address);
        addressRl = view.findViewById(R.id.address_rl);
        editIv = (ImageView) view.findViewById(R.id.eidt_iv);
        backBtn = (ImageView) view.findViewById(R.id.back);
    }

    @Override
    protected void setWidgetLsn() {
        backBtn.setOnClickListener(mOnClickListener);
        addressRl.setOnClickListener(mOnClickListener);
        editIv.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestAddress();
    }

    private AvoidDoubleClickListener mOnClickListener = new AvoidDoubleClickListener() {

        @Override
        public void onViewClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.back) {
                backShopcartFrgm();
            } else if (v == editIv) {
                gotoAddressEditFrgm();
            } else if (v == addressRl) {
                backShopcartFrgm();
            }
        }
    };

    private void backShopcartFrgm() {
        popBackStack();
    }

    private void gotoAddressEditFrgm() {
//        Bundle bundle = new Bundle();
//        bundle.putString("addressJson",addressJson);
        AddressFragment fragment = new AddressFragment();
        gotoFragmentByAdd(fragment, AddressFragment.class);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void requestAddress() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (pDlg != null)
                    pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pDlg != null)
                    pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            successAddress(entry.aList);
                            return;
                        } else {
                            ToastHelper.showShort(getActivity(), entry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + sCode);
            }
        });
    }


    private void successAddress(List<AddressEntry> list) {
        for (AddressEntry entry : list) {
            if (entry.isDefault) {
                phoneTv.setText(entry.mobile);
                addressTv.setText(parseAddress(entry));
            }
        }
//        logger.error(addressEntry.toString());
//        if (TextUtils.isEmpty(addressEntry.errcode)) {
//            addressJson = json;
//            String address = parseAddress(addressEntry);
//            String mobile = addressEntry.mobile;
////            logger.error("mobile:"+mobile+"address:"+address);
//            if(TextUtils.isEmpty(mobile)|| TextUtils.isEmpty(address)){
//                return;
//            }
//            updateAddressUi(mobile,address);
//        } else {
//            if ("4000".equals(addressEntry.errcode)){
////							没有查到地址信息
//                popBackStack();
//                gotoAddressEditFrgm();
//            }else{
//                ToastHelper.showShort(getFragmentActivity(), addressEntry.errmsg);
//            }
//        }
    }

    private String parseAddress(AddressEntry addressEntry) {
        StringBuilder sb = new StringBuilder();
        String building = addressEntry.building
                + getResources().getString(R.string.common_num_building);
        String unit = addressEntry.unit
                + getResources().getString(R.string.common_unit);
        String door = getResources().getString(R.string.common_door_num)
                + addressEntry.room;
        sb.append(addressEntry.zname);
        sb.append(building);
        if (!TextUtils.isEmpty(addressEntry.unit)) {
            sb.append(unit);
        }
        sb.append(door);
        return sb.toString();
    }

    private void updateAddressUi(String phone, String address) {
        phoneTv.setText(phone);
        phoneTv.setVisibility(View.VISIBLE);
        addressTv.setText(address);
        addressTv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
