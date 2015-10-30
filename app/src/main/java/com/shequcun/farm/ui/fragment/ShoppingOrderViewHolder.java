package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.OrderListEntry;
import com.shequcun.farm.ui.adapter.ShoppingOrderAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by mac on 15/10/30.
 */
public class ShoppingOrderViewHolder {

    BaseFragment fragment;

    public ShoppingOrderViewHolder(BaseFragment fragment, View view) {
        this.fragment = fragment;
        ButterKnife.bind(this, view);
        setWidgetLsn();
    }

    protected void setWidgetLsn() {
        buildAdapter();
        scroll_view.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        scroll_view.setOnRefreshListener(onRefrshLsn);
        requestOrderEntry();
    }


    void buildAdapter() {
        if (adapter == null) {
            adapter = new ShoppingOrderAdapter(fragment.getBaseAct());
        }
        adapter.clear();
        mLv.setAdapter(adapter);
    }

    public void addDataToAdapter(List<HistoryOrderEntry> aList) {
        if (aList != null && aList.size() > 0 && mLv != null) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            Utils.setListViewHeightBasedOnChildren(mLv);
        }

        if (pBar != null) {
            pBar.setVisibility(View.GONE);
        }
    }

    PullToRefreshScrollView.OnRefreshListener2 onRefrshLsn = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            requestOrderEntry();
        }
    };

    public void requestOrderEntry() {
        if(fragment==null)
            return;
        RequestParams params = new RequestParams();
        if (adapter != null && adapter.getCount() >= 1) {
            params.add("lastid", adapter.getItem(adapter.getCount() - 1).id + "");
        }
        params.add("length", "20");
        params.add("type", "2");
        HttpRequestUtil.getHttpClient(fragment.getBaseAct()).get(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                if (scroll_view != null)
                    scroll_view.onRefreshComplete();
                if (pBar != null) {
                    pBar.setVisibility(View.GONE);
                }
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
                        ToastHelper.showShort(fragment.getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(fragment.getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(fragment.getBaseAct(), "错误码" + sCode);
            }
        });
    }

    @OnItemClick(R.id.mLv)
    void OnItemClick(int position) {
        if (fragment == null)
            return;
        if (adapter == null)
            return;
        HistoryOrderEntry entry = adapter.getItem(position);
        if (entry != null) {
            if (entry.status == 1 || entry.status == 3 || entry.status == 0 || entry.status == 2) {
                fragment.gotoFragmentByAdd(buildBundle(buildOrderParams(entry)), R.id.mainpage_ly, new ModifyOrderFragment(), ModifyOrderFragment.class.getName());
            } else if (entry.status == 4) {
                ToastHelper.showShort(fragment.getBaseAct(), "您的订单已取消!");
            }
        }
    }


    ModifyOrderParams buildOrderParams(HistoryOrderEntry entry) {
        ModifyOrderParams params = new ModifyOrderParams();
        params.setParams(entry.id, entry.orderno, entry.item_type, entry.combo_id, entry.price, entry.combo_idx, entry.status, entry.date, entry.name, entry.mobile, entry.address, entry.type, entry.placeAnOrderDate, entry.fList);
        return params;
    }

    Bundle buildBundle(ModifyOrderParams entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("HistoryOrderEntry", entry);
        return bundle;
    }

    @Bind(R.id.progress_bar)
    ProgressBar pBar;
    ShoppingOrderAdapter adapter;
    @Bind(R.id.mLv)
    ListView mLv;
    @Bind(R.id.pView)
    PullToRefreshScrollView scroll_view;
}
