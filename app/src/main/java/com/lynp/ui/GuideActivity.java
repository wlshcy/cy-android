package com.lynp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shequcun.farm.BaseFragmentActivity;
//import com.shequcun.farm.R;
import com.lynp.R;

import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.UserGuideDialog;

/**
 * 向导
 * Created by nmg on 16/1/28.
 */
public class GuideActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ly);
        MessageHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler MessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!PersistanceManager.getOnce(GuideActivity.this)) {
                UserGuideDialog userGuideDialog =  new UserGuideDialog(GuideActivity.this);
                userGuideDialog.show();
//                userGuideDialog.setDismissDialog(dismissDialog);
                return;
            }
            showMainPage();
        }
    };

//    private UserGuideDialog.DismissDialog dismissDialog = new UserGuideDialog.DismissDialog() {
//        @Override
//        public void dismiss() {
//            gotoHome();
//        }
//    };

    private void showMainPage() {
        finish();
        startActivity(new Intent(GuideActivity.this, MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
