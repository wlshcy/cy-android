package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.ZoneEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.PhoneUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 管理收获地址
 * Created by apple on 15/8/5.
 */
public class AddressFragment extends BaseFragment {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_ly, container, false);
    }

    @Override
    protected void initWidget(View v) {
        name_edit = (EditText) v.findViewById(R.id.name_edit);
        mobile_phone_edit = (EditText) v.findViewById(R.id.mobile_phone_edit);
        choose_zone_tv = (TextView) v.findViewById(R.id.choose_zone_tv);
//        choose_zone_tv = (TextView) v.findViewById(R.id.choose_zone_tv);
        choose_zone_ll = v.findViewById(R.id.choose_zone_ll);
        addressDetailEt = (EditText) v.findViewById(R.id.building_number_edit);
//        house_number_edit = (EditText) v.findViewById(R.id.house_number_edit);
//        unit_number_edit = (EditText) v.findViewById(R.id.unit_number_edit);
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.receiveing_address);
        commit = (TextView) v.findViewById(R.id.title_right_text);
        commit.setTextColor(getResources().getColor(R.color.green_2bc36c));
        commit.setText(R.string.save);
        doRegisterRefreshBrodcast();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            entry = (AddressEntry) bundle.getSerializable("AddressEntry");
            if (entry != null) {
                setWidgetContent(entry);
            }
        }
    }

    @Override
    protected void setWidgetLsn() {
//        choose_zone_tv.setOnClickListener(onClick);
        back.setOnClickListener(onClick);
        commit.setOnClickListener(onClick);
//        choose_zone_tv.setOnClickListener(onClick);
        choose_zone_ll.setOnClickListener(onClick);
//        requestUserAddress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }


    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            Utils.hideVirtualKeyboard(getActivity(), v);
            if (v == choose_zone_ll)
                gotoFragmentByAdd(R.id.mainpage_ly, new SearchFragment(), SearchFragment.class.getName());
            else if (v == back)
                popBackStack();
            else if (v == commit)
                upLoadAddressToServer();
            //(R.id.mainpage_ly, new ComboMongoliaLayerFragment(), ComboMongoliaLayerFragment.class.getName());
        }
    };

    void upLoadAddressToServer() {
        name = name_edit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastHelper.showShort(getActivity(), R.string.input_name);
            return;
        }
        mobile = mobile_phone_edit.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            ToastHelper.showShort(getActivity(), R.string.input_mobile_phone);
            return;
        }
        if (!PhoneUtil.isPhone(mobile)) {
            ToastHelper.showShort(getActivity(), R.string.common_phone_format_error);
            return;
        }

        community = choose_zone_tv.getText().toString().trim();
        if (TextUtils.isEmpty(community)) {
            ToastHelper.showShort(getActivity(), R.string.choose_community);
            return;
        }

        detailAddr = addressDetailEt.getText().toString().trim();

        if (TextUtils.isEmpty(detailAddr)) {
            ToastHelper.showShort(getActivity(), R.string.input_building_number);
            return;
        }

//        if (!checkDiff()) {
//            ToastHelper.showShort(getActivity(), R.string.duplicate_address_content);
//            return;
//        }


//        String union_NO = unit_number_edit.getText().toString();
//
//        if (TextUtils.isEmpty(union_NO)) {
//            ToastHelper.showShort(getActivity(), R.string.input_union_number);
//            return;
//        }
//
//        String house_NO = house_number_edit.getText().toString();
//
//        if (TextUtils.isEmpty(house_NO)) {
//            ToastHelper.showShort(getActivity(), R.string.input_house_number);
//            return;
//        }

        final RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.put("name", name);
        params.put("mobile", mobile);
        if (entry != null)
            params.put("id", entry.id);
        params.put("zid", entry == null ? "0" : "" + entry.zid);
        params.put("zname", community);
        params.put("bur", detailAddr);
