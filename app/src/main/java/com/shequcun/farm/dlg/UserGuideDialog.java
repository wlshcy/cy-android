package com.shequcun.farm.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by mac on 15/10/12.
 */
public class UserGuideDialog extends Dialog {

    public UserGuideDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.ucai_guide_ly);
        initViewPager();
    }



    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
        views = new ArrayList<View>();
//        欢迎页面

        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(mParams);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                iv.setBackground(getContext().getResources().getDrawable(pics[i]));
            } else {
                iv.setBackgroundDrawable(getContext().getResources().getDrawable(pics[i]));
            }


            views.add(iv);
        }
        vpAdapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(vpAdapter);
        setOnclick();
    }

    private void setOnclick() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 3) {
                    dismiss();
                    PersistanceManager.saveOnce(getContext(), true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private int[] pics = {R.drawable.guide1, R.drawable.guide2,
            R.drawable.guide3, android.R.color.transparent};
    private ViewPagerAdapter vpAdapter;
    private ViewPager viewPager;
    private ArrayList<View> views;
}
