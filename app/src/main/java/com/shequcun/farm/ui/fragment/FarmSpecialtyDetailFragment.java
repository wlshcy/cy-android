package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.widget.CircleFlowIndicator;
import com.common.widget.ViewFlow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.RecommendDetailEntry;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.ui.SqcFarmActivity;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ShareContent;
import com.shequcun.farm.util.ShareUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.listener.SocializeListeners;

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

    private void setDataToView(RecommendEntry entry) {
        nameTv.setText(entry.title);
        if (!TextUtils.isEmpty(entry.descr))
            descTv.setText(entry.descr);
        else
            descTv.setVisibility(View.GONE);
        priceNowTv.setText(Utils.unitPeneyToYuan(entry.price));
        priceOriginTv.setText(Utils.unitPeneyToYuan(entry.mprice));//"¥" + ((float) entry.mprice) / 100
        personSelectTv.setText(entry.sales + "人选择");
        standardTv.setText("规格：" + entry.packw + "g/份");
        if (entry.detail != null && !TextUtils.isEmpty(entry.detail.storage))
            storageMethodTv.setText("储存方法：" + entry.detail.storage);
        else
            storageMethodTv.setText("储存方法：无");
        if (!TextUtils.isEmpty(entry.farm))
            producingPlaceTv.setText("农庄：" + entry.farm);
        else
            producingPlaceTv.setText("农庄：无");
        if (entry.detail != null && !TextUtils.isEmpty(entry.detail.image)) {
            if (!ImageLoader.getInstance().isInited())
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
            ImageLoader.getInstance().displayImage(entry.detail.image, contentImgIv);
        }
        RecommendEntry localEntry = readRecommendEntryFromDisk(entry);
        if (localEntry == null) return;
//        if (goods_count == null) return;
        this.entry = localEntry;
//        goods_count.setText(entry.count + "");
    }

    private RecommendEntry readRecommendEntryFromDisk(RecommendEntry pEntry) {
        RecommendItemKey rItemKey = new RecommendItemKey();
        rItemKey.object = pEntry;
        return new CacheManager(getActivity()).getRecommendEntry(rItemKey);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        backIv = (ImageView) v.findViewById(R.id.back);
        shareIv = (ImageView) v.findViewById(R.id.share_iv);
        entry = buildRecommendEntry();
        carousel_img = (ViewFlow) v.findViewById(R.id.carousel_img);
        carousel_point = (CircleFlowIndicator) v.findViewById(R.id.carousel_point);
        back = v.findViewById(R.id.back);
        pView = (FrameLayout) v.findViewById(R.id.pView);

        nameTv = (TextView) v.findViewById(R.id.name_tv);
        descTv = (TextView) v.findViewById(R.id.desc_tv);
        priceNowTv = (TextView) v.findViewById(R.id.price_now_tv);
        priceOriginTv = (TextView) v.findViewById(R.id.price_origin_tv);
        /*删除线*/
        priceOriginTv.setPaintFlags(priceOriginTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        personSelectTv = (TextView) v.findViewById(R.id.person_select_tv);
        standardTv = (TextView) v.findViewById(R.id.standard_tv);
        storageMethodTv = (TextView) v.findViewById(R.id.storage_method_tv);
        producingPlaceTv = (TextView) v.findViewById(R.id.producing_place_tv);
        contentImgIv = (ImageView) v.findViewById(R.id.content_img_iv);
    }

    @Override
    protected void setWidgetLsn() {
        producingPlaceTv.setOnClickListener(onClick);
        shareIv.setOnClickListener(onClick);
        back.setOnClickListener(onClick);
        buildCarouselAdapter();
        setDataToView(entry);
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
            else if (v == shareIv) {
                ShareContent shareContent = new ShareContent();
//                shareContent.setUrlImage("drawable:///" + R.drawable.icon_share);
                shareContent.setImageId(R.drawable.ic_launcher);
                shareContent.setTargetUrl("https://store.shequcun.com/about/ycabout");
                shareContent.setTitle("test");
                shareContent.setContent("test");
                useUmengToShare(shareContent);
            } else if (v == producingPlaceTv) {
                gotoProducingPlaceFragment(entry.fid);
            }
        }
    };

    private void gotoProducingPlaceFragment(int id) {
        Bundle bundle = new Bundle();
        bundle.putString("Url", "https://store.shequcun.com/yc_farm_item/" + id);
        bundle.putInt("TitleId", R.string.farm_info);
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new SetWebViewFragment(), SetWebViewFragment.class.getName());
    }

    RecommendEntry buildRecommendEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (RecommendEntry) bundle.getSerializable("RecommentEntry") : null;
    }

    void addChildViewToParent() {
        if (entry == null)
            return;
        if (entry.type == 2) {//秒杀菜品
            final View childView = LayoutInflater.from(getActivity()).inflate(R.layout.pay_widget_ly, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            pView.addView(childView, params);
            ((TextView) childView.findViewById(R.id.shop_cart_total_price_tv)).setText("共付:" + Utils.unitPeneyToYuan(entry.price));
            childView.findViewById(R.id.shop_cart_surpport_now_pay_tv).setVisibility(View.GONE);
            childView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {//支付
                @Override
                public void onClick(View view) {
                    ComboEntry entry = new ComboEntry();
                    entry.setPosition(0);
                    entry.prices = new int[1];
                    entry.prices[0] = buildRecommendEntry().price;
                    entry.info = new OtherInfo();
                    entry.info.extras = getExtras();
                    entry.info.type = 3;
                    entry.info.isSckill = true;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ComboEntry", entry);
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, new PayComboFragment(), PayComboFragment.class.getName());

                }
            });
        } else if (entry.type == 1) {//普通菜品
            View childView = LayoutInflater.from(getActivity()).inflate(R.layout.shop_cart_widget_ly, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            pView.addView(childView, params);

            goods_count = (TextView) childView.findViewById(R.id.goods_count);
            View goods_sub = childView.findViewById(R.id.goods_sub);
            View goods_add = childView.findViewById(R.id.goods_add);

            goods_count.setText(entry.count + "");

            goods_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (entry.count > entry.remains) {
                        alertOutOfRemains();
                        return;
                    }

                    entry.count++;
                    goods_count.setText(entry.count + "");
                }
            });


            goods_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (entry.count <= 0)
                        return;
                    entry.count--;
                    if (entry.count < 0) entry.count = 0;
                    goods_count.setText(entry.count + "");
                }
            });

            childView.findViewById(R.id.shop_cart_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entry.count > 0) {
                        RecommendItemKey itemKey = new RecommendItemKey();
                        itemKey.object = entry;
                        new CacheManager(getActivity()).saveRecommendToDisk(itemKey);
                        IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
                        ToastHelper.showShort(getActivity(), R.string.add_shop_cart_success);
                    }
