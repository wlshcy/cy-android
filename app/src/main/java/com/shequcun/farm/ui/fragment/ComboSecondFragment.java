package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.ui.adapter.ComboSubAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * 二级套餐页
 * Created by apple on 15/8/15.
 */
public class ComboSecondFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_second_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        buildComboEntry();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(entry.title + "详情");
    }

    @Override
    protected void setWidgetLsn() {
        buildAdapter();
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @OnItemClick(R.id.mLv)
    void OnItemClick(int pos) {
        if (entry != null) {
            entry.setPosition(pos);
            gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
        }
    }
    void buildAdapter() {
        buildComboEntry();
        if (adapter == null)
            adapter = new ComboSubAdapter(getBaseAct(), entry);
        mLv.setAdapter(adapter);
    }

    void buildComboEntry() {
        if (entry != null)
            return;
        Bundle bundle = getArguments();
        entry = bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }


    Bundle buildBundle(ComboEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("ComboEntry", entry);
        return bundle;
    }

    @Bind(R.id.mLv)
    ListView mLv;
    ComboEntry entry;
    ComboSubAdapter adapter;
}
