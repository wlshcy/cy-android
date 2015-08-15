package com.shequcun.farm.datacenter;

import android.content.Context;

import com.shequcun.farm.db.DBLite;

/**
 * Created by apple on 15/8/7.
 */
public class CacheManager {
    public Context mContext;

    public CacheManager(Context context) {
        this.mContext = context;
    }

    public void saveUserLoginToDisk(byte[] data) {
        try {
            DBLite dblite = new DBLite(mContext, null,
                    KeyWord_UserLoginCacheTag);
            dblite.deleteData();
            dblite.saveToDisk(data);
        } catch (Exception e) {
        }
    }

//    public void delUserLoginToDisk() {
//        try {
//            DBLite dblite = new DBLite(mContext, null,
//                    KeyWord_UserLoginCacheTag);
//            dblite.deleteData();
//        } catch (Exception e) {
//        }
//    }

    public byte[] getUserLoginFromDisk() {
        try {
            DBLite dblite = new DBLite(mContext, null, KeyWord_UserLoginCacheTag);
            return dblite.getZoneData();
        } catch (Exception e) {
        }
        return null;
    }

    public void saveZoneCacheToDisk(byte[] data) {
        try {
            DBLite dblite = new DBLite(mContext, null,
                    KeyWord_UserZoneCacheTag);
            dblite.deleteData();
            dblite.saveToDisk(data);
        } catch (Exception e) {
        }
    }

    public byte[] getZoneCacheFromDisk() {
        try {
            DBLite dblite = new DBLite(mContext, null, KeyWord_UserZoneCacheTag);
            return dblite.getZoneData();
        } catch (Exception e) {
        }
        return null;
    }

    public void saveAddressCacheToDisk(byte[] data) {
        try {
            DBLite dblite = new DBLite(mContext, null,
                    KeyWord_UserAddressTag);
            dblite.deleteData();
            dblite.saveToDisk(data);
        } catch (Exception e) {
        }
    }

    public byte[] getAddressCacheFromDisk() {
        try {
            DBLite dblite = new DBLite(mContext, null, KeyWord_UserAddressTag);
            return dblite.getZoneData();
        } catch (Exception e) {
        }
        return null;
    }

    final String KeyWord_UserZoneCacheTag = "UserZoneCacheTag";
    final String KeyWord_UserLoginCacheTag = "UserLoginCacheTag";
    final String KeyWord_UserAddressTag = "UserAddressTag";
}
