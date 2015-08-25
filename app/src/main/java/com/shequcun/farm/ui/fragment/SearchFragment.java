package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * Created by apple on 15/8/5.
 */
public class SearchFragment extends BaseFragment {
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
        keyword_et = (EditText) v.findViewById(R.id.keyword_et);
        back = v.findViewById(R.id.back);
        mSearchResultLv = (ListView) v.findViewById(R.id.mLv);
        search_community = v.findViewById(R.id.search_community);
        fill_in_address = v.findViewById(R.id.fill_in_address);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.search_community);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        fill_in_address.setOnClickListener(onClick);
        search_community.setOnClickListener(onClick);
        mSearchResultLv.setOnItemClickListener(onItemClick);
        keyword_et.addTextChangedListener(wLsn);
        buildAdapter();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (search_community == v)
                searchCommunity();
            else if (fill_in_address == v) {
                gotoFragmentByAdd(R.id.mainpage_ly, new FillInAddressFragment(), FillInAddressFragment.class.getName());
            }
        }
    };
    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;
            ZoneEntry zEntry = adapter.getItem(position);
            if (zEntry == null)
                return;
            IntentUtil.sendUpdateMyAddressMsg(getActivity(), zEntry);
            //new CacheManager(getActivity()).saveZoneCacheToDisk(JsonUtilsParser.toJson(zEntry).getBytes());
            //popBackStack();
            popBackStack();
            popBackStack();
        }
    };

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

    void searchCommunity() {
        final String keyword = keyword_et.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastHelper.showShort(getActivity(), "请输入小区名称...");
            return;
        }
        RequestParams params = new RequestParams();
        params.add("cid", buildCityId());
        params.add("kw", keyword);

        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "zone/search", params, new AsyncHttpResponseHandler() {
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

        }
    };

    void buildAdapter() {
        if (adapter == null) {
            adapter = new NearbyCommunityAdapter(getActivity());
        }
        mSearchResultLv.setAdapter(adapter);
    }

    NearbyCommunityAdapter adapter;
    ListView mSearchResultLv;
    View search_community;
    EditText keyword_et;
    View fill_in_address;
    View back;
}
