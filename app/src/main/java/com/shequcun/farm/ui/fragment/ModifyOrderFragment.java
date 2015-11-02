package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AlreadyPurchasedEntry;
import com.shequcun.farm.data.AlreadyPurchasedListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.CouponShareEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.MyOrderDetailListEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.platform.ShareContent;
import com.shequcun.farm.platform.ShareManager;
import com.shequcun.farm.ui.adapter.AlreadyPurchasedAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 修改订单
 * Created by apple on 15/8/20.
 */
public class ModifyOrderFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lookup_order_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        hEntry = buildModifyOrderObj();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.order_details);
        if (getOrderStatus() == 0) {//未付款
            order_btn.setText(R.string.pay_immediately);
        } else if (getOrderStatus() == 1) {//待配送
            int orderType = getOrderType();
            if (orderType == 2) {
                order_btn.setVisibility(View.GONE);
            }
            order_btn.setText(orderType == 1 ? R.string.re_choose_dishes : R.string.cancel_order);
        } else if (getOrderStatus() == 2) {//订单配送中
            order_btn.setVisibility(View.GONE);
        } else {
            order_btn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setWidgetLsn() {
        if (TextUtils.isEmpty(hEntry.name) || TextUtils.isEmpty(hEntry.address) || TextUtils.isEmpty(hEntry.mobile)) {
//            requestUserAddress();
            pAddressView.setVisibility(View.GONE);
        } else {
            addressLy.setVisibility(View.VISIBLE);
            addressee_info.setText(hEntry.name + "  " + hEntry.mobile);
            String addressStr = hEntry.address;
            address.setText("地址: " + addressStr);
        }
        order_btn.setOnClickListener(onClick);
        redPacketsIv.setOnClickListener(onClick);
        requestOrderDetails();
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }


    private ModifyOrderParams buildModifyOrderObj() {
        Bundle bundle = getArguments();
        return bundle != null ? (ModifyOrderParams) bundle.getSerializable("HistoryOrderEntry") : null;
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == order_btn) {
                int orderType = getOrderType();
                if (getOrderStatus() == 1) {//待配送
                    order_btn.setText(orderType == 1 || orderType == 2 ? R.string.re_choose_dishes : R.string.cancel_order);
                    if (orderType == 1 || orderType == 2) {
                        showConfirmDlg();
                    } else if (getOrderType() == 0) {
                        cancelOrder();
                    }
                } else if (getOrderStatus() == 0) {
                    ComboEntry entry = new ComboEntry();
                    entry.setPosition(0);
                    entry.prices = new int[1];
                    entry.prices[0] = hEntry.price / 100 >= 99 ? hEntry.price : hEntry.price + 10 * 10 * 10;
                    entry.orderno = hEntry.orderno;
                    entry.info = new OtherInfo();
                    entry.info.isSckill = hEntry.type == 3 ? true : false;// 1.选菜菜品 2.普通单品, 3.秒杀单品
                    entry.info.item_type = hEntry.type;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ComboEntry", entry);
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
                }
            } else if (v == redPacketsIv) {
                if (hEntry == null) return;
                if (TextUtils.isEmpty(hEntry.orderno)) return;
                requestRedPacktetShareUrl(hEntry.orderno);
            }
        }
    };

    String getOrderNumber() {
        return hEntry != null ? hEntry.orderno : null;
    }


    void cancelOrder() {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", hEntry.id + "");
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/delorder", params, new AsyncHttpResponseHandler() {

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
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (TextUtils.isEmpty(jObj.optString("errmsg"))) {
                            ToastHelper.showShort(getBaseAct(), R.string.cancel_order_success);
                            popBackStack();
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), jObj.optString("errmsg"));

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }

                ToastHelper.showShort(getBaseAct(), "错误码" + sCode);
            }
        });
    }

    /**
     * 订单类型
     *
     * @return int 1.套餐订单 2.选菜订单, 3.单品订单, 4.自动选菜订单
     */
    int getOrderType() {
        return hEntry != null ? hEntry.type : 0;
    }

    /**
     * int	订单状态
     *
     * @return 0.未付款, 1.待配送, 2.配送中, 3.配送完成, 4.取消订单, 5.套餐配送完成
     */
    int getOrderStatus() {
        return hEntry != null ? hEntry.status : 0;
    }

    void requestOrderDetails() {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("orderno", getOrderNumber());
        // cai/v2/orderdtl  cai/orderdtl
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/v2/orderdtl", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {

                    MyOrderDetailListEntry entry = JsonUtilsParser.fromJson(new String(data), MyOrderDetailListEntry.class);

                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (entry.aList != null && entry.aList.size() > 0) {
                                List<AlreadyPurchasedEntry> aList = new ArrayList<AlreadyPurchasedEntry>();
                                //AlreadyPurchasedListEntry
                                ArrayList<AlreadyPurchasedEntry> dIe = new ArrayList<AlreadyPurchasedEntry>();
                                int size = entry.aList.size();

                                for (int i = 0; i < size; ++i) {
                                    AlreadyPurchasedListEntry tmpEntry = entry.aList.get(i);
                                    if (tmpEntry != null && tmpEntry.aList != null && tmpEntry.aList.size() > 0) {
                                        for (int j = 0; j < tmpEntry.aList.size(); ++j) {
                                            aList.add(tmpEntry.aList.get(j));
                                        }
                                    }

                                    if (tmpEntry != null && tmpEntry.dIe != null && tmpEntry.dIe.size() > 0) {
                                        for (int j = 0; j < tmpEntry.dIe.size(); ++j) {
                                            dIe.add(tmpEntry.dIe.get(j));
                                        }
                                    }
                                }
                                setRedPacketsView(entry.cpflag);
                                buildAdapter(aList);
                                addSparesFooter(dIe);
                            }
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "错误码" + sCode);
            }

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
        });
    }

    void addHeaderView() {
        if (hEntry != null && !TextUtils.isEmpty(hEntry.date) && mLv != null) {
            View headView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_footer_ly, null);
            ((TextView) headView.findViewById(R.id.distribution_date)).setText(hEntry.date);
            mLv.addHeaderView(headView, null, false);
        }
    }

    void addFooter(int part) {
        if (getBaseAct() != null && mLv != null) {
            View footerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_footer_ly, null);
            ((TextView) footerView.findViewById(R.id.distribution_date)).setText(hEntry.placeAnOrderDate);
            ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
            mLv.addFooterView(footerView, null, false);
        }

    }

    void buildAdapter(List<AlreadyPurchasedEntry> aList) {
        if (aList == null || aList.size() <= 0 || hEntry == null || mLv == null)
            return;
        int part = 0;
        int allWeight = 0;
        for (AlreadyPurchasedEntry entry : aList) {
            part += entry.packs;
            allWeight += entry.packs * entry.packw;
        }
        hEntry.allWeight = allWeight;
        addFooter(part);
        addHeaderView();
        if (adapter == null) {
            adapter = new AlreadyPurchasedAdapter(getBaseAct());
        }
        mLv.setAdapter(adapter);
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
    }

    private void setRedPacketsView(boolean visible) {
        if (redPacketsIv != null)
            redPacketsIv.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    Bundle buildBundle(String orderno, int orderMoney, String alipay, int titleId) {
        Bundle bundle = new Bundle();
        PayParams payParams = new PayParams();
        payParams.setParams(orderno, orderMoney, alipay, false, titleId, false);
        bundle.putSerializable("PayParams", payParams);
        return bundle;
    }

    private void showConfirmDlg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseAct());
        builder.setTitle("提示");
