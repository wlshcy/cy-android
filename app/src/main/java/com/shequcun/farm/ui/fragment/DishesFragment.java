package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.MyOrderAdapter;

/**
 * 菜品订单
 * Created by apple on 15/8/8.
 */
public class DishesFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_order_listview_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        buidlAdapter();
    }

    @Override
    protected void setWidgetLsn() {
    }

    void buidlAdapter() {
        if (adapter == null) {
            adapter = new MyOrderAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
    }

    void addDataToAdapter(){

    }

    MyOrderAdapter adapter;

    ListView mLv;
}
