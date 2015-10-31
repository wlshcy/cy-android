package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.MyComboOrder;
import com.shequcun.farm.data.OrderListEntry;
import com.shequcun.farm.ui.adapter.MyOrderAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * 菜品订单
 * Created by apple on 15/8/8.
 */
public class DishesFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_combo_order_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.my_combo_order);
        buidlAdapter();
    }

    @OnItemClick(R.id.mLv)
    void onItemClick(int position) {
        if (adapter == null)
            return;
        HistoryOrderEntry entry = adapter.getItem(position);
        if (entry != null) {
            if (entry.status == 1 || entry.status == 3 || entry.status == 0 || entry.status == 2) {
                gotoFragmentByAdd(buildBundle(buildOrderParams(entry)), R.id.mainpage_ly, new ModifyOrderFragment(), ModifyOrderFragment.class.getName());
            } else if (entry.status == 4) {
                ToastHelper.showShort(getBaseAct(), "您的订单已取消!");
            }
        }
    }

    @Override
    protected void setWidgetLsn() {
        scroll_view.setMode(PullToRefreshBase.Mode.DISABLED);
        requestOrderEntry(buildOrderNo());
    }


    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    String buildOrderNo() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            MyComboOrder entry = (MyComboOrder) bundle.getSerializable("MyComboOrderEntry");
            if (entry != null) {
                return entry.con;
            }
        }
        return null;
    }


    MyComboOrder buildMyComboOrder() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            MyComboOrder entry = (MyComboOrder) bundle.getSerializable("MyComboOrderEntry");
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }

    void buidlAdapter() {
        if (adapter == null) {
            adapter = new MyOrderAdapter(getBaseAct());
        }
        adapter.clear();
//        adapter.buildPayOnClickLsn(lsn);
        mLv.setAdapter(adapter);
    }

    public void addDataToAdapter(List<HistoryOrderEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            Utils.setListViewHeightBasedOnChildren(mLv);
        }
    }


    ModifyOrderParams buildOrderParams(HistoryOrderEntry entry) {
        ModifyOrderParams params = new ModifyOrderParams();
        MyComboOrder mEntry = buildMyComboOrder();
        params.setParams(entry.id, entry.orderno, 1, entry.combo_id, entry.price, entry.combo_idx, entry.status, entry.date, entry.name, entry.mobile
                , entry.address, entry.type, entry.placeAnOrderDate, entry.fList, mEntry != null ? mEntry.shipday : null, entry.times,
                buildOrderNo(), mEntry != null ? mEntry.duration : 0
        );
        return params;
    }

    Bundle buildBundle(ModifyOrderParams entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("HistoryOrderEntry", entry);
        return bundle;
    }


    public void requestOrderEntry(String orderno) {
        if (TextUtils.isEmpty(orderno)) {
            if (pBar != null) {
                pBar.setVisibility(View.GONE);
            }
            ToastHelper.showShort(getBaseAct(), "请求数据失败.请稍后再试...");
            return;
        }
        RequestParams params = new RequestParams();
        params.add("orderno", orderno);
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/choose", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                if (pBar != null) {
                    pBar.setVisibility(View.GONE);
                }
                if (scroll_view != null)
                    scroll_view.onRefreshComplete();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    OrderListEntry entry = JsonUtilsParser.fromJson(new String(data), OrderListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (entry.aList != null) {
                                addDataToAdapter(entry.aList);
                            }
                            return;
                        }

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
                ToastHelper.showShort(getBaseAct(), "错误码" + sCode);
            }
        });
    }

    @Bind(R.id.progress_bar)
    ProgressBar pBar;
    @Bind(R.id.mLv)
    ListView mLv;
    @Bind(R.id.pView)
    PullToRefreshScrollView scroll_view;
    MyOrderAdapter adapter;
}
