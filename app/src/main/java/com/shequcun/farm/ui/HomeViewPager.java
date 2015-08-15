package com.shequcun.farm.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by apple on 15/8/3.
 */
public class HomeViewPager extends ViewPager {

    int childVPHeight;

    public HomeViewPager(Context context) {
        super(context);
        childVPHeight = (context.getResources().getDisplayMetrics().heightPixels / 3);
    }

    public HomeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        childVPHeight = (context.getResources().getDisplayMetrics().heightPixels / 3);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//        PagerAdapter adapter = this.getAdapter();
        if (getCurrentItem() == 0) {
            if (event.getY() < childVPHeight) {
                return false;
            }
        }
//        if (adapter instanceof HomeViewPagerAdapter) {
//            if (event.getY() < childVPHeight) {
//                return false;
//            }
//        }
        return super.onInterceptTouchEvent(event);
    }
}
