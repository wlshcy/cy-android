package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.bitmap.cache.ImageCacheManager;
import com.common.widget.CircleImageView;
import com.shequcun.farm.R;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.MyAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.JsonUtilsParser;

/**
 * Created by apple on 15/8/3.
 */
public class MyFragment extends BaseFragment {

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_ly, container, false);
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
    }

    @Override
    protected void setWidgetLsn() {
        mLv.setOnItemClickListener(onItemClick);
        buildAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        doRegisterRefreshBrodcast();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }

    View.OnClickListener onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.set://设置
//                    break;
                case R.id.tv_status://登录或选择菜品
                    gotoFragment(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
                    break;
                default:
                    break;
            }
        }
    };

    void addHeader() {
        if (mLv == null)
            return;
        if (hView_1 != null)
            mLv.removeHeaderView(hView_1);
        if (hView_2 != null)
            mLv.removeHeaderView(hView_2);
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        } else {
            uEntry = null;
        }
        hView_1 = LayoutInflater.from(getActivity()).inflate(R.layout.my_item_head_ly, null);
//        hView_1.findViewById(R.id.set).setOnClickListener(onClick);
        ((TextView) hView_1.findViewById(R.id.mobile_phone)).setText(uEntry != null ? uEntry.mobile : "");
        ((CircleImageView) hView_1.findViewById(R.id.my_head)).setImageUrl(uEntry != null ? uEntry.headimg : null, ImageCacheManager.getInstance().getImageLoader());
        ((TextView) hView_1.findViewById(R.id.address_tv)).setText(uEntry != null ? uEntry.address : "");
        hView_1.findViewById(R.id.my_head).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                if (uEntry == null)
                    gotoFragment(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
            }
        });
        hView_1.findViewById(R.id.address_tv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                gotoFragment(R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
            }
        });

        mLv.addHeaderView(hView_1, null, false);
//        mLv.addHeaderView(hView_2 = buildHeadView(uEntry), null, false);
    }

//    void addHeader(ComboEntry entry) {
//        if (mLv == null)
//            return;
//        if (hView_1 != null)
//            mLv.removeHeaderView(hView_1);
//        if (hView_2 != null)
//            mLv.removeHeaderView(hView_2);
//        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
//        if (data != null && data.length > 0) {
//            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
//        }
//        hView_1 = LayoutInflater.from(getActivity()).inflate(R.layout.my_item_head_ly, null);
//        hView_1.findViewById(R.id.set).setOnClickListener(onClick);
//        ((TextView) hView_1.findViewById(R.id.mobile_phone)).setText(uEntry != null ? uEntry.mobile : "");
//        ((CircleImageView) hView_1.findViewById(R.id.my_head)).setImageUrl(uEntry != null ? uEntry.headimg : null, ImageCacheManager.getInstance().getImageLoader());
//        mLv.addHeaderView(hView_1, null, false);
//        mLv.addHeaderView(hView_2 = buildHeaderView(uEntry, entry), null, false);
//    }

    void buildAdapter() {
        addHeader();
        if (adapter == null)
            adapter = new MyAdapter(getActivity(), getResources().getStringArray(R.array.my_array));
        mLv.setAdapter(adapter);
    }

//    View buildHeadView(UserLoginEntry uEntry) {
//        View hView = null;
//        if (uEntry == null) {
//            hView = LayoutInflater.from(getActivity()).inflate(R.layout.no_login_ly, null);
//            hView.findViewById(R.id.tv_status).setOnClickListener(onClick);
//        }
//        else {
//            if (hasChosenCombo) {
//                hView = LayoutInflater.from(getActivity()).inflate(R.layout.have_chosen_ly, null);
//            } else {
//                hView = LayoutInflater.from(getActivity()).inflate(R.layout.no_login_ly, null);
//                ((TextView) hView.findViewById(R.id.tv_tip)).setText(R.string.no_combo_tip);
//                ((TextView) hView.findViewById(R.id.tv_status)).setText(R.string.choose_combo);
//                hView.findViewById(R.id.tv_status).setOnClickListener(onClick);
//            }
//        }
//        return hView;
//    }

//    View buildHeaderView(UserLoginEntry uEntry, ComboEntry entry) {
//        View hView = null;
//        if (uEntry == null) {
//            hView = LayoutInflater.from(getActivity()).inflate(R.layout.no_login_ly, null);
//            hView.findViewById(R.id.tv_status).setOnClickListener(onClick);
//        } else {
//            if (entry != null) {
//                hView = LayoutInflater.from(getActivity()).inflate(R.layout.have_chosen_ly, null);
//                ((TextView) hView.findViewById(R.id.combo_name)).setText(entry.title);
//                ((TextView) hView.findViewById(R.id.dis_cycle)).setText("每周配送" + entry.shipday.length + "次");
//                ((TextView) hView.findViewById(R.id.per_weight)).setText("每次配送" + Utils.unitConversion(entry.weights[entry.index]));
//                ((TextView) hView.findViewById(R.id.times)).setText("配送" + entry.duration + "周");
//                ((TextView) hView.findViewById(R.id.all_weight)).setText("共" + Utils.unitConversion(entry.duration * entry.weights[entry.index] * entry.shipday.length));
//            }
//        }
//        return hView;
//    }

    /**
     * 是否选择菜品
     */
//    boolean hasChosenCombo = false;

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null || mLv == null)
                return;
            if (uEntry == null) {
//                ToastHelper.showShort(getActivity(), R.string.login_msg_tip);
                showLoginDlg();
                return;
            }
            switch (position - mLv.getHeaderViewsCount()) {
                case 0://我的订单
                    gotoFragment(R.id.mainpage_ly, new MyOrderViewPagerFragment(), MyOrderViewPagerFragment.class.getName());
                    break;
                case 1://订单延期配送
                    gotoFragment(R.id.mainpage_ly, new OrderDelayFragment(), OrderDelayFragment.class.getName());
                    break;
                case 2://我的优惠红包
                    gotoFragment(R.id.mainpage_ly, new RedPacketsListFragment(), RedPacketsListFragment.class.getName());
                    break;
                case 3://拨打客服电话
                    ConsultationDlg.showCallTelDlg(getActivity());
                    break;
                case 4://设置
                    gotoFragmentByAdd(R.id.mainpage_ly, new SetFragment(), SetFragment.class.getName());
                    break;
            }

        }
    };

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.youcai.refresh");
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
//            ComboEntry entry = (ComboEntry) intent.getSerializableExtra("ComboEntry");
//            if (entry != null) {
//                addHeader(entry);
//                return;
//            }

            if (action.equals("com.youcai.refresh")) {
                addHeader();
            }
        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }


    void showLoginDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("亲,您还未登录哦!立刻登录?");
        builder.setNegativeButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoFragment(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }


    boolean mIsBind = false;
    ListView mLv;
    MyAdapter adapter;
    UserLoginEntry uEntry;

    View hView_1;
    View hView_2;
}
