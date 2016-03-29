package com.lynp.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lynp.ui.adapter.OrderEnsureAdapter;
import com.lynp.ui.data.OrderEnsureEntry;
import com.lynp.ui.util.IntentUtil;
//import com.shequcun.farm.R;
import com.lynp.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.PayEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.data.WxPayResEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.fragment.AddressFragment;
import com.shequcun.farm.ui.fragment.BaseFragment;
import com.shequcun.farm.ui.fragment.PayResultFragment;
import com.shequcun.farm.ui.fragment.RedPacketsListFragment;
import com.shequcun.farm.util.AlipayUtils;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;
import com.shequcun.farm.util.WxPayUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 订单确认页面
 * Created by nmg on 16/2/1.
 */
public class OrderEnsureFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_ensure, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        entry = (OrderEnsureEntry) bundle.getSerializable("entry");
//        Log.i("****", entry.items.toString());
//        adapter.addAll(entry.items);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {

        Bundle bundle = getArguments();
        entry = (OrderEnsureEntry) bundle.getSerializable("entry");

        buildAdapter();

        adapter.addAll(entry.items);
        adapter.notifyDataSetChanged();
        pScrollView.fullScroll(View.FOCUS_UP);

        if (entry.freight == 0){
            freight.setText("免运费");
        }else {
            freight.setText(Utils.unitPeneyToYuan(entry.freight));
        }
        Spannable spannable = Utils.getSpanableSpan("共计:",
                Utils.unitPeneyToYuan(entry.price),
                ResUtil.dip2px(getBaseAct(), 14),
                ResUtil.dip2px(getBaseAct(), 14),
                getResources().getColor(R.color.gray_a9a9a9),
                getResources().getColor(R.color.red_f36043));
        total_price.setText(spannable);

        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.ensure);
    }

    @Override
    protected void setWidgetLsn() {

    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new OrderEnsureAdapter(getBaseAct());
        adapter.clear();
        eLv.setAdapter(adapter);
        eLv.setExpanded(true);

    }

    @OnClick(R.id.choose_address)
    void doModifyAddress() {
        Bundle bundle = new Bundle();
        bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SELECT);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
    }
    OrderEnsureAdapter adapter;

    OrderEnsureEntry entry;

    @Bind(R.id.eLv)
    ExpandableHeightListView eLv;

    @Bind(R.id.pScrollView)
    ScrollView pScrollView;

    @Bind(R.id.freight)
    TextView freight;

    @Bind(R.id.total_price)
    TextView total_price;

    @Bind(R.id.choose_address)
    View choose_address;

}
