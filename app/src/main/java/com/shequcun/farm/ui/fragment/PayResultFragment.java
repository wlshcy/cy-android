package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.model.PhotoModel;
import com.shequcun.farm.ui.adapter.RecommendAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付结果界面
 * Created by apple on 15/8/18.
 */
public class PayResultFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_result_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.common_small_tv)).setText("为您特别推荐");
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back) {
                clearStack();
            }
        }
    };

    void buildAdapter() {
        if (adapter == null)
            adapter = new RecommendAdapter(getActivity());
        adapter.buildOnClickLsn(onGoodsImgLsn, onBuyLsn);
        mLv.setAdapter(adapter);
    }


    AvoidDoubleClickListener onBuyLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            int position = (int) v.getTag();
        }
    };

    AvoidDoubleClickListener onGoodsImgLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
            for (int i = 0; i < adapter.getItem(position).imgs.length; ++i) {
                photos.add(new PhotoModel(true, adapter.getItem(position).imgs[i]));
            }

            Bundle budle = new Bundle();
            budle.putSerializable(BrowseImageFragment.KEY_PHOTOS, photos);
            budle.putInt(BrowseImageFragment.KEY_INDEX, position);
            gotoFragmentByAdd(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName());
        }
    };

    void requestRecommendDishes() {
        RequestParams params = new RequestParams();
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "reco/dishes", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    RecommentListEntry entry = JsonUtilsParser.fromJson(new String(data), RecommentListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            addDataToAdapter(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
            }
        });
    }

    void addDataToAdapter(List<RecommendEntry> aList) {
        if(aList==null || aList.size()<=0)
            return;
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
    }

    ListView mLv;
    RecommendAdapter adapter;
    View back;
}
