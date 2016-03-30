package com.lynp.ui.fragment;

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
//import com.shequcun.farm.R;
import com.lynp.R;
import com.lynp.ui.data.UserLoginEntry;
import com.lynp.ui.util.CacheManager;
import com.lynp.ui.util.ConsultationDlg;
import com.lynp.ui.adapter.MineAdapter;
import com.lynp.ui.fragment.FragmentUtils;
//import com.shequcun.farm.ui.fragment.MyOrderViewPagerFragment;
//import com.shequcun.farm.ui.fragment.SetFragment;
import com.lynp.ui.util.AvoidDoubleClickListener;
import com.lynp.ui.util.IntentUtil;
import com.lynp.ui.util.N7Utils;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * Created by apple on 15/8/3.
 */
public class MineFragment extends BaseFragment {

    boolean mIsBind = false;
    @Bind(R.id.mine_lv)
    ListView mine_lv;

    MineAdapter adapter;
    UserLoginEntry UserEntry;
    View HeadView;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_ui, container, false);
        return view;
    }

    @Override
    protected void initWidget(View v) {

    }

    private UserLoginEntry getUserLoginEntry() {
        return new CacheManager(getActivity()).getUserLoginEntry();
    }

    @Override
    protected void setWidgetLsn() {

        buildAdapter();
    }


    @OnItemClick(R.id.mine_lv)
    void onItemClick(int position) {
        if (adapter == null || mine_lv == null)
            return;
//        if (UserEntry == null) {
////            FragmentUtils.login(this);
//            return;
//        }
        switch (position - mine_lv.getHeaderViewsCount()) {
            case 0://我的订单
//                gotoFragmentByAdd(R.id.mainpage_ly, new MyOrderViewPagerFragment(), MyOrderViewPagerFragment.class.getName());
                break;

            case 1://地址管理
                Bundle bundle = new Bundle();
//                bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SETTING);
//                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
                break;

            case 2://拨打客服电话
                ConsultationDlg.showCallTelDlg(getBaseAct());
                break;

            case 3://设置
//                gotoFragmentByAdd(R.id.mainpage_ly, new SetFragment(), SetFragment.class.getName());
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
        if (mine_lv == null)
            return;
        if (HeadView != null)
            mine_lv.removeHeaderView(HeadView);
        UserEntry = new CacheManager(getBaseAct()).getUserLoginEntry();
        HeadView = LayoutInflater.from(getBaseAct()).inflate(R.layout.mine_head_ui, null);
        ((TextView) HeadView.findViewById(R.id.mobile_phone)).setText(UserEntry != null ? UserEntry.mobile : "");
        CircleImageView circleImageView = ((CircleImageView) HeadView.findViewById(R.id.my_head));
        if (UserEntry != null && !TextUtils.isEmpty(UserEntry.headimg)) {
            ImageLoader.getInstance().displayImage(N7Utils.filter22UrlParams(UserEntry.headimg, 200), circleImageView);
            HeadView.findViewById(R.id.click_login_tv).setVisibility(View.GONE);
        } else {
            circleImageView.setImageResource(R.color.white_f4f4f4);
        }
        HeadView.findViewById(R.id.red_dot_view).setVisibility(UserEntry != null ? View.VISIBLE : View.GONE);
        HeadView.findViewById(R.id.my_head).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                if (UserEntry == null)
                    FragmentUtils.login(MineFragment.this);
                else
                    ;
//                    gotoChangePwd1();
            }
        });
        mine_lv.addHeaderView(HeadView, null, false);
    }


    void buildAdapter() {
        addHeader();
        if (adapter == null)
            adapter = new MineAdapter(getActivity());
        mine_lv.setAdapter(adapter);
    }


    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_MINE_PAGE);
            getBaseAct().registerReceiver(mUpdateReceiver, intentFilter);
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
//                changePwdTip();
            }
        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getBaseAct().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }


    void showLoginDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseAct());
        builder.setTitle("提示");
        builder.setMessage("亲,您还未登录哦!立刻登录?");
        builder.setNegativeButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentUtils.login(MineFragment.this);
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }
}