//
//                    ((SqcFarmActivity) getActivity()).buildRadioButtonStatus(1);
//                    popBackStack();
                }
            });
        }
    }


//    void requestAddress() {
//        final RecommendEntry rEntry = buildRecommendEntry();
//        if (!TextUtils.isEmpty(alipay) && !TextUtils.isEmpty(orderno)) {
//            gotoFragmentByAdd(buildBundle(orderno, rEntry.price, alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
//            return;
//        }
//        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
//        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "user/address", new AsyncHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                pDlg.show();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                pDlg.dismiss();
//            }
//
//            @Override
//            public void onSuccess(int sCode, Header[] h, byte[] data) {
//                if (data != null && data.length > 0) {
//                    AddressListEntry entry = JsonUtilsParser.fromJson(new String(data), AddressListEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
//                            successUserAddress(entry.aList);
//                            return;
//                        }
//                        ToastHelper.showShort(getActivity(), entry.errmsg);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//                if (sCode == 0) {
//                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(getActivity(), "请求失败,错误码" + sCode);
//            }
//        });
//    }


//    private void successUserAddress(List<AddressEntry> list) {
//        if (list == null || list.size() <= 0) {
//            showFillAddressDlg();
//            return;
//        }
//        int size = list.size();
//        for (int i = 0; i < size; ++i) {
//            AddressEntry entry = list.get(i);
//            if (entry.isDefault) {
//                createSingleDishesOrder(entry);
//                break;
//            }
//        }
//    }

    /**
     * 创建单品订单
     */
