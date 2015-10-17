package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.data.MyComboOrder;
import com.shequcun.farm.data.MyComboOrderListEntry;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.MyComboOrderAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;


import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * 我的订单——菜品订单
 * Created by mac on 15/10/8.
 */
public class MyComboOrderFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_order_listview_ly, container, false);
    }

    @Override
    protected void setWidgetLsn() {
        scroll_view.setMode(PullToRefreshBase.Mode.DISABLED);
        requestOrderNo();
    }

    @Override
    protected void initWidget(View v) {
        buildAdapter();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    void requestOrderNo() {
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/mycombo", new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                if (pBar != null) {
                    pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    MyComboOrderListEntry entry = JsonUtilsParser.fromJson(new String(data), MyComboOrderListEntry.class);
                    if (entry != null && entry.aList != null && entry.aList.size() > 0) {
                        doAddDataToAdapter(entry.aList);
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

    void buildAdapter() {
        if (adapter == null)
            adapter = new MyComboOrderAdapter(getActivity());
        mLv.setAdapter(adapter);
    }


    void doAddDataToAdapter(List<MyComboOrder> aList) {
        if (adapter != null) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
        Utils.setListViewHeightBasedOnChildren(mLv);
    }

    @OnItemClick(R.id.mLv)
    void onItemClick(int position) {
        if (adapter == null)
            return;
        MyComboOrder entry = adapter.getItem(position);
//        HistoryOrderEntry entry = adapter.getItem(position);
//        if (entry != null) {
//            if (entry.status == 1 || entry.status == 3 || entry.status == 0 || entry.status == 2) {
//                gotoFragmentByAdd(buildBundle(buildOrderParams(entry)), R.id.mainpage_ly, new ModifyOrderFragment(), ModifyOrderFragment.class.getName());
//            } else if (entry.status == 4) {
//                ToastHelper.showShort(getActivity(), "您的订单已取消!");
//            }
//        }
        if (entry != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("MyComboOrderEntry", entry);
            gotoFragmentByAdd(bundle, R.id.mainpage_ly, new DishesFragment(), DishesFragment.class.getName());
        }
    }

    @Bind(R.id.progress_bar)
    ProgressBar pBar;
    @Bind(R.id.mLv)
    ListView mLv;
    @Bind(R.id.pView)
    PullToRefreshScrollView scroll_view;

    MyComboOrderAdapter adapter;
}
