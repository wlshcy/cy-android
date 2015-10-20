package com.shequcun.farm.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.shequcun.farm.ui.fragment.FarmSpecialtyDetailFragment;
import com.shequcun.farm.ui.fragment.TransparentFragment;

/**
 * Created by mac on 15/10/20.
 */
public class FarmSpecialtyDetailViewPagerAdapter extends FragmentPagerAdapter {

    Bundle bundle;

    public FarmSpecialtyDetailViewPagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        this.bundle = bundle;
    }

    SparseArray<Fragment> mPageReferenceMap = new SparseArray<Fragment>();

    public Fragment getOneFragment(int index) {
        return mPageReferenceMap.get(index);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = getFragment(position);
//        Bundle args = new Bundle();
        if (position == 0) {
            f = new TransparentFragment();
        } else if (position == 1) {
            f = new FarmSpecialtyDetailFragment();
        }
//        args.putInt("list_type", mListType);
        f.setArguments(bundle);
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


//    public void buildOrderListEntry(OrderListEntry oLentry) {
//        ArrayList<OrderEntry> dishesEntry = new ArrayList<OrderEntry>();
//        ArrayList<OrderEntry> shoppingEntry = new ArrayList<OrderEntry>();
//
//        if (oLentry != null && oLentry.aList != null) {
//            int size = oLentry.aList.size();
//            for (int i = 0; i < size; i++) {
//                OrderEntry tmpEntry = oLentry.aList.get(i);
//                if (tmpEntry.type == 1) {
//                    dishesEntry.add(tmpEntry);
//                } else {
//                    shoppingEntry.add(tmpEntry);
//                }
//            }
//        }
//
//        for (int i = 0; i < getCount(); ++i) {
//            Fragment fragment = getFragment(i);
//            if (fragment instanceof DishesFragment) {
//                ((DishesFragment) fragment).addDataToAdapter(dishesEntry);
//            } else if (fragment instanceof ShoppingOrderFragment) {
//                ((ShoppingOrderFragment) fragment).addDataToAdapter(shoppingEntry);
//            }
//        }
//    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
