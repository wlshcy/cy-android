package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.FarmSpecialtyDetailViewPagerAdapter;

import butterknife.Bind;
import butterknife.OnClick;

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
        vpg.setPageTransformer(true, new Page());
        vpg.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


    class Page implements ViewPager.PageTransformer {


        private  final float MIN_SCALE = 0.85f;
        private  final float MIN_ALPHA = 0.5f;
        private  final float SCALE_FACTOR = 0.95f;
        ViewPager mViewPager;

        public Page(ViewPager mViewPager) {
            this.mViewPager = mViewPager;
        }

        public Page(){

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

//            if (position <= 0) {
//                // apply zoom effect and offset translation only for pages to
//                // the left
//                final float transformValue = Math.abs(Math.abs(position) - 1) * (1.0f - SCALE_FACTOR) + SCALE_FACTOR;
//                int pageWidth = mViewPager.getWidth();
//                final float translateValue = position * -pageWidth;
//                view.setScaleX(transformValue);
//                view.setScaleY(transformValue);
//                if (translateValue > -pageWidth) {
//                    view.setTranslationX(translateValue);
//                } else {
//                    view.setTranslationX(0);
//                }
//            }
        }
    }


    FarmSpecialtyDetailViewPagerAdapter adapter;
    @Bind(R.id.welcome_viewpager)
    ViewPager vpg;
}
