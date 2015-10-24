package com.shequcun.farm.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.widget.PagerSlidingTabStrip;
import com.shequcun.farm.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by cong on 15/10/23.
 */
public class LoginMainFragment extends BaseFragment {
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.pager)
    ViewPager pager;

    LoginFragment smscodeLoginFragment;
    LoginPasswordFragment passwordFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_main, null);
        return view;
    }

    @OnClick(R.id.back)
    public void onBack() {
        popBackStack();
    }

    @Override
    protected void setWidgetLsn() {
    }

    @Override
    protected void initWidget(View v) {
        pager.setAdapter(new MyPagerAdapter(getFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        tabs.setUnderlineColorResource(R.color.divider_color);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#11C258"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#11C258"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
        tabs.setPadding(0,0,0,0);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 如果用FragmentPagerAdapter会导致第二次加载不出的问题
     */
    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"短信登录", "密码登录"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (smscodeLoginFragment == null) {
                        smscodeLoginFragment = new LoginFragment();
                    }
                    return smscodeLoginFragment;
                case 1:
                    if (passwordFragment == null) {
                        passwordFragment = new LoginPasswordFragment();
                    }
                    return passwordFragment;
                default:
                    return null;
            }
        }
    }
}
