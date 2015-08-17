package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.ui.adapter.ComboSubAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;


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
        back = v.findViewById(R.id.back);
        mLv = (ListView) v.findViewById(R.id.mLv);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(entry.title + "详情");
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        buildAdapter();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
        }
    };


    void buildAdapter() {
        buildComboEntry();
        if (adapter == null)
            adapter = new ComboSubAdapter(getActivity(), entry);
        adapter.setChooseDishesLsn(chooseDishes);
        mLv.setAdapter(adapter);
    }

    void buildComboEntry() {
        if (entry != null)
            return;
        Bundle bundle = getArguments();
        entry = bundle != null ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }

    AvoidDoubleClickListener chooseDishes = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            gotoFragmentByAdd(buildBundle((ComboEntry) v.getTag()), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
        }
    };

    Bundle buildBundle(ComboEntry entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("ComboEntry", entry);
        return bundle;
    }

    ListView mLv;
    ComboEntry entry;
    View back;
    ComboSubAdapter adapter;
}
