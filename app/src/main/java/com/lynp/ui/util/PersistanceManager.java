package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/29.
 */
import android.content.Context;

/**
 * Created by apple on 15/8/6.
 */
public class PersistanceManager {

    public static void saveCookieValue(Context mContext, String value) {
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putString("X-Xsrftoken", value).commit();
    }

    /*启动过了*/
    public static void saveOnce(Context mContext, boolean value) {
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putBoolean("installed", value).commit();
    }

    /*启动过了*/
    public static boolean getOnce(Context mContext) {
        return mContext.getSharedPreferences("Cookie", 0).getBoolean(
                "installed", false);
    }

    /*点击了修改密码*/
    public static void saveClickChangePwdFlag(Context mContext, boolean value) {
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putBoolean("click_change_pwd", value).commit();
    }

    /*点击了修改密码*/
    public static boolean getClickChangePwdFlag(Context mContext) {
        return mContext.getSharedPreferences("Cookie", 0).getBoolean(
                "click_change_pwd", false);
    }

    public static String getCookieValue(Context mContext) {
        return mContext.getSharedPreferences("Cookie", 0).getString(
                "X-Xsrftoken", "");
    }

    public static boolean getIsCheckVersion(Context mContext) {

        return mContext.getSharedPreferences("Cookie", 0).getBoolean("is_check_version", false);
    }

    public static void saveIsCheckVersion(Context mContext, boolean isCheck) {
        if (mContext == null) return;
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putBoolean("is_check_version", isCheck).commit();
    }

    public static void saveIsShowLookupComboDetails(Context mContext, String key, boolean isShow) {
        if (mContext == null) return;
        mContext.getSharedPreferences("Cookie", 0).edit()
                .putBoolean(key, isShow).commit();
    }

    public static boolean getIsShowLookUpComboDetails(Context mContext, String key) {
        if (mContext == null)
            return true;
        return mContext.getSharedPreferences("Cookie", 0).getBoolean(key, true);
    }
}