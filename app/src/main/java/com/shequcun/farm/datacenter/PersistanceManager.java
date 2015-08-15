package com.shequcun.farm.datacenter;

import android.content.Context;

/**
 * Created by apple on 15/8/6.
 */
public enum PersistanceManager {
    INSTANCE;
    private Context mContext;

    public void initContext(Context context) {
        mContext = context;
    }

    public boolean isInit() {
        return mContext != null;
    }

    public void saveCookieValue(String value) {
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putString("X-Xsrftoken", value).commit();
    }

    public String getCookieValue() {
        return mContext.getSharedPreferences("Cookie", 0).getString(
                "X-Xsrftoken", "");
    }
}
