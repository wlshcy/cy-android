package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.widget.CircleFlowIndicator;
import com.common.widget.ViewFlow;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.platform.ShareContent;
import com.shequcun.farm.platform.ShareManager;
import com.shequcun.farm.ui.SqcFarmActivity;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.shequcun.farm.util.ShareUtil;

/**
 * 农庄特产详情
 * Created by mac on 15/9/6.
 */
public class FarmSpecialtyDetailFragment extends BaseFragment {
    @Bind(R.id.slider)
    SliderLayout slider;
    @Bind(R.id.custom_indicator2)
    PagerIndicator customIndicator2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.farm_specialty_detail_ly, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setDataToView() {
        nameTv.setText(entry.title);
        if (!TextUtils.isEmpty(entry.descr))
            descTv.setText(entry.descr);
        else
            descTv.setVisibility(View.GONE);
        priceNowTv.setText(Utils.unitPeneyToYuan(entry.price));
        priceOriginTv.setText(Utils.unitPeneyToYuan(entry.mprice));//"¥" + ((float) entry.mprice) / 100
        personSelectTv.setText(entry.sales + "人选择");
        standardTv.setText("商品规格：" + Utils.unitConversion(entry.packw) + "/份");
        if (entry.detail != null && !TextUtils.isEmpty(entry.detail.storage))
            storageMethodTv.setText("储存方法：" + entry.detail.storage);
        else
            storageMethodTv.setText("储存方法：无");
        if (!TextUtils.isEmpty(entry.farm))
            producingPlaceTv.setText("来自农庄：" + entry.farm);
        else
            producingPlaceTv.setText("来自农庄：无");
        if (entry.detail != null) {
            if (!TextUtils.isEmpty(entry.detail.image)) {
                String url = entry.detail.image + "?imageView2/2/w/" + DeviceInfo.getDeviceWidth(this.getActivity());
                ImageLoader.getInstance().displayImage(url, contentImgIv, Constrants.image_display_options_disc);
            }
            contentTv.setText(entry.detail.content);
        }
        if (!entry.isShowDtlFooter) {
            entry.count = 1;
            RecommendEntry localEntry = readRecommendEntryFromDisk(entry);
            if (localEntry == null) return;
            entry = localEntry;
        }
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
        entry = buildRecommendEntry();
        /*删除线*/
        priceOriginTv.setPaintFlags(priceOriginTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    protected void setWidgetLsn() {
        producingPlaceTv.setOnClickListener(onClick);
        shareIv.setOnClickListener(onClick);
        buildCarouselAdapter();
        setDataToView();
        addChildViewToParent();
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    void buildCarouselAdapter() {
//        if (entry == null || entry.imgs == null || entry.imgs.length <= 0) {
//            carousel_img.setVisibility(View.GONE);
//            dismissImgProgress();
//            return;
//        }
//        final List<SlidesEntry> aList = new ArrayList<>();
//        int size = entry.imgs.length;
//        for (int i = 0; i < size; i++) {
//            SlidesEntry sEntry = new SlidesEntry();
//            sEntry.img = entry.imgs[i];
//            aList.add(sEntry);
//        }
//        cAdapter = new CarouselAdapter(getActivity(), aList);
//        cAdapter.setWidth(DeviceInfo.getDeviceWidth(getActivity()));
//        carousel_img.setOnViewSwitchListener(viewSwitchListener);
//        cAdapter.setImageLoaderListener(imageLoaderListener);
//        carousel_img.setAdapter(cAdapter, 0);
//        carousel_img.setFlowIndicator(carousel_point);
        if (entry == null || entry.imgs == null || entry.imgs.length < 1) return;
        for (String url : entry.imgs) {
            addSliderUrl(url);
        }
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(4000);

    }

    private void addSliderUrl(String url) {
        DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
        // initialize a SliderLayout
        url = url + "?imageView2/2/" + DeviceInfo.getDeviceWidth(getActivity());
        textSliderView
                .description("")
                .image(url)
                .setScaleType(BaseSliderView.ScaleType.CenterCrop);
        //add your extra information
//        textSliderView.bundle(new Bundle());
//        textSliderView.getBundle()
//                .putString("extra", name);
        slider.addSlider(textSliderView);
    }

    @Override
    public void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == shareIv) {
                ShareContent sharecontent = new ShareContent();
//                sharecontent.seturlimage("drawable:///" + r.drawable.icon_share);
                sharecontent.setImageId(R.drawable.icon_share_logo);
                sharecontent.setTargetUrl(Constrants.URL_SHARE + entry.id);
                sharecontent.setTitle("有菜，不能说的秘密！");
                sharecontent.setContent("孩子的餐桌我们的标准，走心，连蔬菜都这么有bigger！");
                ShareManager.shareByFrame(getActivity(), sharecontent);
            } else if (v == producingPlaceTv) {
                gotoProducingPlaceFragment(entry.fid);
            }
        }
    };

