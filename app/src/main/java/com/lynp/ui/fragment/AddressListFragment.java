package com.lynp.ui.fragment;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.shequcun.farm.R;
import com.lynp.R;

import com.shequcun.farm.compare.AddressComparator;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.BaseEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.lynp.ui.adapter.AddressListAdapter;
import com.shequcun.farm.ui.fragment.AddressFragment;
import com.shequcun.farm.ui.fragment.BaseFragment;
import com.shequcun.farm.util.HttpRequestUtil;
import com.lynp.ui.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/9/7.
 */
public class AddressListFragment extends BaseFragment {
    @Bind(R.id.address_lv)
    ListView addressLv;
    @Bind(R.id.title_center_text)
    TextView titleTv;
    private AddressListAdapter adapter;
    private View addAddress;
    private int maxLen = 5;
    private int action = Action.SELECT;

    interface Action {
        String KEY = "Action";
        int SELECT = 0;
        int SETTING = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_list, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestAddress();
        addBroadcast();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeBroadcast();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void initParams() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        action = bundle.getInt(Action.KEY);
    }

    @Override
    protected void initWidget(View v) {
        titleTv.setText("地址管理");
    }

    private AddressListAdapter.OnUpdateAddressListener onUpdateAddressListener = new AddressListAdapter.OnUpdateAddressListener() {
        @Override
        public void onUpdate(AddressEntry entry) {
            if (entry == null) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable("AddressEntry", entry);
            gotoAddressFragment(bundle);
        }
    };

    private AddressListAdapter.OnChooseAddressListener onChooseAddressListener = new AddressListAdapter.OnChooseAddressListener() {
        @Override
        public void onChoose(AddressEntry entry) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("AddressEntry", entry);
            requestSetDefaultAddr(entry.id, entry);
        }
    };

    @Override
    protected void setWidgetLsn() {
        addAddress = LayoutInflater.from(getBaseAct()).inflate(R.layout.item_add_address, null);
        addAddress.setVisibility(View.GONE);
        addressLv.addHeaderView(addAddress);
        adapter = new AddressListAdapter(getBaseAct());
        if (action == Action.SELECT) {
            adapter.setShowDefaultIcon(true);
        }
        adapter.setOnUpdateAddressListener(onUpdateAddressListener);
        addressLv.setAdapter(adapter);
        if (action == Action.SELECT) {
            adapter.setOnChooseAddressListener(onChooseAddressListener);
        }
        addAddress.setOnClickListener(onClickListener);
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }


    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            alertDelay(position);
            return false;
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == addAddress) {
                AddressFragment fragment = new AddressFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("key", AddressFragment.KEY_ONLY_SAVE);
                gotoFragmentByAdd(bundle, fragment, fragment.getClass());
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AddressEntry entry = (AddressEntry) adapter.getItem(position - addressLv.getHeaderViewsCount());
            Bundle bundle = new Bundle();
            bundle.putSerializable("AddressEntry", entry);
            if (action == Action.SETTING)
                gotoAddressFragment(bundle);
            else if (action == Action.SELECT)
                requestSetDefaultAddr(entry.id, entry);
        }
    };

    private void gotoAddressFragment(Bundle bundle) {
        AddressFragment fragment = new AddressFragment();
        gotoFragmentByAdd(bundle, fragment, fragment.getClass());
    }

    private void requestAddress() {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "/v1/addresses", new AsyncHttpResponseHandler() {
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
                String result = new String(data);
                AddressListEntry entry = JsonUtilsParser.fromJson(result, AddressListEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errmsg)) {
                        successAddress(entry.aList);
                        return;
                    } else {
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "请求失败,错误码" + sCode);
            }
        });
    }

    private void successAddress(ArrayList<AddressEntry> list) {
        adapter.clear();
        if (list == null || list.size() <= 0) {
            showAddAddressView();
        } else if (list.size() >= maxLen) {
            goneAddAddressView();
        } else {
            showAddAddressView();
        }
        Collections.sort(list, new AddressComparator());
        adapter.addAll(list);
    }

    private void requestSetDefaultAddr(int id, final AddressEntry addressEntry) {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("id", id + "");
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "user/v3/address", params,
                new AsyncHttpResponseHandler() {
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
                            BaseEntry entry = JsonUtilsParser.fromJson(new String(data), BaseEntry.class);
                            if (entry != null) {
                                if (TextUtils.isEmpty(entry.errmsg)) {
                                    new CacheManager(getBaseAct()).saveUserReceivingAddress(JsonUtilsParser.toJson(addressEntry).getBytes());
                                    goback(addressEntry);
                                    IntentUtil.sendUpdateAddressRequest(getActivity());
                                    return;
                                } else {
                                    ToastHelper.showShort(getBaseAct(), entry.errmsg);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                        if (sCode == 0) {
                            ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), "请求失败,错误码" + sCode);
                    }
                });
    }

    private void goback(AddressEntry entry) {
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
        popBackStack();
    }

    private void goneAddAddressView() {
        if (addressLv.getHeaderViewsCount() > 0)
            addressLv.removeHeaderView(addAddress);
    }

    private void showAddAddressView() {
        if (addAddress.getVisibility() == View.GONE)
            addAddress.setVisibility(View.VISIBLE);
    }

    private void addAddAddressView() {
        if (addressLv.getHeaderViewsCount() <= 0)
            addressLv.addHeaderView(addAddress);
    }

    private void addBroadcast() {
        IntentFilter intentFilter = new IntentFilter(IntentUtil.UPDATE_ADDRESS_REQUEST);
        intentFilter.addAction(IntentUtil.UPDATE_ADDRESS_REQUEST);
        getBaseAct().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void removeBroadcast() {
        getBaseAct().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestAddress();
        }
    };

    private void alertDelay(final int pos) {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText(R.string.prompt_delete_address);
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
                        removeAddressItem(pos);
                    }
                });
    }

    private void removeAddressItem(int pos) {
        adapter.remove(pos);
        checkForShowingAddAddressView();
    }

    private void checkForShowingAddAddressView() {
        if (adapter.getCount() < maxLen) {
            addAddAddressView();
            showAddAddressView();
        }
    }
}