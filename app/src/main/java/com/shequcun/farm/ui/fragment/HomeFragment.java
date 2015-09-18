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
import android.widget.AdapterView;

import com.android.volley.Request;
import com.common.widget.CircleFlowIndicator;
import com.common.widget.ExpandableHeightGridView;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.common.widget.ViewFlow;
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
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.ui.adapter.FarmSpecialtyAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 有菜首页
 * Created by mac on 15/9/6.
 */
public class HomeFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        carousel_img = (ViewFlow) v.findViewById(R.id.carousel_img);
        carousel_point = (CircleFlowIndicator) v.findViewById(R.id.carousel_point);
        pView = (PullToRefreshScrollView) v.findViewById(R.id.pView);
        gv = (ExpandableHeightGridView) v.findViewById(R.id.gv);
        no_combo_iv = v.findViewById(R.id.no_combo_iv);
//        more_combo_ly = v.findViewById(R.id.more_combo_ly);
        has_combo_iv = v.findViewById(R.id.has_combo_iv);
//        more_combo = v.findViewById(R.id.more_combo);
    }

    @Override
    protected void setWidgetLsn() {
//        requestSlideFromServer();
        doRegisterRefreshBrodcast();
        pView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
//        pView.setMode(PullToRefreshBase.Mode.DISABLED);
        pView.setOnRefreshListener(onRefrshLsn);
        gv.setOnItemClickListener(onItemClk);
        no_combo_iv.setOnClickListener(onClick);
        has_combo_iv.setOnClickListener(onClick);
//        more_combo.setOnClickListener(onClick);
        buildGridViewAdapter();
        requestHome(1);
//        requestRecomendDishes();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
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
        if (aList == null || aList.size() <= 0) {
            aList = new ArrayList<SlidesEntry>();
            SlidesEntry s = new SlidesEntry();
            aList.add(s);
        }
        cAdapter = new CarouselAdapter(getActivity(), aList);
        cAdapter.buildOnClick(onClick);
        carousel_img.setAdapter(cAdapter, 0);
        carousel_img.setFlowIndicator(carousel_point);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isLogin()) {
                gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
                return;
            }

            if (v == no_combo_iv || v == has_combo_iv) {
                gotoFragmentByAdd(R.id.mainpage_ly, new ComboFragment(), ComboFragment.class.getName());
                return;
            }

//            else if (v == has_combo_iv) {//进入选菜页
//                gotoFragmentByAdd(buildBundle_(comboEntry), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
//                return;
//            }
            SlidesEntry item = (SlidesEntry) v.getTag();
            if (item == null)
                return;
            if (TextUtils.isEmpty(item.url)) {
                LinkEntry link = item.link;
                if (link == null || link.type == 0)
                    return;
                if (link.type == 1) {//1.套餐详情,
                    requestComboDetail(link.id);
                } else if (link.type == 2) {//2.菜品详情
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
                ToastHelper.showShort(getActivity(), "请求失败.错误码" + sCode);
                buildCarouselAdapter(null);
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


    AdapterView.OnItemClickListener onItemClk = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapter == null)
                return;
            RecommendEntry entry = adapter.getItem(i);
            if (entry == null)
                return;
            if (entry.type == 2 && entry.bought) {
                ToastHelper.showShort(getActivity(), R.string.spike_error_tip);
                return;
            }
            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new FarmSpecialtyDetailFragment(), FarmSpecialtyDetailFragment.class.getName());
//            if (isLogin()) {
//            }
//            else {
//                gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
//            }
        }
    };


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


    void requestComboDetail(int id) {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {

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
        return new CacheManager(getActivity()).getUserLoginFromDisk() != null;
    }

    /**
     * 轮播的图片
     */
    ViewFlow carousel_img;
    CircleFlowIndicator carousel_point;
    CarouselAdapter cAdapter;
    boolean mIsBind = false;
    PullToRefreshScrollView pView;
    ExpandableHeightGridView gv;
    private FarmSpecialtyAdapter adapter;
    View no_combo_iv;
    //    View more_combo_ly;
    View has_combo_iv;
    //    View more_combo;//更多套餐
    ComboEntry comboEntry;
//    View has_combo_iv;
}
