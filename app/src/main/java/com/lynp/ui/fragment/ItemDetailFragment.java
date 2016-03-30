package com.lynp.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.shequcun.farm.R;
import com.lynp.R;
import com.lynp.ui.data.ItemDetailEntry;

import com.lynp.ui.db.DataBase;
import com.lynp.ui.util.IntentUtil;

import com.lynp.ui.MainActivity;
import com.lynp.ui.util.HttpRequestUtil;
import com.lynp.ui.util.LocalParams;
import com.lynp.ui.util.ToastHelper;
import com.lynp.ui.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 商品详情
 * Created by nmg on 16/1/29.
 */
public class ItemDetailFragment extends BaseFragment {
    @Bind(R.id.slider)
    SliderLayout slider;

    ItemDetailEntry entry;
    @Bind(R.id.DetailView)
    FrameLayout DetailView;

    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.desc)
    TextView desc;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.mprice)
    TextView mprice;
    @Bind(R.id.size)
    TextView size;
    @Bind(R.id.origin)
    TextView origin;
//    @Bind(R.id.share_iv)
//    ImageView shareIv;

    @Bind(R.id.goods_sub)
    ImageView goods_sub;
    @Bind(R.id.goods_count)
    TextView goods_count;
    @Bind(R.id.goods_add)
    ImageView goods_add;
    @Bind(R.id.shop_cart)
    TextView shop_cart;

    public Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_detail_ui, container, false);
    }

    private void setDataToView() {
        if (entry != null) {
            name.setText(entry.name);
            desc.setText(entry.desc);
            price.setText(Utils.unitPeneyToYuan(entry.price));
            mprice.setText(Utils.unitPeneyToYuan(entry.mprice));
            size.setText(Utils.unitConversion(entry.size) + "/份");
            origin.setText(entry.origin);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
//        entry = buildItemEntry();
        buildItemEntry();
        /*删除线*/
        mprice.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    protected void setWidgetLsn() {
//        producingPlaceTv.setOnClickListener(onClick);
//        shareIv.setOnClickListener(onClick);
//        buildCarouselAdapter();
//        setDataToView();
//        addChildViewToParent();
    }

    @OnClick(R.id.close)
    void back() {
        popBackStack();
    }

    void buildCarouselAdapter() {
        if (entry == null || entry.photo == null) return;
        addSliderUrl(entry.photo);
        slider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
            slider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float position) {
                    //空是为了防止loop
                }
            });
//        } else {
//            for (String url : entry.imgs) {
//                addSliderUrl(url);
//            }
//            slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//            slider.setCustomAnimation(new DescriptionAnimation());
//            slider.setDuration(4000);
//        }

    }

    private void addSliderUrl(String url) {
        DefaultSliderView textSliderView = new DefaultSliderView(getBaseAct());
        // initialize a SliderLayout
//        url = url + "?imageView2/2/" + DeviceInfo.getDeviceWidth(getBaseAct());
        textSliderView
                .description("")
                .image(url)
                .setScaleType(BaseSliderView.ScaleType.CenterCrop);
        slider.addSlider(textSliderView);
    }

    @Override
    public void onStop() {
        slider.stopAutoCycle();
        super.onStop();
    }

//    View.OnClickListener onClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v == shareIv) {
//                ShareContent sharecontent = new ShareContent();
//                sharecontent.setUrlImage(entry.photo);
////                sharecontent.setImageId(R.drawable.icon_share_logo);
//                sharecontent.setTargetUrl(Constrants.URL_SHARE + entry.id);
////                sharecontent.setTitle("有菜，不能说的秘密！");
//                sharecontent.setTitle(entry.name);
////                sharecontent.setContent("孩子的餐桌我们的标准，走心，连蔬菜都这么有bigger！");
////                sharecontent.setContent(entry.desc);
//                ShareManager.shareByFrame(getBaseAct(), sharecontent);
//            } else if (v == producingPlaceTv) {
////                gotoProducingPlaceFragment(entry.id);
//            }
//        }
//    };

    private void gotoHomePageDependPos(int pos) {
        popBackStack();
//        if (entry != null && entry.count > 0) {
//            RecommendItemKey itemKey = new RecommendItemKey();
//            itemKey.object = entry;
//            new CacheManager(getBaseAct()).saveRecommendToDisk(itemKey);
//            IntentUtil.sendUpdateFarmShoppingCartMsg(getBaseAct());
//        }
        MainActivity mAct = (MainActivity) getBaseAct();
        mAct.buildRadioButtonStatus(pos);
    }

    void buildItemEntry() {
        Bundle bundle = getArguments();
//        return bundle != null ? (ItemEntry) bundle.getSerializable("RecommentEntry") : null;
        String id = bundle.getString("id");
        getItemDetail(id);
    }

    void bindButtonClicked(){

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
                    if (entry.count <= 0)
                        return;
                    entry.count--;
                    if (entry.count < 0) entry.count = 0;
                    goods_count.setText(entry.count + "");
                }
        });

        shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry.count > 0) {
                    DataBase dataBase = new DataBase(getActivity());
                    if (dataBase.item_exits(entry.id)){
                        dataBase.deleteItem(entry.id);
                    }
                    dataBase.addItem(entry);
                    IntentUtil.sendUpdateShoppingCartMsg(getBaseAct());
                    ToastHelper.showShort(getBaseAct(), R.string.add_shop_cart_success);
                    }
                }
            });
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

    void getItemDetail(String id) {
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "/v1/vegetables/" + id, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {

                if (data != null && data.length > 0) {
                    String result = new String(data);
//                    Log.i("====", result);
                    Gson gson = new Gson();
                    ItemDetailEntry itemEntry = gson.fromJson(result, ItemDetailEntry.class);
//                    Log.i("======", itemEntry.toString());
                    entry = itemEntry;
                    setDataToView();
                    buildCarouselAdapter();
                    bindButtonClicked();
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//                buildCarouselAdapter(null);
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "请求失败.错误码" + sCode);
            }

//            @Override
//            public void onFinish() {
//                super.onFinish();
//                if (DetailView != null)
//                    DetailView.onRefreshComplete();
//            }
        });
    }

}