    private void gotoShoppingCart() {
        popBackStack();
//        if (entry != null && entry.count > 0) {
//            RecommendItemKey itemKey = new RecommendItemKey();
//            itemKey.object = entry;
//            new CacheManager(getActivity()).saveRecommendToDisk(itemKey);
//            IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity());
//        }
        SqcFarmActivity mAct = (SqcFarmActivity) getActivity();
        mAct.buildRadioButtonStatus(1);
    }

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

                    if (entry.type == 2 && entry.bought) {
                        new com.shequcun.farm.dlg.AlertDialog().alertDialog(getActivity(), R.string.spike_error_tip);
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
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, new PayFragment(), PayFragment.class.getName());

                }
            });
        } else if (entry.type == 1 && !entry.isShowDtlFooter) {//普通菜品
            View childView = LayoutInflater.from(getActivity()).inflate(R.layout.shop_cart_widget_ly, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            pView.addView(childView, params);
            final TextView goods_count = (TextView) childView.findViewById(R.id.goods_count);
            View goods_sub = childView.findViewById(R.id.goods_sub);
            View goods_add = childView.findViewById(R.id.goods_add);
            View go_to_shop_cart_tv = childView.findViewById(R.id.go_to_shop_cart_tv);
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
                }
            });

            go_to_shop_cart_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoShoppingCart();
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
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    private Handler mHandler = new Handler();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        ButterKnife.unbind(this);
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getActivity()).getUserLoginEntry() != null;
    }

    private CarouselAdapter.ImageLoaderListener imageLoaderListener = new CarouselAdapter.ImageLoaderListener() {
        @Override
        public void loadFinish() {
//            dismissImgProgress();
        }

        @Override
        public void loadStart() {
//            popImgProgress();
        }
    };

//    private ViewFlow.ViewSwitchListener viewSwitchListener = new ViewFlow.ViewSwitchListener() {
//        @Override
//        public void onSwitched(View view, int position) {
//            if (cAdapter != null)
//                cAdapter.setCurVisibleIndex(position);
//            ViewFlow viewFlow = (ViewFlow) view.getParent();
//            View view1 = viewFlow.getChildAt(position);
//            if (view1 == null) return;
//            View img = view1.findViewById(R.id.imgView);
//            if (img == null) return;
//            if (img.getTag() == null) {
//                popImgProgress();
//            } else {
//                if (img.getTag() instanceof String) {
//                    String s = (String) img.getTag();
//                }
//                dismissImgProgress();
//            }
//        }
//    };

//    private void dismissImgProgress() {
//        if (imgProgress == null) return;
//        if (imgProgress.getVisibility() == View.VISIBLE)
//            imgProgress.setVisibility(View.GONE);
//    }
//
//    private void popImgProgress() {
//        if (imgProgress == null) return;
//        if (imgProgress.getVisibility() == View.GONE)
//            imgProgress.setVisibility(View.VISIBLE);
//    }

    /**
     * 轮播的图片
     */
//    @Bind(R.id.carousel_img)
//    ViewFlow carousel_img;
//    @Bind(R.id.carousel_point)
//    CircleFlowIndicator carousel_point;
//    CarouselAdapter cAdapter;
    RecommendEntry entry;
    @Bind(R.id.pView)
    FrameLayout pView;
    @Bind(R.id.name_tv)
    TextView nameTv;//产品名称
    @Bind(R.id.desc_tv)
    TextView descTv;//产品描述
    @Bind(R.id.price_now_tv)
    TextView priceNowTv;//产品现价
    @Bind(R.id.price_origin_tv)
    TextView priceOriginTv;//产品原价
    @Bind(R.id.person_select_tv)
    TextView personSelectTv;//产品已选人数
    @Bind(R.id.standard_tv)
    TextView standardTv;//产品规格
    @Bind(R.id.storage_method_tv)
    TextView storageMethodTv;//产品冷藏方法
    @Bind(R.id.producing_place_tv)
    TextView producingPlaceTv;//产品产地
    @Bind(R.id.content_tv)
    TextView contentTv;//产品详情
    @Bind(R.id.content_img_iv)
    ImageView contentImgIv;//产品图片
    @Bind(R.id.share_iv)
    ImageView shareIv;
//    @Bind(R.id.imgProgress)
//    View imgProgress;

}
