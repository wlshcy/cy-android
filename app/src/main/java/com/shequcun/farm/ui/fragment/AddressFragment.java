package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.lynp.ui.fragment.PayFragment;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.BaseEntry;
import com.shequcun.farm.data.RegionListEntry;
import com.shequcun.farm.data.ZoneEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.lynp.ui.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.PhoneUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 管理收获地址
 * Created by apple on 15/8/5.
 */
public class AddressFragment extends BaseFragment {
    @Bind(R.id.save_tv)
    TextView saveTv;
    public static final int KEY_ONLY_SAVE = 1;
    private int key = 0;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_ly, container, false);
    }

    protected void initWidget(View v) {
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
            key = bundle.getInt("key");
            if (key == KEY_ONLY_SAVE)
                deleteTv.setVisibility(View.GONE);
        }

        if (bundle == null || entry == null) {
            deleteTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setWidgetLsn() {
        title_center_text.setText(R.string.receiveing_address);
//        deleteTv.setTextColor(getResources().getColor(R.color.green_2bc36c));

        deleteTv.setText("删除");
        doRegisterRefreshBrodcast();
        back.setOnClickListener(onClick);
        deleteTv.setOnClickListener(onClick);
        choose_zone_ll.setOnClickListener(onClick);
        saveTv.setOnClickListener(onClick);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }


    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            Utils.hideVirtualKeyboard(getBaseAct(), v);
            if (v == choose_zone_ll) {
                requestRegionsList();
//                gotoFragmentByAdd(R.id.mainpage_ly, new SearchFragment(), SearchFragment.class.getName());
            } else if (v == back) {
                alertQuitEdit();
//                boolean hasInput = checkHasInput();
//                if (hasInput) {
//                    return;
//                }
//                if (entry != null && checkDiff()) {
//                    alertQuitEdit();
//                    return;
//                }
//                popBackStack();

            } else if (v == deleteTv) {
                alertDelete();
            } else if (v == saveTv) {
                upLoadAddressToServer();
            }
            //(R.id.mainpage_ly, new ComboMongoliaLayerFragment(), ComboMongoliaLayerFragment.class.getName());
        }
    };

    private void alertDelete() {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText("确定删除地址？");
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
                        requestDeladdr(entry.id + "");
                        alert.dismiss();
                    }
                });
    }

    private void selectRegionAlert(final String[] array) {
        Dialog alertDialog = new AlertDialog.Builder(getBaseAct())
                .setTitle("选择区域")
                .setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        regionTv.setText(array[which]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).create();
        alertDialog.show();
    }

    void requestRegionsList() {
        RequestParams params = new RequestParams();
        params.add("range", "0");
        params.add("pid", "1");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "util/region", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    RegionListEntry rEntry = JsonUtilsParser.fromJson(new String(data), RegionListEntry.class);
                    if (rEntry != null) {
                        if (TextUtils.isEmpty(rEntry.errmsg)) {
                            successRegionList(rEntry);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getBaseAct(), "请检查您的网络后重试.");
                }
            }
        });
    }

    private void successRegionList(RegionListEntry entry) {
        if (entry.mList == null || entry.mList.isEmpty()) return;
        String regions[] = new String[entry.mList.size()];
        for (int i = 0; i < regions.length; i++) {
            regions[i] = entry.mList.get(i).name;
        }
        selectRegionAlert(regions);
    }

    void upLoadAddressToServer() {
        name = name_edit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastHelper.showShort(getBaseAct(), R.string.input_name);
            return;
        }
        mobile = mobile_phone_edit.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            ToastHelper.showShort(getBaseAct(), R.string.input_mobile_phone);
            return;
        }
        if (!PhoneUtil.isPhone(mobile)) {
            ToastHelper.showShort(getBaseAct(), R.string.common_phone_format_error);
            return;
        }

        region = regionTv.getText().toString().trim();
        if (TextUtils.isEmpty(region) || "点击选择".equals(region)) {
            ToastHelper.showShort(getBaseAct(), R.string.choose_region);
            return;
        }

        detailAddr = addressDetailEt.getText().toString().trim();

        if (TextUtils.isEmpty(detailAddr)) {
            ToastHelper.showShort(getBaseAct(), R.string.input_building_number);
            return;
        }

//        if (!checkDiff()) {
//            ToastHelper.showShort(getBaseAct(), R.string.duplicate_address_content);
//            return;
//        }


