package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.data.RecommentListEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.ui.adapter.SpecialtyAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by apple on 15/8/31.
 */
public class SpecialtyFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.discount_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        v.findViewById(R.id.back).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.specialty);
        buildAdapter();
    }

    @Override
    protected void setWidgetLsn() {
        requestRecomendDishes();
    }


    void buildAdapter() {
        if (adapter == null)
            adapter = new SpecialtyAdapter(getActivity());
        adapter.buildOnClickLsn(onGoodsImgLsn, onBuyLsn);
        mLv.setAdapter(adapter);
    }

    View.OnClickListener onBuyLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
            if (!(data != null && data.length > 0)) {
                showLoginDlg();
                return;
            }

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

    View.OnClickListener onGoodsImgLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            RecommendEntry entry = adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RecommendEntry", entry);
            gotoFragmentByAnimation(bundle, R.id.mainpage_ly, new RecommendGoodsDetailsFragment(), RecommendGoodsDetailsFragment.class.getName());
        }
    };


    void showLoginDlg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("提示");
        builder.setMessage("亲,您还未登录哦!立刻登录?");
        builder.setNegativeButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoFragment(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }


    void requestRecomendDishes() {
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/itemlist", new AsyncHttpResponseHandler() {
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
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }
    }

    SpecialtyAdapter adapter;
    ListView mLv;
}
