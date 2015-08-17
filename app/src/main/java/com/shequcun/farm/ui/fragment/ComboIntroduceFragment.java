package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import java.util.ArrayList;

/**
 * 套餐介绍
 * Created by apple on 15/8/17.
 */
public class ComboIntroduceFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

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
        // 实例化ViewPager
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        right.setOnClickListener(onClick);

        viewPager.setOnPageChangeListener(this);
        // 设置适配器数据
        viewPager.setAdapter(vpAdapter);
//        // 将要分页显示的View装入数组中
//        views.add(view1);
//        views.add(view2);
//        views.add(view3);
//        views.add(view4);
        vpAdapter.notifyDataSetChanged();
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    ViewPagerAdapter vpAdapter;
    ArrayList views;
    ViewPager viewPager;
}