//        String union_NO = unit_number_edit.getText().toString();
//
//        if (TextUtils.isEmpty(union_NO)) {
//            ToastHelper.showShort(getBaseAct(), R.string.input_union_number);
//            return;
//        }
//
//        String house_NO = house_number_edit.getText().toString();
//
//        if (TextUtils.isEmpty(house_NO)) {
//            ToastHelper.showShort(getBaseAct(), R.string.input_house_number);
//            return;
//        }

        final RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.put("name", name);
        params.put("mobile", mobile);
        if (entry != null) {
            params.put("id", entry.id);
            params.put("city", entry.city);
        }
        params.put("region", region);
        params.put("address", detailAddr);
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
//            params.put("zname", region);
//            params.put("building", detailAddr);
////            params.put("unit", union_NO);
////            params.put("room", house_NO);
//        }
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "user/v3/address", params, new AsyncHttpResponseHandler() {
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
                            ToastHelper.showShort(getBaseAct(), errmsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "提交失败，错误码" + sCode);
            }
        });
    }

//    private boolean checkDiff() {
//        setInputToFiled();
//        if (!TextUtils.isEmpty(entry.name) && !entry.name.equals(name))
//            return true;
//        if (!TextUtils.isEmpty(entry.mobile) && !entry.mobile.equals(mobile))
//            return true;
//        if (!TextUtils.isEmpty(entry.zname) && znameDiff)
//            return true;
//        return !TextUtils.isEmpty(entry.bur) && !entry.bur.equals(detailAddr);
//    }

    private void setInputToFiled() {
        name = name_edit.getText().toString().trim();
        mobile = mobile_phone_edit.getText().toString().trim();
        region = regionTv.getText().toString().trim();
        detailAddr = addressDetailEt.getText().toString().trim();
    }

    private boolean checkHasInput() {
        if (entry == null) {
            name = name_edit.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                return true;
            }
            mobile = mobile_phone_edit.getText().toString().trim();

            if (!TextUtils.isEmpty(mobile)) {
                return true;
            }

            region = regionTv.getText().toString().trim();
            if (!"点击选择".equals(region)) {
                return true;
            }

            detailAddr = addressDetailEt.getText().toString().trim();

            if (!TextUtils.isEmpty(detailAddr)) {
                return true;
            }
        }
        return false;
    }

    void updateUserInfo(AddressEntry entry) {
        FragmentManager manager = getBaseAct().getSupportFragmentManager();
        if (manager != null) {
            List<Fragment> aList = manager.getFragments();
            if (aList != null && aList.size() > 0) {
                int length = aList.size();
                for (int i = 1; i < length; i++) {
                    Fragment fragment = aList.get(i);
                    if (fragment != null && fragment instanceof PayFragment) {
                        ((PayFragment) fragment).setAddressWidgetContent(entry);
                        break;
                    }
                }
            }
        }
        IntentUtil.sendUpdateAddressRequest(getBaseAct());
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
        if (!TextUtils.isEmpty(entry.region))
            regionTv.setText(entry.region);
        if (!TextUtils.isEmpty(entry.address))
            addressDetailEt.setText(entry.address);
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.youcai.refresh.myaddress");
            getBaseAct().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getBaseAct().unregisterReceiver(mUpdateReceiver);
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
                if (!TextUtils.isEmpty(details_address)) {
                    if (entry == null)
                        entry = new AddressEntry();
                    entry.address = details_address;
                    entry.zid = 0;
                    entry.city = null;
                    entry.region = null;
//                    choose_zone_tv.setText(details_address);
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
//        if (!TextUtils.isEmpty(entry.name) && !entry.name.equals(zEntry.name)) znameDiff = true;
//        entry.zname = zEntry.name;
        entry.zid = zEntry.id;
//        choose_zone_tv.setText(zEntry.name);
    }

    private void alertQuitEdit() {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText("确定退出修改？");
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

    private void requestDeladdr(String id) {
        RequestParams params = new RequestParams();
        params.add("id", id);
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        HttpRequestUtil.getHttpClient(getActivity()).post(LocalParams.getBaseUrl() + "user/deladdr", params, new AsyncHttpResponseHandlerIntercept() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
                String result = new String(responseBody);
                BaseEntry entry = JsonUtilsParser.fromJson(result, BaseEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        IntentUtil.sendUpdateAddressRequest(getActivity());

                        popBackStack();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody, error);
            }
        });
    }


    private AddressEntry entry = null;
    /**
     * 门牌号
     */
//    EditText house_number_edit;
    /**
     * 手机号
     */
    @Bind(R.id.mobile_phone_edit)
    EditText mobile_phone_edit;
    //    @Bind(R.id.choose_zone_tv)
//    TextView choose_zone_tv;
    @Bind(R.id.regionTv)
    TextView regionTv;
    /**
     * 姓名
     */
    @Bind(R.id.name_edit)
    EditText name_edit;
    @Bind(R.id.choose_zone_ll)
    View choose_zone_ll;
    /**
     * 楼号
     */
    @Bind(R.id.building_number_edit)
    EditText addressDetailEt;
    /**
     * 单元号
     */
    @Bind(R.id.back)
    View back;
    @Bind(R.id.title_right_text)
    TextView deleteTv;
    @Bind(R.id.title_center_text)
    TextView title_center_text;
    private boolean znameDiff;
    private String name, mobile, region, detailAddr;
}
