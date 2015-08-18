package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.data.OrderListEntry;
import com.shequcun.farm.ui.adapter.MyOrderAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.List;

/**
 * 菜品订单
 * Created by apple on 15/8/8.
 */
public class DishesFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_order_listview_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        pBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        scroll_view = (PullToRefreshScrollView) v.findViewById(R.id.scroll_view);
        buidlAdapter();
    }

    @Override
    protected void setWidgetLsn() {
        scroll_view.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        scroll_view.setOnRefreshListener(onRefrshLsn);
        requestOrderEntry();
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


    void buidlAdapter() {
        if (adapter == null) {
            adapter = new MyOrderAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
    }

    public void addDataToAdapter(List<HistoryOrderEntry> aList) {
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(mLv);
        if (pBar != null) {
            pBar.setVisibility(View.GONE);
        }
    }

    public void requestOrderEntry() {
        RequestParams params = new RequestParams();
        if (adapter != null && adapter.getCount() >= 1) {
            params.add("lastid", adapter.getItem(adapter.getCount() - 1).id + "");
        }
        params.add("length", "20");
        params.add("type", "1");
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
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

                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码" + sCode);
            }
        });
    }

    ProgressBar pBar;
    MyOrderAdapter adapter;
    ListView mLv;
    PullToRefreshScrollView scroll_view;
}
