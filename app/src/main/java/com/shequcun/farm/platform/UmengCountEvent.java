package com.shequcun.farm.platform;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by cong on 15/10/31.
 */
public class UmengCountEvent {
    private static final String home_banner = "click_home_banner";

    public static void onClickHomeBanner(Context context) {
        MobclickAgent.onEvent(context, home_banner);
    }
}
