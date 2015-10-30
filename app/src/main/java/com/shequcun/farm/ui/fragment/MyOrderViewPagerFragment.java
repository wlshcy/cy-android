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
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

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
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.my_order);
        View myComboView = LayoutInflater.from(getBaseAct()).inflate(R.layout.my_order_listview_ly, null);
        new MyComboOrderViewHolder(this, myComboView);
        View boughtOrderView = LayoutInflater.from(getBaseAct()).inflate(R.layout.my_order_listview_ly, null);
        new ShoppingOrderViewHolder(this, boughtOrderView);
        ArrayList<View> aList = new ArrayList<>();
        aList.add(myComboView);
        aList.add(boughtOrderView);
        buildAdapter(aList);
    }


    void buildAdapter(ArrayList<View> aList) {
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(aList);
        orderPager.setAdapter(vpAdapter);
        orderPager.addOnPageChangeListener(onPageChangeListener);

        if (getArguments() != null) {
            setCurrentItem(1);
        }
    }

    @Override
    protected void setWidgetLsn() {
        if (orderPager != null) {
            orderPager.addOnPageChangeListener(onPageChangeListener);
            orderPager.setOffscreenPageLimit(1);
        }
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @OnClick({R.id.dishes_tv, R.id.shopping_tv})
    void doClick(View v) {
        if (orderPager != null)
            orderPager.setCurrentItem(v == dishes_tv ? 0 : 1);
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
        if (orderPager != null)
            orderPager.setCurrentItem(position);
        ColorStateList gray =
                getBaseAct().getResources().getColorStateList(R.color.gray_676767);
        ColorStateList lightGreeColor = getBaseAct().getResources().getColorStateList(R.color.green_11C258);
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
    @Bind(R.id.dishes_tv)
    TextView dishes_tv;
    /**
     * 购物订单
     */
    @Bind(R.id.shopping_tv)
    TextView shopping_tv;
    @Bind(R.id.orderPager)
    ViewPager orderPager;
}
