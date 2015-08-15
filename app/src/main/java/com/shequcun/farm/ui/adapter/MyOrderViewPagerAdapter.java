package com.shequcun.farm.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.shequcun.farm.ui.fragment.DishesFragment;
import com.shequcun.farm.ui.fragment.ShoppingOrderFragment;

/**
 * Created by apple check_turn_on 15/7/21.
 */
public class MyOrderViewPagerAdapter extends FragmentPagerAdapter {

    public MyOrderViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private int mListType = 1;

    // private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer,
    // Fragment>();
    SparseArray<Fragment> mPageReferenceMap = new SparseArray<Fragment>();

    public Fragment getOneFragment(int index) {
        return mPageReferenceMap.get(index);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = getFragment(position);
//        Bundle args = new Bundle();
        if (position == 0) {
            f = new DishesFragment();
        } else if (position == 1) {
            f = new ShoppingOrderFragment();
        }
//        args.putInt("list_type", mListType);
//        f.setArguments(args);
        mPageReferenceMap.put(position, f);
        return f;
    }

    public Fragment getFragment(int position) {
        return mPageReferenceMap.get(Integer.valueOf(position));
    }

    @Override
    public int getCount() {
        return 2;
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