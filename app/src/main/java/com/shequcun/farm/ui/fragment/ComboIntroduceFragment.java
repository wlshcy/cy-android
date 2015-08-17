package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;

/**
 * 套餐介绍
 * Created by apple on 15/8/17.
 */
public class ComboIntroduceFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_introduce_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.combo_introduce);
        right = v.findViewById(R.id.title_right_text);
        ((TextView) v.findViewById(R.id.title_right_text)).setText(R.string.consultation);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        right.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == right) {
                ConsultationDlg.showCallTelDlg(getActivity());
            }
        }
    };

    View back;
    View right;
}
