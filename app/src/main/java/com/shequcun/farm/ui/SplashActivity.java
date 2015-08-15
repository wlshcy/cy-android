package com.shequcun.farm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.LocalParams;

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
        LocalParams.INSTANCE.initData(getApplicationContext());
        HttpRequestUtil.setContext(getApplicationContext());
        PersistanceManager.INSTANCE.initContext(getApplication());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity(new Intent(SplashActivity.this, SqcFarmActivity.class));
            finish();
        }
    };


}
