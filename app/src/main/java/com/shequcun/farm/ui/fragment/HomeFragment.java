package com.shequcun.farm.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboDetailEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.HomeEntry;
import com.shequcun.farm.data.LinkEntry;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.ui.adapter.FarmSpecialtyAdapter;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;



import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

/**
 * 有菜首页
 * Created by mac on 15/9/6.
 */
public class HomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener {
    @Bind(R.id.slider)
    SliderLayout slider;
    @Bind(R.id.custom_indicator2)
    PagerIndicator customIndicator2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    protected void initWidget(View v) {
    }

    @Override
    protected void setWidgetLsn() {
        doRegisterRefreshBrodcast();
//        pView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pView.setMode(PullToRefreshBase.Mode.BOTH);
        pView.setOnRefreshListener(onRefrshLsn);
        buildGridViewAdapter();
        requestHome(1);
    }

    @OnClick({R.id.no_combo_iv, R.id.has_combo_iv})
    void doClick() {
        if (!isLogin()) {
            gotoFragmentByAnimation(null, R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName(), R.anim.scale_left_top_in, R.anim.scale_left_top_out);
            return;
        }
        gotoFragmentByAnimation(null, R.id.mainpage_ly, new ComboFragment(), ComboFragment.class.getName(), R.anim.scale_left_top_in, R.anim.scale_left_top_out);
    }

    @OnItemClick(R.id.gv)
    void onItemClick(int position) {
        if (adapter == null)
            return;
        RecommendEntry entry = adapter.getItem(position);
        if (entry == null)
            return;
        if (entry.type == 2 && entry.bought) {
            ToastHelper.showShort(getActivity(), R.string.spike_error_tip);
            return;
        }
        gotoFragmentByAnimation(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName(), R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
//        gotoFragmentByAnimation(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName(),R.anim.rotate_in,R.anim.slide_out_to_bottom);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
        ButterKnife.unbind(this);
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_COMBO_PAGE);
            getActivity().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(IntentUtil.UPDATE_COMBO_PAGE)) {
                requestHome(2);
            }
        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    void buildCarouselAdapter(List<SlidesEntry> aList) {
//        if (aList == null || aList.size() <= 0) {
//            aList = new ArrayList<SlidesEntry>();
//            SlidesEntry s = new SlidesEntry();
//            aList.add(s);
//            dismissImgProgress();
//        }
//        cAdapter = new CarouselAdapter(getActivity(), aList);
//        cAdapter.buildOnClick(onClick);
//        carousel_img.setAdapter(cAdapter, 0);
//        carousel_img.setFlowIndicator(carousel_point);
//        carousel_img.setOnViewSwitchListener(viewSwitchListener);
//        cAdapter.setImageLoaderListener(imageLoaderListener);
//        cAdapter.setWidth(DeviceInfo.getDeviceWidth(getActivity()));
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
        DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
        // initialize a SliderLayout
        String url = entry.img + "?imageView2/2/" + DeviceInfo.getDeviceWidth(getActivity());
        textSliderView
                .description("")
                .image(url)
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(this)
                .setParamObj(entry);
        slider.addSlider(textSliderView);
    }

    private void addSliderUrl(int resId) {
        DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
        // initialize a SliderLayout
        textSliderView
                .description("")
                .image(resId)
                .setScaleType(BaseSliderView.ScaleType.Fit);
        slider.addSlider(textSliderView);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (slider.getParamObj() == null) return;
        SlidesEntry entry = (SlidesEntry) slider.getParamObj();
        gotoAdFragment(entry);
    }

    private void gotoAdFragment(SlidesEntry item) {
        if (TextUtils.isEmpty(item.url)) {
            LinkEntry link = item.link;
            if (link == null || link.type == 0)
                return;
            if (link.type == 1) {//1.套餐详情,
                requestComboDetail(link.id);
            } else if (link.type == 2) {//2.菜品详情
                requestSingleDishDetail(link.id);
            }
            return;
        }

        gotoFragmentByAdd(buildBundle(item.url), R.id.mainpage_ly, new AdFragment(), AdFragment.class.getName());
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isLogin()) {
                gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
                return;
            }
            SlidesEntry item = null;
            if (v.getTag() instanceof SlidesEntry) {
                item = (SlidesEntry) v.getTag();
            }
            if (item == null)
                return;
            if (TextUtils.isEmpty(item.url)) {
                LinkEntry link = item.link;
                if (link == null || link.type == 0)
                    return;
                if (link.type == 1) {//1.套餐详情,
                    requestComboDetail(link.id);
                } else if (link.type == 2) {//2.菜品详情
                    requestSingleDishDetail(link.id);
                }
                return;
            }

