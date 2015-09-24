package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import butterknife.Bind;

/**
 * 产品介绍
 * Created by apple on 15/8/7.
 */
public class ComboMongoliaLayerFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_mongolia_layer_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
    }

    @Override
    protected void setWidgetLsn() {
        close.setOnClickListener(onClick);
        look_combo_detail_tv.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            PersistanceManager.saveIsShowLookupComboDetails(getActivity(), buildKey(), false);
            if (look_combo_detail_tv == v) {
                popBackStack();
                gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new WebViewFragment(), WebViewFragment.class.getName());
            } else if (close == v) {
                popBackStack();
            }
        }
    };


    private String buildKey() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            ComboEntry entry = (ComboEntry) bundle.getSerializable("ComboEntry");
            if (entry != null) {
                return entry.id + "" + entry.weights[entry.getPosition()];
            }
        }
        return "";
    }


    /**
     * 查看详情
     */
    @Bind(R.id.look_combo_detail_tv)
    View look_combo_detail_tv;
    @Bind(R.id.close)
    View close;
}
