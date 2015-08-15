package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.data.ZoneEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
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
        house_number_edit = (EditText) v.findViewById(R.id.house_number_edit);
        name_edit = (EditText) v.findViewById(R.id.name_edit);
        mobile_phone_edit = (EditText) v.findViewById(R.id.mobile_phone_edit);
        community_tv = (TextView) v.findViewById(R.id.community_tv);
        building_number_edit = (EditText) v.findViewById(R.id.building_number_edit);
        unit_number_edit = (EditText) v.findViewById(R.id.unit_number_edit);
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.receiveing_address);
        commit = (TextView) v.findViewById(R.id.title_right_text);
        commit.setText(R.string.commit);
        doRegisterRefreshBrodcast();
    }

    @Override
    protected void setWidgetLsn() {
        community_tv.setOnClickListener(onClick);
        back.setOnClickListener(onClick);
        commit.setOnClickListener(onClick);
        requestUserAddress();
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
            if (v == community_tv)
                gotoFragmentByAdd(R.id.mainpage_ly, new NearbyCommunityFragment(), NearbyCommunityFragment.class.getName());
            else if (v == back)
                popBackStack();
            else if (v == commit) {
                upLoadAddressToServer();
                //(R.id.mainpage_ly, new ComboMongoliaLayerFragment(), ComboMongoliaLayerFragment.class.getName());
            }
        }
    };


    void upLoadAddressToServer() {
        if (entry == null)
            return;
        String name = name_edit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastHelper.showShort(getActivity(), R.string.input_name);
            return;
        }
        String phone_number = mobile_phone_edit.getText().toString();

        if (TextUtils.isEmpty(phone_number)) {
            ToastHelper.showShort(getActivity(), R.string.input_mobile_phone);
            return;
        }

        String community = community_tv.getText().toString();
        if (TextUtils.isEmpty(community)) {
            ToastHelper.showShort(getActivity(), R.string.choose_community);
            return;
        }

        String building_NO = building_number_edit.getText().toString();

        if (TextUtils.isEmpty(building_NO)) {
            ToastHelper.showShort(getActivity(), R.string.input_building_number);
            return;
        }

        String union_NO = unit_number_edit.getText().toString();

        if (TextUtils.isEmpty(union_NO)) {
            ToastHelper.showShort(getActivity(), R.string.input_union_number);
            return;
        }

        String house_NO = house_number_edit.getText().toString();

        if (TextUtils.isEmpty(house_NO)) {
            ToastHelper.showShort(getActivity(), R.string.input_house_number);
            return;
        }

        final RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.INSTANCE.getCookieValue());
        params.put("name", name);
        params.put("mobile", phone_number);
        params.put("city", entry == null ? "北京市" : entry.city);
        params.put("region", entry == null ? "" : entry.region);
        params.put("zid", entry == null ? "0" : "" + entry.zid);
        params.put("zname", community);
        params.put("building", building_NO);
        params.put("unit", union_NO);
        params.put("room", house_NO);
        params.put("id", entry.id);
        if (!TextUtils.isEmpty(entry.street)) {
            params.put("street", entry.street);
        }
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpPost(LocalParams.INSTANCE.getBaseUrl() + "user/address", params, new AsyncHttpResponseHandler() {
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
                                updateUserInfo(jObj.optString("address"));
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

    void updateUserInfo(String address) {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data == null || data.length <= 0)
            return;
        UserLoginEntry uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        if (uEntry == null)
            return;
        uEntry.name = name_edit.getText().toString();
        uEntry.address = address;
        new CacheManager(getActivity()).saveUserLoginToDisk(JsonUtilsParser.toJson(uEntry).getBytes());
        popBackStack();
    }

    void requestUserAddress() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {
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
                            buildDefaultAddress(entry.aList);
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

    /**
     * 查找用户默认地址
     *
     * @param aList
     */
    void buildDefaultAddress(List<AddressEntry> aList) {
        if (aList == null || aList.size() <= 0)
            return;
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
        name_edit.setText(entry.name);
        mobile_phone_edit.setText(entry.mobile);
        community_tv.setText(entry.zname);
        building_number_edit.setText(entry.building);
        unit_number_edit.setText(entry.unit);
        house_number_edit.setText(entry.room);
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
                if (zEntry != null) {
                    setWidgetContent(zEntry);
                } else {
                    if (entry != null) {
                        entry.name = intent.getStringExtra("community_name");
                        entry.street = intent.getStringExtra("details_address");
                        setWidgetContent(entry);
                    }
                }
            }
        }
    };

    void setWidgetContent(ZoneEntry zEntry) {
        if (entry != null) {
            entry.region = zEntry.region;
            entry.city = zEntry.city;
            entry.zname = zEntry.name;
            entry.zid = zEntry.id;
        }
        community_tv.setText(zEntry.name);
    }

    private AddressEntry entry = null;
    /**
     * 门牌号
     */
    EditText house_number_edit;
    /**
     * 姓名
     */
    EditText name_edit;
    /**
     * 手机号
     */
    EditText mobile_phone_edit;
    TextView community_tv;
    /**
     * 楼号
     */
    EditText building_number_edit;
    /**
     * 单元号
     */
    EditText unit_number_edit;
    View back;
    TextView commit;
}
