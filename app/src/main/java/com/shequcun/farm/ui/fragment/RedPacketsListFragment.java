package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.common.widget.PullToRefreshAdapterViewBase;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshListView;
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

import org.apache.http.Header;

import java.util.Iterator;
import java.util.List;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsListFragment extends BaseFragment {
    private PullToRefreshListView redPacketsLv;
    private TextView titleTv;
    private TextView rightTv;
    private RedPacketsAdapter adapter;
    private View emptyView, leftIv;
    public static final String KEY_TYPE = "type";
    private int type = 0;
    private int length = 10;
    private int curSize = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_packets_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getTypeFromParams();
        reuqestRedPacketsList(type, 0);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        redPacketsLv = (PullToRefreshListView) v.findViewById(R.id.red_packets_lv);
        redPacketsLv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        titleTv = (TextView) v.findViewById(R.id.title_center_text);
        titleTv.setText(R.string.use_favorable_red_packets);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        leftIv = v.findViewById(R.id.back);
        rightTv.setText(R.string.use_rule);
        emptyView = v.findViewById(R.id.empty_list_view);
        if (adapter == null) {
            adapter = new RedPacketsAdapter(getActivity());
        }
        redPacketsLv.setAdapter(adapter);
    }

    @Override
    protected void setWidgetLsn() {
        redPacketsLv.setOnItemClickListener(onItemClickListener);
        redPacketsLv.setOnRefreshListener(onRefreshListener);
        redPacketsLv.setOnRefreshingScrollToOriginal(onRefreshingScrollToOriginal);
        leftIv.setOnClickListener(onClickListener);
        rightTv.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == rightTv) {
                gotoRuleFragment();
                } else if (v == leftIv) {
                popBackStack();
            }
        }
    };

    private void gotoRuleFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("Url", "https://store.shequcun.com/coupon/yc_info");
        bundle.putInt("TitleId", R.string.red_packets_rule);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
    }

    private PullToRefreshAdapterViewBase.OnRefreshingScrollToOriginal onRefreshingScrollToOriginal = new PullToRefreshAdapterViewBase.OnRefreshingScrollToOriginal() {
        @Override
        public void onScrollToOriginal() {
            redPacketsLv.onRefreshComplete();
        }
    };

    private PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            if (redPacketsLv.isHeaderShown()) {
            } else {
                CouponEntry entry = (CouponEntry) adapter.getLastItem();
                reuqestRedPacketsList(type, entry == null ? 0 : entry.id);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;
            CouponEntry entry = (CouponEntry) adapter.getItem(position - redPacketsLv.getRefreshableView().getHeaderViewsCount());
            if (entry == null)
                return;
            if (entry.used)
                return;
            FragmentManager manager = getActivity().getSupportFragmentManager();
            if (manager != null) {
                List<Fragment> aList = manager.getFragments();
                if (aList != null && aList.size() > 0) {
                    int length = aList.size();
                    for (int i = 1; i < length; i++) {
                        Fragment fragment = aList.get(i);
                        if (fragment != null && fragment instanceof FarmSpecialtyShoppingCartFragment) {
                            ((FarmSpecialtyShoppingCartFragment) fragment).updateRedPackets(entry);
                            break;
                        }
                    }
                }
            }
            popBackStack();
        }
    };

    private void reuqestRedPacketsList(int type, int lastId) {
        RequestParams params = new RequestParams();
        /*查看优惠券时不传type，查询出所有的优惠券*/
        if (type != 0)
            params.add("type", 2 + "");
        params.add("lastid", lastId + "");
        params.add("length", length + "");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/coupon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                RedPacketsEntry entry = JsonUtilsParser.fromJson(result, RedPacketsEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errmsg)) {
                        successRedPacketsList(entry);
                    } else {
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + statusCode);
            }
        });
    }

    private void addEmptyView() {
        redPacketsLv.setEmptyView(emptyView);
    }

    private void successRedPacketsList(RedPacketsEntry entry) {
        if (entry.list == null || entry.list.isEmpty()) {
            addEmptyView();
            return;
        }
        entry.list.get(0).used = true;
        if (curSize > 0 && curSize % length < length) return;
        /*选择优惠券时*/
        if (type!=0){
            /*过滤出无效优惠券*/
            filterExpire(entry.list,entry.time);
        }else {
            adapter.setServeTime(entry.time);
        }
        adapter.addAll(entry.list);
        curSize = adapter.getCount();
    }

    private void filterExpire(List<CouponEntry> list, long serveTime) {
        Iterator<CouponEntry> i = list.iterator();
        while (i.hasNext()) {
            CouponEntry entry = i.next();
            if (entry.used || (serveTime > 0 && entry.expire <= serveTime))
                i.remove();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (redPacketsLv != null)
                redPacketsLv.onRefreshComplete();
        }
    };

    private int getTypeFromParams() {
        Bundle bundle = getArguments();
        if (bundle == null) return 0;
        return bundle.getInt(KEY_TYPE);
    }
}
