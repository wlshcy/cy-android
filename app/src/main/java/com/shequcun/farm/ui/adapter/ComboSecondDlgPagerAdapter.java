package com.shequcun.farm.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by mac on 15/10/9.
 */
public class ComboSecondDlgPagerAdapter extends PagerAdapter {

    // 界面列表
    private ArrayList<View> views;

    public ComboSecondDlgPagerAdapter(ArrayList<View> views) {
        this.views = views;
    }

    public void clear() {
        this.views.clear();
        this.notifyDataSetChanged();
    }

    public void replace(ArrayList<View> views) {
        this.views.clear();
        this.views.addAll(views);
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    public float getPageWidth(int position) {
        return 0.5f;
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(View view, int position) {
        if (view == null) {
            return null;
        }
        ((ViewPager) view).addView(views.get(position), 0);
        return views.get(position);


    }


    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return (view == arg1);
    }

    /**
     * 销毁position位置的界面
     */
    @Override
    public void destroyItem(View view, int position, Object arg2) {
        try {
            if (views.size() > 0) {
                ((ViewPager) view).removeView(views.get(position));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
