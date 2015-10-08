package com.shequcun.farm.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ZoneEntry;
import com.shequcun.farm.data.ZoneListEntry;
import com.shequcun.farm.ui.adapter.NearbyCommunityAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by apple on 15/8/5.
 */
public class SearchFragment extends BaseFragment implements AMapLocationListener {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_community_ly, container, false);
    }

    @Override
    protected void initWidget(View v) {
        startLocation();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.search_community);
    }

    @Override
    protected void setWidgetLsn() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        keyword_et.addTextChangedListener(wLsn);
        buildAdapter();
    }

    @OnItemClick(R.id.mLv)
    void onItemClick(int pos) {
        if (adapter == null)
            return;
        ZoneEntry zEntry = adapter.getItem(pos);
        if (zEntry == null)
            return;
        IntentUtil.sendUpdateMyAddressMsg(getActivity(), zEntry);
        popBackStack();
    }

    @OnClick(R.id.back)
    void back() {
        Utils.hideVirtualKeyboard(getActivity(), search_community);
        popBackStack();
    }

    @OnClick(R.id.fill_in_address)
    void gotoAddressZoneFragment() {
        gotoFragmentByAdd(R.id.mainpage_ly, new AddressZoneFragment(), AddressZoneFragment.class.getName());
    }


    @OnClick(R.id.search_community)
    void startSearch() {
        final String keyword = keyword_et.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastHelper.showShort(getActivity(), "请输入小区名称...");
            return;
        }
        RequestParams params = new RequestParams();
        params.add("cid", "1");
        params.add("kw", keyword);

        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "zone/search", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    ZoneListEntry zLentry = JsonUtilsParser.fromJson(new String(data), ZoneListEntry.class);
                    if (zLentry != null) {
                        if (TextUtils.isEmpty(zLentry.errmsg)) {
                            addDataToAdapter(zLentry.aList);
                        } else {
                            ToastHelper.showShort(getActivity(), zLentry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] data, Throwable error) {
                if (statusCode == 0)
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
            }
        });
    }


//    void upLoadUserAddressToServer(String address) {
//        RequestParams params = new RequestParams();
//        params.add("address", address);
//        params.add("_xsrf", PersistanceManager.INSTANCE.getCookieValue());
//        HttpRequestUtil.httpPost(LocalParams.INSTANCE.getBaseUrl() + "user/save", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
//                if (data != null && data.length > 0) {
//                    UserLoginEntry uuEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
//                    if (uuEntry != null) {
//                        if (TextUtils.isEmpty(uuEntry.errmsg)) {
//                            saveUserInfoToDisk(uuEntry);
//                        } else {
//                            ToastHelper.showShort(getActivity(), uuEntry.errmsg);
//                        }
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
//                ToastHelper.showShort(getActivity(), "修改失败,错误码" + sCode);
//            }
//        });
//    }
//
//    void saveUserInfoToDisk(UserLoginEntry uuEntry) {
//        if (uuEntry == null)
//            return;
//        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
//        if (data == null || data.length <= 0)
//            return;
//        UserLoginEntry uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
//        uEntry.address = uuEntry.address;
//        new CacheManager(getActivity()).saveUserLoginToDisk(JsonUtilsParser.toJson(uEntry).getBytes());
//    }


    String buildCityId() {
        Bundle bundle = getArguments();
        return bundle != null ? bundle.getInt("CityId") + "" : "";
    }


    void addDataToAdapter(List<ZoneEntry> aList) {
        if (aList == null || aList.size() <= 0) {
            ToastHelper.showShort(getActivity(), "很抱歉,未搜索到" + keyword_et.getText().toString().trim());
            return;
        }
        Utils.hideVirtualKeyboard(getActivity(), keyword_et);
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
    }

    TextWatcher wLsn = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            final String inputStr = keyword_et.getText().toString();
            if (TextUtils.isEmpty(inputStr)) {
                search_community.setVisibility(View.GONE);
                return;
            }
            search_community.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (pBar.getVisibility() == View.VISIBLE)
                pBar.setVisibility(View.GONE);
            startLocation();
        }
    };

    void buildAdapter() {
        if (adapter == null) {
            adapter = new NearbyCommunityAdapter(getActivity());
        }
        mSearchResultLv.setAdapter(adapter);
    }

    private void searchCommunityDependLonLat(double lon, double lat) {
        RequestParams params = new RequestParams();
        params.add("lng", "" + lon);
        params.add("lat", "" + lat);
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl()
                + "zone/v2/list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                if (pBar != null) {
                    pBar.setVisibility(View.VISIBLE);
                }
                super.onStart();
            }

            @Override
            public void onFinish() {
                if (pBar != null) {
                    pBar.setVisibility(View.GONE);
                }
                super.onFinish();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    ZoneListEntry zEntry = JsonUtilsParser.fromJson(new String(
                            data), ZoneListEntry.class);
                    if (zEntry != null) {
                        if (!TextUtils.isEmpty(zEntry.errmsg)) {
                            ToastHelper.showShort(getActivity(), zEntry.errmsg);
                            return;
                        }
                        addDataToAdapter1(zEntry.aList);
                        stopLocation();
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                ToastHelper.showShort(getActivity(), "加载失败...");
            }
        });
    }

    private void addDataToAdapter1(List<ZoneEntry> aList) {
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
    }


    private void stopLocation() {
        if (mLocMgrProxy != null) {
            mLocMgrProxy.removeUpdates(this);
            mLocMgrProxy.destroy();
        }
    }

    @Override
    public void onLocationChanged(final AMapLocation location) {
        if (location != null) {
            if (locationCount++ <= 0) {
                return;
            }

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    searchCommunityDependLonLat(location.getLongitude(), location.getLatitude());
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }


    private void startLocation() {
        // 初始化定位，只采用网络定位
        if (mLocMgrProxy == null) {
            mLocMgrProxy = LocationManagerProxy.getInstance(getActivity());
            mLocMgrProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 1000, 0, this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocation();
        mLocMgrProxy = null;
    }

    LocationManagerProxy mLocMgrProxy;
    @Bind(R.id.progress_bar)
    ProgressBar pBar;
    NearbyCommunityAdapter adapter;
    @Bind(R.id.mLv)
    ListView mSearchResultLv;
    @Bind(R.id.search_community)
    View search_community;
    @Bind(R.id.keyword_et)
    EditText keyword_et;
    private int locationCount = 0;
}
