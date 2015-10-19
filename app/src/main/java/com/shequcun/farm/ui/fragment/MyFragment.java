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
import android.widget.ListView;
import android.widget.TextView;

import com.common.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.MyAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.N7Utils;

import butterknife.Bind;
import butterknife.OnItemClick;

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
    }

    @Override
    protected void setWidgetLsn() {
        buildAdapter();
    }


    @OnItemClick(R.id.mLv)
    void onItemClick(int position) {
        if (adapter == null || mLv == null)
            return;
        if (uEntry == null) {
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
                Bundle bundle1 = new Bundle();
                bundle1.putInt(RedPacketsListFragment.KEY_ACTION, RedPacketsListFragment.ACTION_LOOK);
                gotoFragment(bundle1, R.id.mainpage_ly, new RedPacketsListFragment(), RedPacketsListFragment.class.getName());
                break;
            case 3://拨打客服电话
                ConsultationDlg.showCallTelDlg(getActivity());
                break;
            case 4://地址管理
                Bundle bundle = new Bundle();
                bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SETTING);
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
                break;
            case 5://设置
                gotoFragmentByAdd(R.id.mainpage_ly, new SetFragment(), SetFragment.class.getName());
                break;
        }
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

    void addHeader() {
        if (mLv == null)
            return;
        if (hView_1 != null)
            mLv.removeHeaderView(hView_1);
        if (hView_2 != null)
            mLv.removeHeaderView(hView_2);
        uEntry = new CacheManager(getActivity()).getUserLoginEntry();
        hView_1 = LayoutInflater.from(getActivity()).inflate(R.layout.my_item_head_ly, null);
        ((TextView) hView_1.findViewById(R.id.mobile_phone)).setText(uEntry != null ? uEntry.mobile : "");
        CircleImageView circleImageView = ((CircleImageView) hView_1.findViewById(R.id.my_head));
        if (uEntry != null && !TextUtils.isEmpty(uEntry.headimg)) {
            ImageLoader.getInstance().displayImage(N7Utils.filter22UrlParams(uEntry.headimg,200), circleImageView);
            hView_1.findViewById(R.id.click_login_tv).setVisibility(View.GONE);
        }
        hView_1.findViewById(R.id.my_head).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                if (uEntry == null)
                    gotoFragment(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
            }
        });
        mLv.addHeaderView(hView_1, null, false);
    }

    void buildAdapter() {
        addHeader();
        if (adapter == null)
            adapter = new MyAdapter(getActivity(), getResources().getStringArray(R.array.my_array));
        mLv.setAdapter(adapter);
    }


    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_MINE_PAGE);
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
    @Bind(R.id.mLv)
    ListView mLv;
    MyAdapter adapter;
    UserLoginEntry uEntry;
    View hView_1;
    View hView_2;
}
