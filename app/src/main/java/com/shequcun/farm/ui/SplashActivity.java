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
import com.shequcun.farm.dlg.UserGuideDialog;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

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
        MobclickAgent.updateOnlineConfig(this);
        //umeng统计
//        AnalyticsConfig.setAppkey(this, "55c870b067e58ec5440030b5");
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!PersistanceManager.getOnce(SplashActivity.this)) {
                UserGuideDialog userGuideDialog =  new UserGuideDialog(SplashActivity.this);
                userGuideDialog.show();
                userGuideDialog.setDismissDialog(dismissDialog);
                return;
            }
            gotoHome();
        }
    };

    private UserGuideDialog.DismissDialog dismissDialog = new UserGuideDialog.DismissDialog() {
        @Override
        public void dismiss() {
            gotoHome();
        }
    };

    private void gotoHome() {
        finish();
        startActivity(new Intent(SplashActivity.this, SqcFarmActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //umeng统计页面
        MobclickAgent.onPageStart("SplashScreen");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //umeng统计页面
        MobclickAgent.onPageEnd("SplashScreen");
    }
}
