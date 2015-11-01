package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.MyComboOrder;
import com.shequcun.farm.data.MyComboOrderListEntry;
import com.shequcun.farm.ui.adapter.MyComboOrderAdapter;
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
public class MyComboOrderViewHolder {
    BaseFragment fgt;

    MyComboOrderViewHolder(BaseFragment fgt, View view) {
        this.fgt = fgt;
        ButterKnife.bind(this, view);
        buildAdapter();
        scroll_view.setMode(PullToRefreshBase.Mode.DISABLED);
        requestOrderNo();
    }


    void buildAdapter() {
        if (adapter == null)
            adapter = new MyComboOrderAdapter(fgt.getBaseAct());
        mLv.setAdapter(adapter);
    }

    @OnItemClick(R.id.mLv)
    void onItemClick(int position) {
        if (adapter == null || fgt == null)
            return;
        MyComboOrder entry = adapter.getItem(position);
        if (entry != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("MyComboOrderEntry", entry);
            fgt.gotoFragmentByAdd(bundle, R.id.mainpage_ly, new DishesFragment(), DishesFragment.class.getName());
        }
    }


    void requestOrderNo() {
        if (fgt == null)
            return;
        HttpRequestUtil.getHttpClient(fgt.getBaseAct()).get(LocalParams.getBaseUrl() + "cai/mycombo", new AsyncHttpResponseHandler() {

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
                    ToastHelper.showShort(fgt.getBaseAct(), R.string.network_error_tip);
                    return;
                }

                ToastHelper.showShort(fgt.getBaseAct(), "请求失败,错误码" + sCode);
            }
        });
    }

    void doAddDataToAdapter(List<MyComboOrder> aList) {
        if (adapter != null) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
        Utils.setListViewHeightBasedOnChildren(mLv);
    }

    @Bind(R.id.progress_bar)
    ProgressBar pBar;
    @Bind(R.id.mLv)
    ListView mLv;
    @Bind(R.id.pView)
    PullToRefreshScrollView scroll_view;

    MyComboOrderAdapter adapter;
}
