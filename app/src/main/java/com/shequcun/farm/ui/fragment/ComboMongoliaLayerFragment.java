package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shequcun.farm.R;
import com.shequcun.farm.util.AvoidDoubleClickListener;

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
        look_combo_detail_tv = v.findViewById(R.id.look_combo_detail_tv);
        close = v.findViewById(R.id.close);
    }

    @Override
    protected void setWidgetLsn() {
        close.setOnClickListener(onClick);
        look_combo_detail_tv.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (look_combo_detail_tv == v) {

            } else if (close == v)
                popBackStack();
        }
    };

    /**
     * 查看详情
     */
    View look_combo_detail_tv;

    View close;
}
