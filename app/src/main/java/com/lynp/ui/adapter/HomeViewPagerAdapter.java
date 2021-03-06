package com.lynp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.lynp.ui.fragment.HomeFragment;
import com.lynp.ui.fragment.ShoppingCartFragment;
import com.lynp.ui.fragment.MineFragment;

/**
 * Created by apple on 15/8/4.
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private int mListType = 1;
    SparseArray<Fragment> mPageReferenceMap = new SparseArray<Fragment>();

    public Fragment getOneFragment(int index) {
        return mPageReferenceMap.get(index);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = getFragment(position);
        if (position == 0) {
            f = new HomeFragment();
        } else if (position == 1) {
            f = new ShoppingCartFragment();
        } else if (position == 2) {
            f = new MineFragment();
        }
        mPageReferenceMap.put(position, f);
        return f;
    }

    public Fragment getFragment(int position) {
        return mPageReferenceMap.get(Integer.valueOf(position));
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
