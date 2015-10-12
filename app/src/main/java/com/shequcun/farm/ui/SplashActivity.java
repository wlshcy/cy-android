package com.shequcun.farm.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ly);
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            gotoHome();
        }
    };

    private void gotoHome() {
        startActivity(new Intent(SplashActivity.this, SqcFarmActivity.class));
        finish();
    }
}