//    void createSingleDishesOrder(AddressEntry entry) {
//        String address = null;
//        UserLoginEntry uEntry = new CacheManager(getActivity()).getUserLoginEntry();
//        if (uEntry != null && !TextUtils.isEmpty(uEntry.address)) {
//            address = uEntry.address;
//        }
//        final RecommendEntry rEntry = buildRecommendEntry();
//        RequestParams params = new RequestParams();
//        params.add("type", "3");
//        params.add("name", entry.name);
//        params.add("mobile", entry.mobile);
//        params.add("address", address);
//        params.add("extras", getExtras());
//        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
//
//        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
//        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "cai/order", params, new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                pDlg.show();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                pDlg.dismiss();
//            }
//
//            @Override
//            public void onSuccess(int sCode, Header[] h, byte[] data) {
//                if (data != null && data.length > 0) {
//                    OrderEntry entry = JsonUtilsParser.fromJson(new String(data), OrderEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
//                            if (rEntry != null) {
//                                gotoFragmentByAdd(buildBundle(orderno = entry.orderno, rEntry.price, alipay = entry.alipay, R.string.pay_success), R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());
//                                mHandler.sendEmptyMessageDelayed(0, 30 * 60 * 1000);
//                            }
//                            return;
//                        }
//                        ToastHelper.showShort(getActivity(), entry.errmsg);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//                if (sCode == 0) {
//                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(getActivity(), "错误码" + sCode);
//            }
//        });
//    }
    public String getExtras() {
        String result = "";
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            result += entry.id + ":" + 1;
        }
        return result;
    }

//    private android.os.Handler mHandler = new android.os.Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    alipay = null;
//                    orderno = null;
//                    break;
//            }
//        }
//    };

//    Bundle buildBundle(String orderno, int orderMoney, String alipay, int titleId) {
//        Bundle bundle = new Bundle();
//        PayParams payParams = new PayParams();
//        payParams.setParams(orderno, orderMoney, alipay, false, titleId);
//        bundle.putSerializable("PayParams", payParams);
//        return bundle;
//    }

//    void showFillAddressDlg() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("提示");
//        builder.setMessage("亲,您还未填写您的收货地址哦!快去完善吧!");
//        builder.setNegativeButton("完善", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                gotoFragment(R.id.mainpage_ly, new AddressFragment(), AddressFragment.class.getName());
//            }
//        });
//        builder.setNeutralButton("取消", null);
//        builder.create().show();
//    }


//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
////        mHandler.removeCallbacksAndMessages(null);
//    }

    private void alertOutOfMaxpacks() {
        String content = getResources().getString(R.string.out_of_maxpacks);
        alertDialog(content);
    }

    private void alertOutOfRemains() {
        String content = "亲,该菜品库存不足,请选其他菜品吧";
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

    private void useUmengToShare(ShareContent shareContent) {
        if (shareController == null)
            shareController = new ShareUtil(getActivity());
        shareController.shareAll(shareContent);
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
                Log.e("FarmSpecialty", "ecode" + eCode);
                showText = "分享失败 [" + eCode + "]";
            }
            ToastHelper.showShort(getActivity(), showText);
        }
    };

    private ShareUtil shareController;

    /**
     * 轮播的图片
     */
    ViewFlow carousel_img;
    CircleFlowIndicator carousel_point;
    CarouselAdapter cAdapter;
    RecommendEntry entry;
    View back;
    FrameLayout pView;
//    String alipay;
//    String orderno;

    TextView nameTv;//产品名称
    TextView descTv;//产品描述
    TextView priceNowTv;//产品现价
    TextView priceOriginTv;//产品原价
    TextView personSelectTv;//产品已选人数
    TextView standardTv;//产品规格
    TextView storageMethodTv;//产品冷藏方法
    TextView producingPlaceTv;//产品产地
    ImageView contentImgIv;//产品图片
    ImageView shareIv;
    ImageView backIv;
    TextView goods_count;
}
