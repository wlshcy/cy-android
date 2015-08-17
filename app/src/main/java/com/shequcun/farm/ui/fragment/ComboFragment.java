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
import android.widget.ListView;

import com.common.widget.CircleFlowIndicator;
import com.common.widget.ViewFlow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.ComboListEntry;
import com.shequcun.farm.data.ComboParam;
import com.shequcun.farm.data.SlidesEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.CarouselAdapter;
import com.shequcun.farm.ui.adapter.ComboAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
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
 * 套餐页面
 * Created by apple on 15/8/3.
 */
public class ComboFragment extends BaseFragment {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_ly, container, false);
    }

    @Override
    protected void initWidget(View v) {
        mListView = (ListView) v.findViewById(R.id.mListView);
        carousel_img = (ViewFlow) v.findViewById(R.id.carousel_img);
        carousel_point = (CircleFlowIndicator) v.findViewById(R.id.carousel_point);
    }

    @Override
    protected void setWidgetLsn() {
        mListView.setOnItemClickListener(onItemClick);
        buildAdapter();
        buildCarouselAdapter();
        requestComboList();
        doRegisterRefreshBrodcast();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            SlidesEntry item = (SlidesEntry) v.getTag();
            if (item == null)
                return;
            if (TextUtils.isEmpty(item.url))
                return;
            gotoFragmentByAdd(buildBundle(item.url), R.id.mainpage_ly, new AdFragment(), AdFragment.class.getName());
        }
    };

    void buildCarouselAdapter() {
        if (cAdapter == null) {
            List<SlidesEntry> list = new ArrayList<SlidesEntry>();
//            for (int i = 0; i < 4; i++) {
            SlidesEntry s = new SlidesEntry();
            list.add(s);
//            }
            cAdapter = new CarouselAdapter(getActivity(), list);
        }
        cAdapter.buildOnClick(onClick);
        carousel_img.setAdapter(cAdapter, 0);
        carousel_img.setFlowIndicator(carousel_point);
    }

    Bundle buildBundle(final String adUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("AdUrl", adUrl);
        return bundle;
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new ComboAdapter(getActivity());
        mListView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;

            ComboEntry entry = adapter.getItem(position);
            if (entry == null)
                return;
            if (isLogin()) {
                if (buildIsMyComboClick(position)) {
                    gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
                } else
                    gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ComboSecondFragment(), ComboSecondFragment.class.getName());
            } else {
                gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
            }

<<<<<<< HEAD
            Bundle bundle = new Bundle();
            if (buildIsMyComboClick(position)) {
                bundle.putInt("id",entry.id);
//                bundle.putInt("weight",entry.weights[]);
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            } else {
                bundle.putSerializable("comboParams",parseEntryForParams(entry));
                gotoFragmentByAdd(bundle, R.id.mainpage_ly, new ComboSecondFragment(), ComboSecondFragment.class.getName());
            }
=======
>>>>>>> master
        }
    };

    ArrayList<ComboParam> parseEntryForParams(ComboEntry entry) {
        ArrayList<ComboParam> params = new ArrayList<>();
        for (int i = 0; i < entry.mprices.length; i++) {
            ComboParam p = new ComboParam();
            p.setId(entry.id);
            p.setDuration(entry.duration);
            p.setShipday(entry.shipday);
            p.setTitle(entry.title);
            p.setTotalPrices(entry.prices[i]);
            p.setWimg(entry.wimgs[i]);
            p.setWeights(entry.weights[i]);
            p.setComboIdx(i);
            params.add(p);
        }
        return params;
    }


    Bundle buildBundle(ComboEntry entry) {
        Bundle bundle = new Bundle();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }

    /**
     * 请求套餐列表
     */
    void requestComboList() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.INSTANCE.getBaseUrl() + "cai/combo", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (pDlg != null)
                    pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pDlg != null)
                    pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    ComboListEntry entry = JsonUtilsParser.fromJson(new String(data), ComboListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            doAddMyComboDataToAdapter(entry.myCombos);
                            doAddDataToAdapter(entry.aList);
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
                ToastHelper.showShort(getActivity(), "错误码 " + sCode);
            }
        });
    }


    void doAddMyComboDataToAdapter(List<ComboEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.setIsMyCombo(true);
            Utils.setListViewHeightBasedOnChildren(mListView);
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            IntentUtil.sendUpdateMyInfoMsg(getActivity(), aList.get(0));
        }
    }

    void doAddDataToAdapter(List<ComboEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.setIsMyCombo(false);
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            Utils.setListViewHeightBasedOnChildren(mListView);
        }
    }

    /**
     * 是不是单击的我的套餐项
     *
     * @param position
     * @return boolean true 是
     */
    boolean buildIsMyComboClick(final int position) {
        if (position >= adapter.getCount() || adapter.getItem(position) == null)
            return false;
        View pView = mListView.getChildAt(position + mListView.getHeaderViewsCount()
                - mListView.getFirstVisiblePosition());
        if (pView == null)
            return false;
        return pView.findViewById(R.id.my_combo).getVisibility() == View.VISIBLE;
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
                if (adapter != null)
                    adapter.clear();
                requestComboList();
            }
        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getActivity().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    boolean mIsBind = false;
    ComboAdapter adapter;
    CarouselAdapter cAdapter;
    ListView mListView;
    /**
     * 轮播的图片
     */
    ViewFlow carousel_img;
    CircleFlowIndicator carousel_point;
}
