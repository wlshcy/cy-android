package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.MyAddressAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cong on 15/9/7.
 */
public class AddressListFragment extends BaseFragment {
    private ListView addressLv;
    private TextView titleTv;
    private MyAddressAdapter adapter;
    private View back;
    private View addAddress;
    private int maxLen = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_list, null);
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

    @Override
    protected void initWidget(View v) {
        addressLv = (ListView) v.findViewById(R.id.address_lv);
        titleTv = (TextView) v.findViewById(R.id.title_center_text);
        back = v.findViewById(R.id.back);
        titleTv.setText(R.string.my_address);
        addAddress = LayoutInflater.from(getActivity()).inflate(R.layout.item_add_address, null);
        addAddress.setVisibility(View.GONE);
        addressLv.addHeaderView(addAddress);
        adapter = new MyAddressAdapter(getActivity());
        addressLv.setAdapter(adapter);
    }

    @Override
    protected void setWidgetLsn() {
        addressLv.setOnItemClickListener(onItemClickListener);
        back.setOnClickListener(onClickListener);
        addAddress.setOnClickListener(onClickListener);
        addressLv.setOnItemLongClickListener(onItemLongClickListener);
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
            if (v == back) {
                popBackStack();
            } else if (v == addAddress) {
                AddressFragment fragment = new AddressFragment();
                gotoFragmentByAdd(fragment, fragment.getClass());
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AddressEntry entry = (AddressEntry) adapter.getItem(position - addressLv.getHeaderViewsCount());
            Bundle bundle = new Bundle();
            bundle.putSerializable("AddressEntry", entry);
            AddressFragment fragment = new AddressFragment();
            gotoFragmentByAdd(bundle, fragment, fragment.getClass());
        }
    };

    private void requestAddress() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/v2/address", new AsyncHttpResponseHandler() {
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

    private void successAddress(ArrayList<AddressEntry> list) {
        if (list == null || list.isEmpty())
            return;
        else if (list.size() >= maxLen)
            goneAddAddressView();
        else
            showAddAddressView();
        adapter.clear();
        adapter.addAll(list);
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
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void removeBroadcast() {
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestAddress();
        }
    };

    private void alertDelay(final int pos) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
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

    private void checkForShowingAddAddressView(){
        if (adapter.getCount()<maxLen){
            addAddAddressView();
            showAddAddressView();
        }
    }
}
