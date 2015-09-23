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
        back.setOnClickListener(onClick);
        mLv.setOnItemClickListener(onItemLsn);
        buildAdapter();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
        }
    };


    AdapterView.OnItemClickListener onItemLsn = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (entry != null) {
                entry.setPosition(position);
                gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            }
        }
    };


    void buildAdapter() {
        buildComboEntry();
        if (adapter == null)
            adapter = new ComboSubAdapter(getActivity(), entry);
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
    @Bind(R.id.back)
    View back;
    ComboSubAdapter adapter;
}
