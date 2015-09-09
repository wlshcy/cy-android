package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.common.widget.PullToRefreshAdapterViewBase;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshListView;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.RedPacketsEntry;
import com.shequcun.farm.ui.adapter.RedPacketsAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by cong on 15/9/7.
 */
public class RedPacketsListFragment extends BaseFragment {
    private PullToRefreshListView redPacketsLv;
    private TextView titleTv;
    private TextView rightTv;
    private RedPacketsAdapter adapter;
    private View emptyView, leftIv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_packets_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reuqestRedPacketsList();
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
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popBackStack();
        }
    };

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
                reuqestRedPacketsList();
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    private void reuqestRedPacketsList() {
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/coupon", new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody.toString());
                RedPacketsEntry entry = JsonUtilsParser.fromJson(result, RedPacketsEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errcode)) {
                        succesRedPacketsList(entry.list);
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
//        TextView emptyTv = new TextView(getActivity());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        emptyTv.setText("您还没有红包呐～");
//        emptyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//        emptyTv.setLayoutParams(layoutParams);
//        emptyTv.setGravity(Gravity.CENTER);
//        emptyTv.setVisibility(View.GONE);
        redPacketsLv.setEmptyView(emptyView);
    }

    private void succesRedPacketsList(ArrayList list) {
        if (list == null || list.isEmpty()) return;
        adapter.addAll(list);
        if (adapter.getCount() <= 0)
            addEmptyView();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (redPacketsLv != null)
                redPacketsLv.onRefreshComplete();
        }
    };
}