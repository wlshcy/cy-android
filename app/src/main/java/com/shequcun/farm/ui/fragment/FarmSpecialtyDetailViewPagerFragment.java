package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.FarmSpecialtyDetailViewPagerAdapter;

import butterknife.Bind;

/**
 * Created by mac on 15/10/20.
 */
public class FarmSpecialtyDetailViewPagerFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ucai_guide_ly, container, false);
    }

    @Override
    protected void setWidgetLsn() {
        buildAdapter();
        vpg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setCurrentItem(1);
    }

    @Override
    protected void initWidget(View v) {
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    void buildAdapter() {
        adapter = new FarmSpecialtyDetailViewPagerAdapter(getChildFragmentManager(), getArguments());
        vpg.setAdapter(adapter);
    }


    private void setCurrentItem(int position) {
        if (position == 0) {
            popBackStack();
            return;
        }
        vpg.setCurrentItem(position);
    }


    FarmSpecialtyDetailViewPagerAdapter adapter;
    @Bind(R.id.welcome_viewpager)
    ViewPager vpg;
}
