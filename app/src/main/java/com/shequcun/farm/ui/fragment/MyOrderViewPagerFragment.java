package com.shequcun.farm.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.MyOrderViewPagerAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;

/**
 * 我的订单
 * Created by apple on 15/8/8.
 */
public class MyOrderViewPagerFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_order_viewpager_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
//        mOrderDataCenter = new MyOrderDataCenter(getActivity());
        orderPager = (ViewPager) v.findViewById(R.id.orderPager);
        back = v.findViewById(R.id.back);
        dishes_tv = (TextView) v.findViewById(R.id.dishes_tv);
        shopping_tv = (TextView) v.findViewById(R.id.shopping_tv);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.my_order);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        shopping_tv.setOnClickListener(onClick);
        dishes_tv.setOnClickListener(onClick);
        orderPager.setOnPageChangeListener(onPageChangeListener);
        buildAdapter();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == dishes_tv)
                setCurrentItem(0);
            else if (v == shopping_tv)
                setCurrentItem(1);
        }
    };

    void buildAdapter() {
        adpter = new MyOrderViewPagerAdapter(getChildFragmentManager());
        orderPager.setAdapter(adpter);
//        mOrderDataCenter.requestMyOrder(adpter, 0, 20);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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
    };

    private void setCurrentItem(int position) {
        orderPager.setCurrentItem(position);
        ColorStateList gray =
                getActivity().getResources().getColorStateList(R.color.gray_676767);
        ColorStateList lightGreeColor = getActivity().getResources().getColorStateList(R.color.green_11C258);
        if (position == 0) {//菜品订单
            dishes_tv.setTextColor(lightGreeColor);
            shopping_tv.setTextColor(gray);
        } else if (position == 1) {//购物订单
            shopping_tv.setTextColor(lightGreeColor);
            dishes_tv.setTextColor(gray);
        }
    }

    /**
     * 菜品订单
     */
    TextView dishes_tv;
    /**
     * 购物订单
     */
    TextView shopping_tv;
    MyOrderViewPagerAdapter adpter;
    View back;
    ViewPager orderPager;

//    MyOrderDataCenter mOrderDataCenter;
}
