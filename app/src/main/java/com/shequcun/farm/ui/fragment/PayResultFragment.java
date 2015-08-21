package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.ui.adapter.RecommendAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;

import org.apache.http.Header;

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
        clearStack();
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.pay_success);
        recoTv = (TextView) v.findViewById(R.id.common_small_tv);
        recoTv.setVisibility(View.GONE);
    }

    @Override
    protected void setWidgetLsn() {
        buildAdapter();
        back.setOnClickListener(onClick);
        if (isRecomDishes())
            requestRecomendDishes();
    }


    boolean isRecomDishes() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null) {
            return entry.isRecoDishes;
        }
        return false;
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
            if (adapter == null)
                return;

            RecommendEntry entry = adapter.getItem(position);

            if (entry.remains <= 0) {
                alertOutOfRemains();
                return;
            }

            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new SingleDishesFragment(), SingleDishesFragment.class.getName());
        }
    };

    /**
     * 剩余量不足
     */
    private void alertOutOfRemains() {
        String content = getResources().getString(R.string.out_of_remains);
        alertDialog(content);
    }

    /**
     * 库存不足
     *
     * @param maxpacks
     */
    private void alertOutOfMaxpacks(int maxpacks) {
        String content = getResources().getString(R.string.out_of_maxpacks);
        content = content.replace("A", maxpacks + "");
        alertDialog(content);
    }

    private void alertDialog(String content) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.alert_dialog);
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

    Bundle buildBundle(RecommendEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecommendEntry", entry);
        return bundle;
    }

    AvoidDoubleClickListener onGoodsImgLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            RecommendEntry entry = adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RecommendEntry", entry);
            gotoFragmentByAnimation(bundle, R.id.mainpage_ly, new RecommendGoodsDetailsFragment(), RecommendGoodsDetailsFragment.class.getName());
//            int position = (int) v.getTag();
//            ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
//            for (int i = 0; i < adapter.getItem(position).imgs.length; ++i) {
//                photos.add(new PhotoModel(true, adapter.getItem(position).imgs[i]));
//            }
//
//            Bundle budle = new Bundle();
//            budle.putSerializable(BrowseImageFragment.KEY_PHOTOS, photos);
//            budle.putInt(BrowseImageFragment.KEY_INDEX, position);
//            gotoFragmentByAdd(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName());
        }
    };

    void requestRecomendDishes() {
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/itemlist", new AsyncHttpResponseHandler() {
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

    void addDataToAdapter(List<RecommendEntry> aList) {
        if (aList != null && aList.size() > 0) {
            recoTv.setVisibility(View.VISIBLE);
            recoTv.setText("为您特别推荐");
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
    }

    TextView recoTv;
    ListView mLv;
    RecommendAdapter adapter;
    View back;
}
