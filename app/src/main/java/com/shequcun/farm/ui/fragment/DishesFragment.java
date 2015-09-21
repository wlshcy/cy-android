package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.HistoryOrderEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.MyComboOrderListEntry;
import com.shequcun.farm.data.OrderListEntry;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
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
        scroll_view = (PullToRefreshScrollView) v.findViewById(R.id.pView);
        buidlAdapter();
    }

    @Override
    protected void setWidgetLsn() {
        scroll_view.setMode(PullToRefreshBase.Mode.DISABLED);
//        scroll_view.setOnRefreshListener(onRefrshLsn);
        mLv.setOnItemClickListener(onItemLsn);
//        requestOrderEntry();
        requestOrderNo();
    }


    void requestOrderNo() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");

        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/mycombo", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
                if (pBar != null) {
                    pBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
//                    RecommendEntry entry = JsonUtilsParser.fromJson(new String(data), RecommendEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
////                            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName());
//                        }
//                    }

                    MyComboOrderListEntry entry = JsonUtilsParser.fromJson(new String(data), MyComboOrderListEntry.class);

                    if (entry != null && entry.aList != null && entry.aList.size() > 0) {
                        requestOrderEntry(entry.aList.get(0).con);
                    }

                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {

            }
        });

    }


    AdapterView.OnItemClickListener onItemLsn = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (adapter == null)
                return;
            HistoryOrderEntry entry = adapter.getItem(position);

            if (entry != null) {

                if (entry.status == 1 || entry.status == 3 || entry.status == 0 || entry.status == 2) {
                    gotoFragmentByAdd(buildBundle(buildOrderParams(entry)), R.id.mainpage_ly, new ModifyOrderFragment(), ModifyOrderFragment.class.getName());
                }
//                else if (entry.status == 0) {
//                    requestAlipay(entry);
//                }
//                else if (entry.status == 2) {
//                    ToastHelper.showShort(getActivity(), "您的订单正在配送中,请耐心等待!");
//                }
                else if (entry.status == 4) {
                    ToastHelper.showShort(getActivity(), "您的订单已取消!");
                }
            }
        }
    };

//    PullToRefreshScrollView.OnRefreshListener2 onRefrshLsn = new PullToRefreshBase.OnRefreshListener2() {
//        @Override
//        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//        }
//
//        @Override
//        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//            requestOrderEntry();
//        }
//    };


    void buidlAdapter() {
        if (adapter == null) {
            adapter = new MyOrderAdapter(getActivity());
        }
        adapter.clear();
//        adapter.buildPayOnClickLsn(lsn);
        mLv.setAdapter(adapter);
    }

    public void addDataToAdapter(List<HistoryOrderEntry> aList) {
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
        Utils.setListViewHeightBasedOnChildren(mLv);
    }


    ModifyOrderParams buildOrderParams(HistoryOrderEntry entry) {
        ModifyOrderParams params = new ModifyOrderParams();
        params.setParams(entry.id, entry.orderno, 1, entry.combo_id, entry.price, entry.combo_idx, entry.status, entry.date);
        return params;
    }

    Bundle buildBundle(ModifyOrderParams entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("HistoryOrderEntry", entry);
        return bundle;
    }


    public void requestOrderEntry(String orderno) {
//        UserLoginEntry uentry = new CacheManager(getActivity()).getUserLoginEntry();
//        if (uentry == null || TextUtils.isEmpty(uentry.orderno)) {
//            if (pBar != null) {
//                pBar.setVisibility(View.GONE);
//            }
//            return;
//        }
        RequestParams params = new RequestParams();
        params.add("orderno", orderno);
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/choose", params, new AsyncHttpResponseHandler() {
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
