package com.lynp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.widget.ExpandableHeightGridView;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.shequcun.farm.R;
import com.lynp.R;
import com.shequcun.farm.data.ComboDetailEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.LinkEntry;

import com.lynp.ui.data.ItemEntry;

import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.lynp.ui.adapter.ItemAdapter;
import com.shequcun.farm.ui.fragment.BaseFragment;
import com.shequcun.farm.ui.fragment.ComboSecondFragment;
import com.shequcun.farm.ui.fragment.FragmentUtils;
import com.shequcun.farm.util.ClickUtil;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * 原味首页
 * Created by nmg on 16/1/27.
 */
public class HomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener {
    @Nullable
    @Bind(R.id.slider)
    SliderLayout slider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_ui, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    protected void initWidget(View v) {
    }

    @Override
    protected void setWidgetLsn() {
//        doRegisterRefreshBrodcast();
        pView.setMode(PullToRefreshBase.Mode.BOTH);
        pView.setOnRefreshListener(onRefrshLsn);
        buildGridViewAdapter();
//        getSlides();
        getVegs();
    }

    @OnItemClick(R.id.ItemView)
    void onItemClick(int position) {
        if (adapter == null)
            return;
        ItemEntry entry = adapter.getItem(position);

        if (entry == null)
            return;
        gotoFragmentByAdd(buildBundle(entry.id), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        doUnRegisterReceiver();
    }

//    void doRegisterRefreshBrodcast() {
//        if (!mIsBind) {
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(IntentUtil.UPDATE_COMBO_PAGE);
//            getBaseAct().registerReceiver(mUpdateReceiver, intentFilter);
//            mIsBind = true;
//        }
//    }

//    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (TextUtils.isEmpty(action)) {
//                return;
//            }
//            if (action.equals(IntentUtil.UPDATE_COMBO_PAGE)) {
//                getVegs();
//            }
//        }
//    };

//    private void doUnRegisterReceiver() {
//        if (mIsBind) {
//            getBaseAct().unregisterReceiver(mUpdateReceiver);
//            mIsBind = false;
//        }
//    }

    void buildCarouselAdapter(List<SlidesEntry> aList) {
        slider.removeAllSliders();
        if (aList == null || aList.isEmpty()) {
            addSliderUrl(R.drawable.icon_combo_default);
            slider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
            slider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float position) {
                    //空是为了防止loop
                }
            });
        } else if (aList.size() == 1) {
            addSliderUrl(aList.get(0));
            slider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
            slider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float position) {
                    //空是为了防止loop
                }
            });
        } else {
            for (SlidesEntry se : aList) {
                addSliderUrl(se);
            }
            slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.setCustomAnimation(new DescriptionAnimation());
            slider.setDuration(4000);
        }
    }

    private void addSliderUrl(SlidesEntry entry) {
        DefaultSliderView textSliderView = new DefaultSliderView(getBaseAct());
        // initialize a SliderLayout
        String url = entry.img + "?imageView2/2/" + DeviceInfo.getDeviceWidth(getBaseAct());
        textSliderView
                .description("")
                .image(url)
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(this)
                .setParamObj(entry);
        slider.addSlider(textSliderView);
    }

    private void addSliderUrl(int resId) {
        DefaultSliderView textSliderView = new DefaultSliderView(getBaseAct());
        // initialize a SliderLayout
        textSliderView
                .description("")
                .image(resId)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        slider.addSlider(textSliderView);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        if (slider.getParamObj() == null) return;
        SlidesEntry entry = (SlidesEntry) slider.getParamObj();
        gotoAdFragment(entry);
//        UmengCountEvent.onClickHomeBanner(getActivity());
    }

    private void gotoAdFragment(SlidesEntry item) {
        if (TextUtils.isEmpty(item.url)) {
            if (!isLogin()) {
                FragmentUtils.login(this);
                return;
            }
            LinkEntry link = item.link;
            if (link == null || link.type == 0)
                return;
            if (link.type == 1) {//1.套餐详情,
                requestComboDetail(link.id);
            } else if (link.type == 2) {//2.菜品详情
//                requestSingleDishDetail(link.id);
            }
            return;
        }

//        gotoFragmentByAdd(buildBundle(item.url), R.id.mainpage_ly, new AdFragment(), AdFragment.class.getName());
    }


//    Bundle buildBundle(final String adUrl) {
//        Bundle bundle = new Bundle();
//        bundle.putString("AdUrl", adUrl);
//        return bundle;
//    }

    Bundle buildBundle(final String id){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        return bundle;
    }

    PullToRefreshScrollView.OnRefreshListener2 onRefrshLsn = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            getVegs();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//            requestRecomendDishes();
        }
    };

    void getVegs() {
        RequestParams params = new RequestParams();
        params.add("length", length + "");
//        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/home", params, new AsyncHttpResponseHandler() {
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "/v1/vegetables", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    String result = new String(data);
                    Gson gson = new Gson();
                    List<ItemEntry> vegEntry = gson.fromJson(result, new TypeToken<List<ItemEntry>>(){}.getType());

                    if(vegEntry != null){
                        addDataToAdapter(vegEntry);
                    }

//                    if (hEntry != null) {
//                        if (TextUtils.isEmpty(hEntry.errmsg)) {
//                            buildCarouselAdapter(hEntry.sList);
//                            addDataToAdapter(hEntry.items);
//                            updateMyComboStatus(hEntry.has_combo);
//
//                            addDataToAdapter(result);
//                            return;
//                        }
//                        ToastHelper.showShort(getBaseAct(), hEntry.errmsg);
//                    }
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

            @Override
            public void onFinish() {
                super.onFinish();
                if (pView != null)
                    pView.onRefreshComplete();
            }
        });
    }


    /**
     * 请求特产
     */
