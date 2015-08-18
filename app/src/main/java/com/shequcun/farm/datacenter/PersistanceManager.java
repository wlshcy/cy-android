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

    public boolean getIsCheckVersion() {
        if (mContext == null) {
            return false;
        }
        return mContext.getSharedPreferences("Cookie", 0).getBoolean("is_check_version", false);
    }

    public void saveIsCheckVersion(boolean isCheck) {
        if (mContext == null) return;
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putBoolean("is_check_version", isCheck).commit();
    }
}
