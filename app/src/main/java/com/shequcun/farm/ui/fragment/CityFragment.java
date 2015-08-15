package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.RegionsAdapter;
import com.shequcun.farm.data.RegionEntry;
import com.shequcun.farm.data.RegionListEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.List;

/**
 * city choose
 * Created by apple on 15/8/5.
 */
public class CityFragment extends BaseFragment {
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
        mLv = (ListView) v.findViewById(R.id.mLv);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.choose_city);
        back = v.findViewById(R.id.back);
        v.findViewById(R.id.common_small_tv).setVisibility(View.GONE);
        pBr = (ProgressBar) v.findViewById(R.id.progress_bar);
    }

    @Override
    protected void setWidgetLsn() {
        mLv.setOnItemClickListener(onItemClick);
        back.setOnClickListener(onClick);
        requestCityList();
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;
            RegionEntry rEntry = adapter.getItem(position);
            if (rEntry == null)
                return;
            if (rEntry.name.equals("北京")) {
                gotoFragmentByAdd(buildBundle(rEntry.id), R.id.mainpage_ly, new SearchFragment(), SearchFragment.class.getName());
            } else {
                ToastHelper.showShort(getActivity(), R.string.sorry);
            }
        }
    };

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
        }
    };

    void requestCityList() {
        buildAdapter();
        RequestParams params = new RequestParams();
        params.add("type", "2");
        params.add("group", "1");
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "util/region", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (pBr != null) {
                    pBr.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pBr != null) {
                    pBr.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    RegionListEntry rEntry = JsonUtilsParser.fromJson(new String(data), RegionListEntry.class);
                    if (rEntry != null) {
                        if (TextUtils.isEmpty(rEntry.errmsg)) {
                            addDataToAdapter(rEntry.mList);
                        } else {
                            ToastHelper.showShort(getActivity(), rEntry.errmsg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                }
            }
        });
    }

    Bundle buildBundle(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("CityId", id);
        return bundle;
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new RegionsAdapter(getActivity());
        mLv.setAdapter(adapter);
    }

    void addDataToAdapter(List<RegionEntry> rEntry) {
        adapter.addAll(rEntry);
        adapter.notifyDataSetChanged();
    }

    RegionsAdapter adapter;
    ProgressBar pBr;
    View back;
    ListView mLv;
}
