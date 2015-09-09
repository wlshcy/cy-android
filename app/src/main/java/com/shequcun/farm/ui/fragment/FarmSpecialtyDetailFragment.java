package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.widget.CircleFlowIndicator;
import com.common.widget.ViewFlow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AddressEntry;
import com.shequcun.farm.data.AddressListEntry;
import com.shequcun.farm.data.OrderEntry;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.SqcFarmActivity;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 农庄特产详情
 * Created by mac on 15/9/6.
 */
public class FarmSpecialtyDetailFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.farm_specialty_detail_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        entry = buildRecommendEntry();
        carousel_img = (ViewFlow) v.findViewById(R.id.carousel_img);
        carousel_point = (CircleFlowIndicator) v.findViewById(R.id.carousel_point);
        back = v.findViewById(R.id.back);
        share = v.findViewById(R.id.share);
        pView = (RelativeLayout) v.findViewById(R.id.pView);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        buildCarouselAdapter();
        addChildViewToParent();
    }

    void buildCarouselAdapter() {
        if (entry == null || entry.imgs == null || entry.imgs.length <= 0) {
            carousel_img.setVisibility(View.GONE);
            return;
        }
        List<SlidesEntry> aList = new ArrayList<>();
        int size = entry.imgs.length;
        for (int i = 0; i < size; i++) {
            SlidesEntry sEntry = new SlidesEntry();
            sEntry.img = entry.imgs[i];
            aList.add(sEntry);
        }

        cAdapter = new CarouselAdapter(getActivity(), aList);
        carousel_img.setAdapter(cAdapter, 0);
        carousel_img.setFlowIndicator(carousel_point);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back)
                popBackStack();
        }
    };

    RecommendEntry buildRecommendEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (RecommendEntry) bundle.getSerializable("RecommentEntry") : null;
    }

    void addChildViewToParent() {
        if (entry == null)
            return;
        if (entry.type == 2) {//秒杀菜品
            final View childView = LayoutInflater.from(getActivity()).inflate(R.layout.pay_widget_ly, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            childView.setLayoutParams(params);
            pView.addView(childView, params);
            ((TextView) childView.findViewById(R.id.shop_cart_total_price_tv)).setText("共付:" + Utils.unitPeneyToYuan(entry.price));
            ((TextView) childView.findViewById(R.id.shop_cart_surpport_now_pay_tv)).setText("您已选好菜品了!");
            childView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {//支付
                @Override
                public void onClick(View view) {
                    requestAddress();
                }
            });
        } else if (entry.type == 1) {//普通菜品
            final View childView = LayoutInflater.from(getActivity()).inflate(R.layout.shop_cart_widget_ly, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            childView.setLayoutParams(params);
            pView.addView(childView, params);

            final TextView goods_count = (TextView) childView.findViewById(R.id.goods_count);
            final View goods_sub = childView.findViewById(R.id.goods_sub);
            final View goods_add = childView.findViewById(R.id.goods_add);

            goods_count.setText(entry.count + "");

            goods_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entry.count++;
                    goods_count.setText(entry.count + "");
                }
            });


            goods_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entry.count--;
                    goods_count.setText(entry.count + "");
                }
            });

            childView.findViewById(R.id.shop_cart_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecommendItemKey itemKey = new RecommendItemKey();
                    itemKey.object = entry;
                    new CacheManager(getActivity()).saveRecommendToDisk(itemKey);
                    IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
                    ((SqcFarmActivity) getActivity()).buildRadioButtonStatus(1);
                    popBackStack();
                }
            });
        }
    }


    void requestAddress() {
        final RecommendEntry rEntry = buildRecommendEntry();
        if (!TextUtils.isEmpty(alipay) && !TextUtils.isEmpty(orderno)) {
            gotoFragmentByAdd(buildBundle(orderno, rEntry.price, alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
            return;
        }
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            successUserAddress(entry.aList);
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
                ToastHelper.showShort(getActivity(), "请求失败,错误码" + sCode);
            }
        });
    }


    private void successUserAddress(List<AddressEntry> list) {
        if (list == null || list.size() <= 0) {
            showFillAddressDlg();
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            AddressEntry entry = list.get(i);
            if (entry.isDefault) {
                createSingleDishesOrder(entry);
                break;
            }
        }
    }

    /**
     * 创建单品订单
     */
    void createSingleDishesOrder(AddressEntry entry) {
        String address = null;
        UserLoginEntry uEntry = new CacheManager(getActivity()).getUserLoginEntry();
        if (uEntry != null && !TextUtils.isEmpty(uEntry.address)) {
            address = uEntry.address;
        }
        final RecommendEntry rEntry = buildRecommendEntry();
        RequestParams params = new RequestParams();
        params.add("type", "3");
        params.add("name", entry.name);
        params.add("mobile", entry.mobile);
        params.add("address", address);
        params.add("extras", getExtras());
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));

        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    OrderEntry entry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (rEntry != null) {
                                gotoFragmentByAdd(buildBundle(orderno = entry.orderno, rEntry.price, alipay = entry.alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                                mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
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

    public String getExtras() {
        String result = "";
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            result += entry.id + ":" + 1;
        }
        return result;
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    alipay = null;
                    orderno = null;
                    break;
            }
        }
    };

    Bundle buildBundle(String orderno, int orderMoney, String alipay, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, false, titleId);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    void showFillAddressDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("亲,您还未填写您的收货地址哦!快去完善吧!");
        builder.setNegativeButton("完善", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoFragment(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 轮播的图片
     */
    ViewFlow carousel_img;
    CircleFlowIndicator carousel_point;
    CarouselAdapter cAdapter;
    RecommendEntry entry;
    View back;
    View share;
    RelativeLayout pView;
    String alipay;
    String orderno;
}