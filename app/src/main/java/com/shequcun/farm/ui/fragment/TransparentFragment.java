package com.shequcun.farm.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by mac on 15/10/20.
 */
public class TransparentFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView iv = new ImageView(getBaseAct());
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(mParams);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(getBaseAct().getResources().getDrawable(android.R.color.transparent));
        } else {
            iv.setBackgroundDrawable(getBaseAct().getResources().getDrawable(android.R.color.transparent));
        }
        return iv;
    }

    @Override
    protected void setWidgetLsn() {

    }

    @Override
    protected void initWidget(View v) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