//        builder.setMessage(R.string.choose_dishes_tip);
        builder.setMessage(R.string.re_choose_dishes_tip);
        builder.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                PhoneUtil.gotoCall(getBaseAct(), Constrants.Customer_Service_Phone);
                ComboEntry entry = new ComboEntry();
                entry.id = hEntry.combo_id;
                entry.setPosition(0);
                entry.weights = new int[1];
                entry.weights[0] = hEntry.allWeight;
                entry.prices = new int[1];
                entry.prices[0] = hEntry.price;
                entry.combo_idx = hEntry.combo_idx;
                entry.orderno = hEntry.orderno;
                entry.times = hEntry.times;
                entry.duration = hEntry.duration;
                entry.shipday = hEntry.shipday;
                entry.con = TextUtils.isEmpty(hEntry.con) ? entry.orderno : hEntry.con;
                Bundle bundle = new Bundle();
                bundle.putSerializable("ComboEntry", entry);
                popBackStack();
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            }
        });
        builder.setNeutralButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void requestRedPacktetShareUrl(String orderNo) {
        RequestParams params = new RequestParams();
        params.add("_xsrf", PersistanceManager.getCookieValue(getBaseAct()));
        params.add("orderno", orderNo);
        HttpRequestUtil.getHttpClient(getBaseAct()).post(LocalParams.getBaseUrl() + "cai/coupon", params, new AsyncHttpResponseHandler() {
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
                        useUmengToShare(entry.url, entry.title, entry.content);
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

    private void useUmengToShare(String url, String title, String content) {
//        if (shareController == null)
//            shareController = new ShareUtil(getBaseAct());
        ShareContent shareContent = new ShareContent();
        shareContent.setImageId(R.drawable.icon_share_redpackets_logo);
        shareContent.setTargetUrl(url);
        shareContent.setTitle(title);
        shareContent.setContent(content);
        ShareManager.shareByFrame(getBaseAct(), shareContent);
    }

//    private SocializeListeners.SnsPostListener mSnsPostListener = new SocializeListeners.SnsPostListener() {
//
//        @Override
//        public void onStart() {
//        }
//
//        @Override
//        public void onComplete(SHARE_MEDIA sm, int eCode,
//                               SocializeEntity sEntity) {
//            String showText = "分享成功";
//            if (eCode != StatusCode.ST_CODE_SUCCESSED) {
//                showText = "分享失败 [" + eCode + "]";
//            }
//            ToastHelper.showShort(getBaseAct(), showText);
//        }
//    };


    /**
     * 添加备选菜
     */
    void addSparesFooter(List<AlreadyPurchasedEntry> aList) {
        if (hEntry != null && hEntry.fList != null && hEntry.fList.size() > 0) {
            if (aList != null && aList.size() > 0) {
                View view = LayoutInflater.from(getBaseAct()).inflate(R.layout.remark_footer_ly, null);
                TextView textView = (TextView) view.findViewById(R.id.title_tv);
                textView.setText("固定蔬菜");
                mLv.addFooterView(view);
                for (FixedComboEntry entry : hEntry.fList) {
                    if (entry == null)
                        continue;
                    View headerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_item_ly, null);
                    ImageView goodImg = (ImageView) headerView.findViewById(R.id.goods_img);
                    if (entry.imgs != null && entry.imgs.length > 0)
                        ImageLoader.getInstance().displayImage(entry.imgs[0] + "?imageview2/2/w/180", goodImg);
                    ((TextView) headerView.findViewById(R.id.goods_name)).setText(entry.title);
                    ((TextView) headerView.findViewById(R.id.goods_price)).setText(entry.quantity + entry.unit + "/份");
                    headerView.findViewById(R.id.goods_count).setVisibility(View.GONE);
                    mLv.addFooterView(headerView);
                }
            }
        }

        if (aList != null && aList.size() > 0) {
            mLv.addFooterView(LayoutInflater.from(getBaseAct()).inflate(R.layout.remark_footer_ly, null), null, false);
            for (int i = 0; i < aList.size(); i++) {
                View footerView = LayoutInflater.from(getBaseAct()).inflate(R.layout.order_details_item_ly, null);
                ImageView goodsImg = (ImageView) footerView.findViewById(R.id.goods_img);
                ImageLoader.getInstance().displayImage(aList.get(i).img + "?imageview2/2/w/180", goodsImg);
                ((TextView) footerView.findViewById(R.id.goods_name)).setText(aList.get(i).title);
                (footerView.findViewById(R.id.goods_price)).setVisibility(View.GONE);
                footerView.findViewById(R.id.goods_count).setVisibility(View.GONE);
                mLv.addFooterView(footerView, null, false);
            }
        }
    }

    //    private ShareUtil shareController;
    @Bind(R.id.addressee_ly)
    View addressLy;
    @Bind(R.id.redPacketsIv)
    View redPacketsIv;
    ModifyOrderParams hEntry;
    AlreadyPurchasedAdapter adapter;
    @Bind(R.id.back)
    View back;
    @Bind(R.id.order_btn)
    TextView order_btn;
    @Bind(R.id.addressee_info)
    TextView addressee_info;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.mLv)
    ListView mLv;
    @Bind(R.id.pAddressView)
    View pAddressView;
}
