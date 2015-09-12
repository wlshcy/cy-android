package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
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
import com.shequcun.farm.data.CouponShareEntry;
import com.shequcun.farm.data.RedPacketsEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.RedPacketsAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ShareContent;
import com.shequcun.farm.util.ShareUtil;
import com.shequcun.farm.util.ToastHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.listener.SocializeListeners;

import org.apache.http.Header;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_packets_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reuqestRedPacketsList(getTypeFromParams());
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
                requestRedPacktetShareUrl("1595002445473430");
            } else if (v == leftIv) {
                popBackStack();
            }
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
                reuqestRedPacketsList(getTypeFromParams());
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

    private void reuqestRedPacketsList(int type) {
        RequestParams params = new RequestParams();
        params.add("type", type + "");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/coupon", new AsyncHttpResponseHandler() {
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
                    if (TextUtils.isEmpty(entry.errcode)) {
                        succesRedPacketsList(entry);
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

    private void succesRedPacketsList(RedPacketsEntry entry) {
        if (entry.list == null || entry.list.isEmpty()) return;
        adapter.setServeTime(entry.time);
        adapter.addAll(entry.list);
        if (adapter.getCount() <= 0)
            addEmptyView();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (redPacketsLv != null)
                redPacketsLv.onRefreshComplete();
        }
    };

    private int getTypeFromParams() {
        Bundle bundle = getArguments();
        if (bundle == null) return 1;
        return bundle.getInt(KEY_TYPE);
    }

    private void alertRedPacketsShare(int count, final String url, final String title, final String content) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_redpackets_share);
        TextView countTv = (TextView) alert.getWindow().findViewById(R.id.red_packets_count_tv);
        countTv.setText(countTv.getText().toString().replace("A", count > 0 ? count + "" : "N"));
        alert.getWindow().findViewById(R.id.share_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                useUmengToShare(url, title, content);
            }
        });
        alert.getWindow().findViewById(R.id.close_iv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
    }

    private void requestRedPacktetShareUrl(String orderNo) {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("orderno", orderNo);
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/coupon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                CouponShareEntry entry = JsonUtilsParser.fromJson(result, CouponShareEntry.class);
                if (entry != null) {
                    if (TextUtils.isEmpty(entry.errmsg)) {
                        alertRedPacketsShare(entry.count, entry.url, entry.title, entry.content);
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

    private void useUmengToShare(String url, String title, String content) {
        if (shareController == null)
            shareController = new ShareUtil(getActivity());
        ShareContent shareContent = new ShareContent();
        shareContent.setUrlImage("drawable:///" + R.drawable.icon_share_redpackets_logo);
        shareContent.setTargetUrl(url);
        shareContent.setTitle(title);
        shareContent.setContent(content);
        shareController.wxShareContent(shareContent);
        shareController.circleShareContent(shareContent);
        shareController.postShare(mSnsPostListener);
    }

    private SocializeListeners.SnsPostListener mSnsPostListener = new SocializeListeners.SnsPostListener() {

        @Override
        public void onStart() {
        }

        @Override
        public void onComplete(SHARE_MEDIA sm, int eCode,
                               SocializeEntity sEntity) {
            String showText = "分享成功";
            if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                showText = "分享失败 [" + eCode + "]";
            }
            ToastHelper.showShort(getActivity(), showText);
        }
    };

    private ShareUtil shareController;
}
