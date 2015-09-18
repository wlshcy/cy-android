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
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ResUtil;
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
        if (entry.detail != null) {
            if (!TextUtils.isEmpty(entry.detail.image)){
                if (!ImageLoader.getInstance().isInited())
                    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
                String url = entry.detail.image+"?imageView2/2/w/"+ DeviceInfo.getDeviceWidth(this.getActivity());
                ImageLoader.getInstance().displayImage(url, contentImgIv);
            }
            contentTv.setText(entry.detail.content);
        }
        RecommendEntry localEntry = readRecommendEntryFromDisk(entry);
        this.entry.count = 1;
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
        contentTv = (TextView) v.findViewById(R.id.content_tv);
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
                shareContent.setTargetUrl(Constrants.URL_SHARE);
                shareContent.setTitle("万水千山总是情，有菜送你个红包行不行!");
                shareContent.setContent("孩子的餐桌，有菜的标准。为孩子选择健康蔬菜。");
                useUmengToShare(shareContent);
            } else if (v == producingPlaceTv) {
                gotoProducingPlaceFragment(entry.fid);
            }
        }
    };

    private void gotoProducingPlaceFragment(int id) {
        Bundle bundle = new Bundle();
        bundle.putString("Url", Constrants.URL_FARM + id);
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
            ((TextView) childView.findViewById(R.id.shop_cart_total_price_tv)).setText("共付:" + Utils.unitPeneyToYuan(entry.price / 100 >= 99 ? entry.price : entry.price + 10 * 10 * 10));
            childView.findViewById(R.id.shop_cart_surpport_now_pay_tv).setVisibility(View.GONE);
            childView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {//支付
                @Override
                public void onClick(View view) {

                    if (!isLogin()) {
                        gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
                        return;
                    }

                    ComboEntry tmpEntry = new ComboEntry();
                    tmpEntry.setPosition(0);
                    tmpEntry.prices = new int[1];
                    tmpEntry.prices[0] = entry.price / 100 >= 99 ? entry.price : entry.price + 10 * 10 * 10;
                    tmpEntry.info = new OtherInfo();
                    tmpEntry.info.extras = getExtras();
                    tmpEntry.info.type = 3;
                    tmpEntry.info.isSckill = true;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ComboEntry", tmpEntry);
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


    public String getExtras() {
        String result = "";
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            result += entry.id + ":" + 1;
        }
        return result;
    }


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
                showText = "分享失败 [" + eCode + "]";
            }
            ToastHelper.showShort(getActivity(), showText);
        }
    };

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getActivity()).getUserLoginFromDisk() != null;
    }


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
    TextView contentTv;//产品详情
    ImageView contentImgIv;//产品图片
    ImageView shareIv;
    ImageView backIv;
    TextView goods_count;
}
