package com.lynp.ui;

import android.app.Application;
//import com.umeng.analytics.MobclickAgent;

/**
 * Created by cong on 15/10/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        MobclickAgent.setDebugMode(true);
        //umeng统计禁止默认的页面统计方式
//        MobclickAgent.openActivityDurationTrack(false);
    }
}