//    void requestRecomendDishes() {
//        RequestParams params = new RequestParams();
//        params.add("length", length2 + "");
//        if (adapter != null && adapter.getCount() >= 1) {
//            params.add("start", adapter.getCount() + "");
//        } else {
//            params.add("start", "0");
//        }
//        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "/v1/vegetables", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                if (pView != null)
//                    pView.onRefreshComplete();
//            }
//
//            @Override
//            public void onSuccess(int sCode, Header[] h, byte[] data) {
//                if (data != null && data.length > 0) {
//                    ItemEntry entry = JsonUtilsParser.fromJson(new String(data), RecommentListEntry.class);
//                    if (entry != null) {
//                        if (TextUtils.isEmpty(entry.errmsg)) {
//                            if (entry.aList == null || entry.aList.size() <= 0) {
//                                ToastHelper.showShort(getBaseAct(), R.string.no_more_goods);
//                                return;
//                            }
//                            addDataToAdapter2(entry.aList);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//                if (sCode == 0) {
//                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
//                    return;
//                }
//                ToastHelper.showShort(getBaseAct(), "哇,刷新失败了.请稍后重试.");
//            }
//        });
//    }

    void buildGridViewAdapter() {
        if (adapter == null)
            adapter = new ItemAdapter(getBaseAct());
        gv.setAdapter(adapter);
        gv.setExpanded(true);
    }

    void addDataToAdapter(List<ItemEntry> aList) {
        if (adapter != null) {
            adapter.clear();
        }
        if (aList != null && aList.size() > 0) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
        if (aList.size() % length > 0) {
            pView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }else {
            pView.setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    void addDataToAdapter2(List<ItemEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
        if (aList.size() % length2 > 0) {
            pView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

//    Bundle buildBundle(ItemEntry entry) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("RecommentEntry", entry);
//        return bundle;
//    }




//    void updateMyComboStatus(boolean isShow) {
//        if (isShow) {
//            no_combo_iv.setVisibility(View.GONE);
//            has_combo_iv.setVisibility(View.VISIBLE);
//        } else {
//            comboEntry = null;
//            no_combo_iv.setVisibility(View.VISIBLE);
//            has_combo_iv.setVisibility(View.GONE);
//        }
//        UserLoginEntry entry = new CacheManager(getBaseAct()).getUserLoginEntry();
//        if (entry != null) {
//            entry.isMyCombo = isShow;
//            new CacheManager(getBaseAct()).saveUserLoginToDisk(JsonUtilsParser.toJson(entry).getBytes());
//        }
//    }


//    void requestSingleDishDetail(int id) {
//        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
//        RequestParams params = new RequestParams();
//        params.add("id", "" + id);
//
//        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/itemdtl", params, new AsyncHttpResponseHandler() {
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
//                    ItemEntry entry = JsonUtilsParser.fromJson(new String(data), RecommendEntry.class);
//                    if (entry != null) {
////                        if (TextUtils.isEmpty(entry.errmsg)) {
//////                            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailViewPagerFragment(), FarmSpecialtyDetailViewPagerFragment.class.getName());
////                            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailViewPagerFragment.class.getName());
////                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
//
//            }
//        });
//    }


    void requestComboDetail(int id) {
        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {

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
                    ComboDetailEntry entry = JsonUtilsParser.fromJson(new String(data), ComboDetailEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            gotoFragmentByAdd(buildBundle_(entry.combo), R.id.mainpage_ly, new ComboSecondFragment(), ComboSecondFragment.class.getName());
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "请求失败,错误码" + sCode);
            }
        });
    }

    Bundle buildBundle_(ComboEntry entry) {
        Bundle bundle = new Bundle();
        entry.setPosition(entry.index);
        entry.setMine(false);
        bundle.putSerializable("ComboEntry", entry);
        return bundle;
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getBaseAct()).getUserLoginEntry() != null;
    }

    @Bind(R.id.ItemBoard)
    PullToRefreshScrollView pView;
    @Bind(R.id.ItemView)
    ExpandableHeightGridView gv;
//    @Nullable
//    @Bind(R.id.no_combo_iv)
//    View no_combo_iv;
//    @Nullable
//    @Bind(R.id.has_combo_iv)
//    View has_combo_iv;
//    ComboEntry comboEntry;
//    boolean mIsBind = false;
    private ItemAdapter adapter;
    private int length = 10;
    private int length2 = 15;
}
