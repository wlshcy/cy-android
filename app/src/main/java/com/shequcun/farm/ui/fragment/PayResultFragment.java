package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.CouponShareEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.RecommendAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
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
 * 支付结果界面
 * Created by apple on 15/8/18.
 */
public class PayResultFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_result_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        clearStack();
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        int titleId = getTitleId();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.order_result);
        recoTv = (TextView) v.findViewById(R.id.common_small_tv);
        recoTv.setVisibility(View.GONE);
        result_tip = (TextView) v.findViewById(R.id.result_tip);
        result_tip.setText(titleId == R.string.pay_success ? R.string.order_pay_success : R.string.order_submit_success);
    }

    @Override
    protected void setWidgetLsn() {
        buildAdapter();
        back.setOnClickListener(onClick);
        IntentUtil.sendUpdateComboMsg(getActivity());
//        if (isRecomDishes())
//            requestRecomendDishes();
    }


    boolean isRecomDishes() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null) {
            return entry.isRecoDishes;
        }
        return false;
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back) {
                clearStack();
            }
        }
    };

    void buildAdapter() {
        if (adapter == null)
            adapter = new RecommendAdapter(getActivity());
        adapter.buildOnClickLsn(onGoodsImgLsn, onBuyLsn);
        mLv.setAdapter(adapter);
    }


    AvoidDoubleClickListener onBuyLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            int position = (int) v.getTag();
            if (adapter == null)
                return;

            RecommendEntry entry = adapter.getItem(position);

            if (entry.remains <= 0) {
                alertOutOfRemains();
                return;
            }

            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new SingleDishesFragment(), SingleDishesFragment.class.getName());
        }
    };

    /**
     * 剩余量不足
     */
    private void alertOutOfRemains() {
        String content = getResources().getString(R.string.out_of_remains);
        alertDialog(content);
    }

    /**
     * 库存不足
     *
     * @param maxpacks
     */
    private void alertOutOfMaxpacks(int maxpacks) {
        String content = getResources().getString(R.string.out_of_maxpacks);
        content = content.replace("A", maxpacks + "");
        alertDialog(content);
    }

    private void alertDialog(String content) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.alert_dialog);
        TextView tv = (TextView) alert.getWindow().findViewById(R.id.content_tv);
        tv.setText(content);
        alert.getWindow().findViewById(R.id.ok_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
    }

    Bundle buildBundle(RecommendEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecommendEntry", entry);
        return bundle;
    }

    AvoidDoubleClickListener onGoodsImgLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            RecommendEntry entry = adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RecommendEntry", entry);
            gotoFragmentByAnimation(bundle, R.id.mainpage_ly, new RecommendGoodsDetailsFragment(), RecommendGoodsDetailsFragment.class.getName());
        }
    };

    void requestRecomendDishes() {
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/itemlist", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    RecommentListEntry entry = JsonUtilsParser.fromJson(new String(data), RecommentListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            addDataToAdapter(entry.aList);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
//        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/itemlist", new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int sCode, Header[] h, byte[] data) {
//                if (data != null && data.length > 0) {
//                    RecommentListEntry entry = JsonUtilsParser.fromJson(new String(data), RecommentListEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
//                            addDataToAdapter(entry.aList);
//                            return;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//            }
//        });
    }

    void addDataToAdapter(List<RecommendEntry> aList) {
        if (aList != null && aList.size() > 0) {
            recoTv.setVisibility(View.VISIBLE);
            recoTv.setText("为您特别推荐");
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
    }

    private int getTitleId() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null)
            return entry.titleId;

        return R.string.pay_success;
    }

    private void requestRedPacktetShareUrl(String orderNo){
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        params.add("orderno",orderNo);
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/coupon", params,new AsyncHttpResponseHandler() {
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
                        useUmengToShare(entry.url,entry.title,entry.content);
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

    private void useUmengToShare(String url,String title,String content) {
        if (shareController == null)
            shareController = new ShareUtil(getActivity());
        ShareContent shareContent = new ShareContent();
        shareContent.setUrlImage("drawable:///" + R.drawable.ic_launcher);
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

    TextView recoTv;
    ListView mLv;
    RecommendAdapter adapter;
    View back;
    TextView result_tip;
}
