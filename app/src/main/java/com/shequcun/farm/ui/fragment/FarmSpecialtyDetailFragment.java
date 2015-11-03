package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.OtherInfo;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.db.RecommendItemKey;
import com.shequcun.farm.platform.ShareContent;
import com.shequcun.farm.platform.ShareManager;
import com.shequcun.farm.ui.SqcFarmActivity;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 农庄特产详情
 * Created by mac on 15/9/6.
 */
public class FarmSpecialtyDetailFragment extends BaseFragment {
    @Bind(R.id.slider)
    SliderLayout slider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.farm_specialty_detail_ly, container, false);
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
                String url = entry.detail.image + "?imageView2/2/w/" + DeviceInfo.getDeviceWidth(this.getBaseAct());
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
        return new CacheManager(getBaseAct()).getRecommendEntry(rItemKey);
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
//        clearStack();
        popBackStack();
//        startAnimation();
    }

    void buildCarouselAdapter() {
        if (entry == null || entry.imgs == null || entry.imgs.length < 1) return;
        if (entry.imgs.length == 1) {
            addSliderUrl(entry.imgs[0]);
            slider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
            slider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float position) {
                    //空是为了防止loop
                }
            });
        } else {
            for (String url : entry.imgs) {
                addSliderUrl(url);
            }
            slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.setCustomAnimation(new DescriptionAnimation());
            slider.setDuration(4000);
        }

    }

    private void addSliderUrl(String url) {
        DefaultSliderView textSliderView = new DefaultSliderView(getBaseAct());
        // initialize a SliderLayout
        url = url + "?imageView2/2/" + DeviceInfo.getDeviceWidth(getBaseAct());
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
                sharecontent.setUrlImage(entry.imgs[0]);
//                sharecontent.setImageId(R.drawable.icon_share_logo);
                sharecontent.setTargetUrl(Constrants.URL_SHARE + entry.id);
//                sharecontent.setTitle("有菜，不能说的秘密！");
                sharecontent.setTitle(entry.title);
//                sharecontent.setContent("孩子的餐桌我们的标准，走心，连蔬菜都这么有bigger！");
                sharecontent.setContent(entry.descr);
                ShareManager.shareByFrame(getBaseAct(), sharecontent);
            } else if (v == producingPlaceTv) {
                gotoProducingPlaceFragment(entry.fid);
            }
        }
    };

    private void gotoHomePageDependPos(int pos) {
        popBackStack();
//        if (entry != null && entry.count > 0) {
//            RecommendItemKey itemKey = new RecommendItemKey();
//            itemKey.object = entry;
//            new CacheManager(getBaseAct()).saveRecommendToDisk(itemKey);
//            IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
//        }
        SqcFarmActivity mAct = (SqcFarmActivity) getBaseAct();
        mAct.buildRadioButtonStatus(pos);
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
            final View childView = LayoutInflater.from(getBaseAct()).inflate(R.layout.pay_widget_ly, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            pView.addView(childView, params);
            ((TextView) childView.findViewById(R.id.shop_cart_total_price_tv)).setText("共付:" + Utils.unitPeneyToYuan(entry.price / 100 >= 99 ? entry.price : entry.price + 10 * 10 * 10));
            childView.findViewById(R.id.shop_cart_surpport_now_pay_tv).setVisibility(View.GONE);
            childView.findViewById(R.id.buy_order_tv).setOnClickListener(new View.OnClickListener() {//支付
                @Override
                public void onClick(View view) {

                    if (!isLogin()) {
                        FragmentUtils.login(FarmSpecialtyDetailFragment.this);
                        return;
                    }

                    if (entry.type == 2 && entry.bought) {
                        new com.shequcun.farm.dlg.AlertDialog().alertDialog(getBaseAct(), R.string.spike_error_tip, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gotoHomePageDependPos(2);
                                Bundle bundle = new Bundle();

                                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new MyOrderViewPagerFragment(), MyOrderViewPagerFragment.class.getName());
                            }
                        });
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
            View childView = LayoutInflater.from(getBaseAct()).inflate(R.layout.shop_cart_widget_ly, null);
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
                    if (entry.count >= entry.maxpacks) {
                        alertOutOfMaxpacks(entry.count);
                        return;
                    }

                    if (entry.count >= entry.remains) {
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
                        new CacheManager(getBaseAct()).saveRecommendToDisk(itemKey);
                        IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
                        ToastHelper.showShort(getBaseAct(), R.string.add_shop_cart_success);
                    }
                }
            });

            go_to_shop_cart_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoHomePageDependPos(1);
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

    private void alertOutOfMaxpacks(int maxPacks) {

        alertDialog("亲,该商品您最多只能选择" + maxPacks + "份哟!");
    }

    private void alertOutOfRemains() {
        String content = "亲,该菜品库存不足,请选其他菜品吧";
        alertDialog(content);
    }


    private void alertDialog(String content) {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
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
        return new CacheManager(getBaseAct()).getUserLoginEntry() != null;
    }

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
