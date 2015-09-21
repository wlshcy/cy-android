package com.shequcun.farm.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import org.apache.http.Header;

import java.util.List;

/**
 * 附近的小区
 * Created by apple on 15/8/5.
 */
public class NearbyCommunityFragment extends BaseFragment implements AMapLocationListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nearby_community_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        startLocation();
        mLv = (ListView) v.findViewById(R.id.mLv);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.community_name);
        back = v.findViewById(R.id.back);
        search_community = (TextView) v.findViewById(R.id.title_right_text);
        search_community.setText(R.string.search);
        pBar = (ProgressBar) v.findViewById(R.id.progress_bar);
    }

    @Override
    protected void setWidgetLsn() {
        mLv.setOnItemClickListener(onItemClick);
        back.setOnClickListener(onClick);
        search_community.setOnClickListener(onClick);
        buildAdapter();
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

    private void stopLocation() {
        if (mLocMgrProxy != null) {
            mLocMgrProxy.removeUpdates(this);
            mLocMgrProxy.destroy();
        }
    }


    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (aDapter == null)
                return;
            ZoneEntry zEntry = aDapter.getItem(position);
            if (zEntry == null)
                return;
            IntentUtil.sendUpdateMyAddressMsg(getActivity(), zEntry);
            //new CacheManager(getActivity()).saveZoneCacheToDisk(JsonUtilsParser.toJson(zEntry).getBytes());
            popBackStack();
        }
    };

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == search_community) {
//                gotoFragmentByAdd(R.id.mainpage_ly, new CityFragment(), CityFragment.class.getName());
                gotoFragmentByAdd(buildBundle(1), R.id.mainpage_ly, new SearchFragment(), SearchFragment.class.getName());
            }
        }
    };

    Bundle buildBundle(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("CityId", id);
        return bundle;
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
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    ZoneListEntry zEntry = JsonUtilsParser.fromJson(new String(
                            data), ZoneListEntry.class);
                    if (zEntry != null) {
                        if (!TextUtils.isEmpty(zEntry.errmsg)) {
                            ToastHelper.showShort(getActivity(), zEntry.errmsg);
                            return;
                        }
                        addDataToAdapter(zEntry.aList);
                        stopLocation();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] data, Throwable error) {
                ToastHelper.showShort(getActivity(), "加载失败...");
            }
        });
    }

    private void addDataToAdapter(List<ZoneEntry> aList) {
        aDapter.addAll(aList);
        aDapter.notifyDataSetChanged();
    }

    void buildAdapter() {
        if (aDapter == null)
            aDapter = new NearbyCommunityAdapter(getActivity());
        mLv.setAdapter(aDapter);
    }

    NearbyCommunityAdapter aDapter;
    ProgressBar pBar;
    View back;
    ListView mLv;
    LocationManagerProxy mLocMgrProxy;
    TextView search_community;
    private int locationCount = 0;
}