//        if (!TextUtils.isEmpty(entry.street)) {
//            params.put("street", entry.street);
//            params.put("region", "");
//            params.put("city", "");
//            params.put("zname", "");
//            params.put("building", "");
//            params.put("unit", "");
//            params.put("room", "");
//        } else {
//            params.put("street", "");
//            params.put("zid", entry == null ? "0" : "" + entry.zid);
//            params.put("region", entry == null ? "" : entry.region);
//            params.put("city", entry == null ? "北京市" : entry.city);
//            params.put("zname", community);
//            params.put("building", detailAddr);
////            params.put("unit", union_NO);
////            params.put("room", house_NO);
//        }
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "user/v2/address", params, new AsyncHttpResponseHandler() {
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
            public void onSuccess(int sCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    try {
                        JSONObject jObj = new JSONObject(new String(data));
                        if (jObj != null) {
                            String errmsg = jObj.optString("errmsg");
                            if (TextUtils.isEmpty(errmsg)) {
                                AddressEntry entry = new AddressEntry();
                                entry.address = jObj.optString("address");
                                entry.name = name;
                                entry.mobile = mobile;
                                updateUserInfo(entry);
                                return;
                            }
                            ToastHelper.showShort(getActivity(), errmsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "提交失败，错误码" + sCode);
            }
        });
    }

//    private boolean checkDiff() {
//        if (entry == null) return true;
//        if (!TextUtils.isEmpty(entry.name) && !entry.name.equals(name))
//            return true;
//        if (!TextUtils.isEmpty(entry.mobile) && !entry.mobile.equals(mobile))
//            return true;
//        if (!TextUtils.isEmpty(entry.zname) && znameDiff)
//            return true;
//        if (!TextUtils.isEmpty(entry.bur) && !entry.bur.equals(detailAddr))
//            return true;
//        return false;
//    }

    void updateUserInfo(AddressEntry entry) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        if (manager != null) {
            List<Fragment> aList = manager.getFragments();
            if (aList != null && aList.size() > 0) {
                int length = aList.size();
                for (int i = 1; i < length; i++) {
                    Fragment fragment = aList.get(i);
                    if (fragment != null && fragment instanceof PayComboFragment) {
                        ((PayComboFragment) fragment).setAddressWidgetContent(entry);
                        break;
                    }
                }
            }
        }
        IntentUtil.sendUpdateAddressRequest(getActivity());
        popBackStack();
    }

    /**
     * 查找用户默认地址
     *
     * @param aList
     */
    void buildDefaultAddress(List<AddressEntry> aList) {
        if (aList == null || aList.size() <= 0) {
            entry = new AddressEntry();
            return;
        }
        int size = aList.size();
        for (int i = 0; i < size; i++) {
            AddressEntry tmpEntry = aList.get(i);
            if (tmpEntry != null && tmpEntry.isDefault) {
                entry = tmpEntry;
                break;
            }
        }
        if (entry == null)
            return;
        setWidgetContent(entry);
    }


    void setWidgetContent(AddressEntry entry) {
        if (!TextUtils.isEmpty(entry.name))
            name_edit.setText(entry.name);
        if (!TextUtils.isEmpty(entry.mobile))
            mobile_phone_edit.setText(entry.mobile);
        if (!TextUtils.isEmpty(entry.zname))
            choose_zone_tv.setText(entry.zname);
        if (!TextUtils.isEmpty(entry.bur))
            addressDetailEt.setText(entry.bur);
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.youcai.refresh.myaddress");
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    boolean mIsBind = false;

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals("com.youcai.refresh.myaddress")) {
                ZoneEntry zEntry = (ZoneEntry) intent.getSerializableExtra("ZoneEntry");
                if (zEntry != null)
                    setWidgetContent(zEntry);
                String details_address = intent.getStringExtra("details_address");
                if (!TextUtils.isEmpty(details_address)){
                    entry.zname = details_address;
                    entry.zid = 0;
                    entry.city = null;
                    entry.region = null;
                    choose_zone_tv.setText(details_address);
                }
//                setWidgetContent(entry);
//                    }
//                }
            }
        }
    };

    void setWidgetContent(ZoneEntry zEntry) {
        if (entry == null)
            entry = new AddressEntry();
        if (!TextUtils.isEmpty(entry.name) && !entry.name.equals(zEntry.name)) znameDiff = true;
        entry.zname = zEntry.name;
        entry.zid = zEntry.id;
        choose_zone_tv.setText(zEntry.name);
    }

    private AddressEntry entry = null;
    /**
     * 门牌号
     */
//    EditText house_number_edit;
    /**
     * 姓名
     */
    EditText name_edit;
    /**
     * 手机号
     */
    EditText mobile_phone_edit;
    TextView choose_zone_tv;
//    TextView choose_zone_tv;
    View choose_zone_ll;
    /**
     * 楼号
     */
    EditText addressDetailEt;
    /**
     * 单元号
     */
//    EditText unit_number_edit;
    View back;
    TextView commit;
    private boolean znameDiff;
    private String name, mobile, community, detailAddr;
}
