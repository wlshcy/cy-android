package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.CouponEntry;
import com.shequcun.farm.data.RedPacketsEntry;
import com.shequcun.farm.ui.adapter.RedPacketsAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsInvalidListFragment extends BaseFragment {
    @Bind(R.id.red_packets_lv)
    ExpandableHeightListView redPacketsLv;
    @Bind(R.id.title_center_text)
    TextView titleTv;
    @Bind(R.id.title_right_text)
    TextView rightTv;
    private RedPacketsAdapter adapter;
    @Bind(R.id.empty_ll)
    LinearLayout emptyView;
    @Bind(R.id.pView)
    PullToRefreshScrollView pView;
    public static final String KEY_TYPE = "type";
    public static final String KEY_ACTION = "action";
    /*1、2 选择红包(1.套餐优惠券, 2.单品优惠券)*/
    private int type = 0;
    private int action = 0;
    private int length = 10;
    private int curSize = 0;
    public static final int ACTION_LOOK = 1;

    int payMoney = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_packets_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getTypeFromParams();
        requstRedPacketsList(type, 0);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        pView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        titleTv.setText("失效红包");
        if (adapter == null) {
            adapter = new RedPacketsAdapter(getBaseAct());
        }
        redPacketsLv.setAdapter(adapter);
        redPacketsLv.setExpanded(true);
    }

    @Override
    protected void setWidgetLsn() {
        action = getActionFromParams();
        if (action != ACTION_LOOK)
            redPacketsLv.setOnItemClickListener(onItemClickListener);
//        redPacketsLv.setOnRefreshListener(onRefreshListener);
//        redPacketsLv.setOnRefreshingScrollToOriginal(onRefreshingScrollToOriginal);

        pView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (adapter == null)
                    return;
                CouponEntry entry = (CouponEntry) adapter.getLastItem();
                requstRedPacketsList(type, entry == null ? 0 : entry.id);
            }
        });
    }

    @OnClick({R.id.back, R.id.title_right_text})
    void doClick(View v) {
        if (v == rightTv)
            gotoRuleFragment();
        else popBackStack();
    }

    private void gotoRuleFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("Url", "https://store.shequcun.com/coupon/yc_info");
        bundle.putInt("TitleId", R.string.red_packets_rule);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
    }

//    private PullToRefreshAdapterViewBase.OnRefreshingScrollToOriginal onRefreshingScrollToOriginal = new PullToRefreshAdapterViewBase.OnRefreshingScrollToOriginal() {
//        @Override
//        public void onScrollToOriginal() {
//            redPacketsLv.onRefreshComplete();
//        }
//    };
//
//    private PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
//        @Override
//        public void onRefresh(PullToRefreshBase refreshView) {
//            if (redPacketsLv.isHeaderShown()) {
//            } else {
//                CouponEntry entry = (CouponEntry) adapter.getLastItem();
//                reuqestRedPacketsList(type, entry == null ? 0 : entry.id);
//            }
//        }
//    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;
            CouponEntry entry = (CouponEntry) adapter.getItem(position);
            if (entry == null)
                return;
            if (entry.used)
                return;
            FragmentManager manager = getBaseAct().getSupportFragmentManager();
            if (manager != null) {
                List<Fragment> aList = manager.getFragments();
                if (aList != null && aList.size() > 0) {
                    int length = aList.size();
                    for (int i = 1; i < length; i++) {
                        Fragment fragment = aList.get(i);
                        if (fragment != null && fragment instanceof PayFragment) {
                            ((PayFragment) fragment).updateRedPackets(entry);
                            break;
                        }
                    }
                }
            }
        }
    };

    private void requstRedPacketsList(int type, int lastId) {
        RequestParams params = new RequestParams();
        /*查看优惠券时不传type，查询出所有的优惠券*/
        if (type != 0)
            params.add("type", type + "");
        params.add("usable", "0");
        params.add("lastid", lastId + "");
        params.add("length", length + "");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/coupon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
//                mHandler.sendEmptyMessageDelayed(0, 1000);
                if (pView != null)
                    pView.onRefreshComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                RedPacketsEntry entry = JsonUtilsParser.fromJson(result, RedPacketsEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errmsg)) {
                        successRedPacketsList(entry);
                    } else {
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "请求失败,错误码" + statusCode);
            }
        });
    }

    private void addEmptyView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.empty_text_ly, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyView.addView(view, layoutParams);
        redPacketsLv.setEmptyView(emptyView);
    }

    private void successRedPacketsList(RedPacketsEntry entry) {
        if (entry == null)
            return;
        List<CouponEntry> list = entry.list;
        if (list == null || list.isEmpty()) {
            addEmptyView();
            return;
        }
        adapter.setServeTime(entry.time);
        adapter.addAll(entry.list);
        curSize = adapter.getCount();
        if (curSize % length > 0) {
            pView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    private int getTypeFromParams() {
        Bundle bundle = getArguments();
        if (bundle == null) return 0;
        return bundle.getInt(KEY_TYPE);
    }

    private int getActionFromParams() {
        Bundle bundle = getArguments();
        if (bundle == null) return 0;
        return bundle.getInt(KEY_ACTION);
    }
}