            gotoFragmentByAdd(buildBundle(item.url), R.id.mainpage_ly, new AdFragment(), AdFragment.class.getName());
        }
    };

    Bundle buildBundle(final String adUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("AdUrl", adUrl);
        return bundle;
    }


    PullToRefreshScrollView.OnRefreshListener2 onRefrshLsn = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            requestHome(2);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            requestRecomendDishes();
//            requestHome(1);
        }
    };

    void requestHome(final int mode) {
        RequestParams params = new RequestParams();
        params.add("mode", mode + "");
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/home", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    String result = new String(data);
                    HomeEntry hEntry = JsonUtilsParser.fromJson(result, HomeEntry.class);
                    if (hEntry != null) {
                        if (TextUtils.isEmpty(hEntry.errmsg)) {
                            if (mode != 2) {
                                buildCarouselAdapter(hEntry.sList);
                                addDataToAdapter(hEntry.items);
                            }
                            updateMyComboStatus(hEntry.has_combo);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), hEntry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                buildCarouselAdapter(null);
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "请求失败.错误码" + sCode);
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
    void requestRecomendDishes() {
        RequestParams params = new RequestParams();
        params.add("length", 15 + "");
        if (adapter != null && adapter.getCount() >= 1) {
            params.add("lastid", adapter.getItem(adapter.getCount() - 1).id + "");
        }
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/itemlist", new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                if (pView != null)
                    pView.onRefreshComplete();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
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
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "哇,刷新失败了.请稍后重试.");
            }
        });
    }

    void buildGridViewAdapter() {
        if (adapter == null)
            adapter = new FarmSpecialtyAdapter(getActivity());
        gv.setAdapter(adapter);
        gv.setExpanded(true);
    }

    void addDataToAdapter(List<RecommendEntry> aList) {
        if (adapter != null) {
            adapter.clear();
        }
        if (aList != null && aList.size() > 0) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
    }

    Bundle buildBundle(RecommendEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecommentEntry", entry);
        return bundle;
    }


    void updateMyComboStatus(boolean isShow) {
        if (isShow) {
            no_combo_iv.setVisibility(View.GONE);
            has_combo_iv.setVisibility(View.VISIBLE);
        } else {
            comboEntry = null;
            no_combo_iv.setVisibility(View.VISIBLE);
            has_combo_iv.setVisibility(View.GONE);
        }
    }


    void requestSingleDishDetail(int id) {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", "" + id);

        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/itemdtl", params, new AsyncHttpResponseHandler() {

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
                    RecommendEntry entry = JsonUtilsParser.fromJson(new String(data), RecommendEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName());
                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {

            }
        });
    }


    void requestComboDetail(int id) {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {

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
                            gotoFragmentByAnimation(buildBundle_(entry.combo), R.id.mainpage_ly, new ComboSecondFragment(), ComboSecondFragment.class.getName(), R.anim.puff_in, R.anim.puff_out);
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

    Bundle buildBundle_(ComboEntry entry) {
        Bundle bundle = new Bundle();
        entry.setPosition(entry.index);
        bundle.putSerializable("ComboEntry", entry);
        return bundle;
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
    @Bind(R.id.pView)
    PullToRefreshScrollView pView;
    @Bind(R.id.gv)
    ExpandableHeightGridView gv;
    @Bind(R.id.no_combo_iv)
    View no_combo_iv;
    @Bind(R.id.has_combo_iv)
    View has_combo_iv;
//    @Bind(R.id.imgProgress)
//    View imgProgress;

    ComboEntry comboEntry;
    //    CarouselAdapter cAdapter;
    boolean mIsBind = false;
    private FarmSpecialtyAdapter adapter;
}
