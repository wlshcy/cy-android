package com.shequcun.farm.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by mac on 15/10/26.
 */
public abstract class BaseViewPagerFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.baseview_pager_fragment_ly, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewpager.setPageTransformer(true, new Page());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0)
            popBackStack();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    public void buildPagerAdapter(View view) {
        if (view == null)
            return;
        ArrayList<View> aList = new ArrayList<>();
        aList.add(buildTranslucentView(getBaseAct()));
        aList.add(view);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(aList);
        viewpager.setAdapter(vpAdapter);
        viewpager.addOnPageChangeListener(this);
        viewpager.setCurrentItem(1);
    }

//    public void buildPagerAdapter(Context mContext, ArrayList<View> views) {
//        if (views == null || views.size() <= 0)
//            return;
//        views.add(0, buildTranslucentView(mContext));
//        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views);
//        viewpager.setAdapter(vpAdapter);
//        viewpager.addOnPageChangeListener(this);
//        viewpager.setCurrentItem(1);
//    }

    public View buildTranslucentView(Context mContext) {
        ImageView iv = new ImageView(mContext);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(mParams);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(mContext.getResources().getDrawable(android.R.color.transparent));
        } else {
            iv.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.transparent));
        }
        return iv;
    }


    class Page implements ViewPager.PageTransformer {
        private final float MIN_SCALE = 0.85f;
        private final float MIN_ALPHA = 0.5f;
        private final float SCALE_FACTOR = 0.95f;

        public Page() {

        }

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


    @Bind(R.id.viewpager)
    ViewPager viewpager;
}
