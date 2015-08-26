package com.shequcun.farm.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;
import com.shequcun.farm.util.HttpRequestUtil;

import java.util.ArrayList;

/**
 * 闪屏
 * Created by apple on 15/8/3.
 */
public class SplashActivity extends BaseFragmentActivity {
    private ViewPagerAdapter vpAdapter;
    private ViewPager viewPager;
    private ArrayList<View> views;
    private int welcomeImg = R.drawable.splash_bg;
    private int[] pics = {R.drawable.guide1, R.drawable.guide2,
            R.drawable.guide3};
    private TextView mWelcomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ly);
//        LocalParams.INSTANCE.initData(getApplicationContext());
        HttpRequestUtil.setContext(getApplicationContext());
        initViewPager();
        setOnclick();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            gotoHome();
        }
    };

    private void gotoHome(){
        startActivity(new Intent(SplashActivity.this, SqcFarmActivity.class));
        finish();
    }

    private void initViewPager() {
        mWelcomeBtn = (TextView) findViewById(R.id.welcome_btn);
        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
        views = new ArrayList<View>();
//        欢迎页面
        if (isStartUped()) {
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(mParams);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                iv.setBackground(getResources().getDrawable(welcomeImg));
            } else {
                iv.setBackgroundDrawable(getResources().getDrawable(welcomeImg));
            }
            views.add(iv);
            mHandler.sendEmptyMessageDelayed(0, 1000);
//            滑动轮播图
        } else {
            for (int i = 0; i < pics.length; i++) {
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(mParams);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    iv.setBackground(getResources().getDrawable(pics[i]));
                } else {
                    iv.setBackgroundDrawable(getResources().getDrawable(pics[i]));
                }
                views.add(iv);
            }
        }
        vpAdapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(vpAdapter);
    }

    private boolean isStartUped() {
        return PersistanceManager.getOnce(getApplicationContext());
    }

    private void setOnclick() {
        mWelcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHome();
                PersistanceManager.saveOnce(getApplicationContext(),true);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 2) {
                    mWelcomeBtn.setVisibility(View.VISIBLE);
                } else {
                    mWelcomeBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

}
