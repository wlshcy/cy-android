package com.shequcun.farm;

import android.app.Application;
import android.content.IntentFilter;

import com.shequcun.farm.http.HttpAction;
import com.shequcun.farm.receiver.NetworkReceiver;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by cong on 15/10/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        MobclickAgent.setDebugMode(true);
        //umeng统计禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
        registNetworkReceiver();
        new HttpAction(getApplicationContext()).init();
    }

    private void registNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        registerReceiver(new NetworkReceiver(), intentFilter);
    }
}
