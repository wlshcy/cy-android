package com.lynp.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nmg on 16/1/28.
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
        if (getCurrentItem() == 0) {
            if (event.getY() < childVPHeight) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(event);
    }
}
