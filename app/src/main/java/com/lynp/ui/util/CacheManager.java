package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/29.
 */

import android.content.Context;
import android.text.TextUtils;

import com.lynp.ui.data.AddressEntry;
import com.lynp.ui.data.RecommendEntry;
import com.lynp.ui.data.UserLoginEntry;
import com.lynp.ui.db.DBLite;
import com.lynp.ui.db.DBRecordItem;
import com.lynp.ui.db.RecommendItemKey;
import com.lynp.ui.util.JsonUtilsParser;

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

    public void delUserLoginToDisk() {
        try {
            DBLite dblite = new DBLite(mContext, null,
                    KeyWord_UserLoginCacheTag);
            dblite.deleteData();
        } catch (Exception e) {
        }
    }

    public void saveUserReceivingAddress(byte[] data) {
        try {
            DBLite dblite = new DBLite(mContext, null, KeyWord_UserReceivingAddressTag);
            dblite.deleteData();
            dblite.saveToDisk(data);
        } catch (Exception e) {
        }
    }

    public AddressEntry getUserReceivingAddress() {
        byte[] data = getData(KeyWord_UserReceivingAddressTag);
        if (data == null && data.length <= 0)
            return null;
        return JsonUtilsParser.fromJson(new String(data), AddressEntry.class);
    }

    public byte[] getData(String keyWord) {
        try {
            DBLite dblite = new DBLite(mContext, null, keyWord);
            return dblite.getZoneData();
        } catch (Exception e) {
        }
        return null;
    }

    public UserLoginEntry getUserLoginEntry() {
        byte[] data = getData(KeyWord_UserLoginCacheTag);
        if (data == null || data.length <= 0)
            return null;
        UserLoginEntry uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        return uEntry;
    }


    public RecommendEntry[] getRecommendFromDisk() {
        DBLite dblite = new DBLite(mContext, null, KeyWord_RecommendTag);
        dblite.loadData();
        int length = dblite.getRecordSize();
        if (length <= 0)
            return null;
        RecommendEntry[] ze = new RecommendEntry[length];
        for (int i = 0; i < length; i++) {
            DBRecordItem aitem = dblite.getRecord(i);
            ze[i] = parseRecommentItem(aitem.getStringValue("RecommentItem", ""));
        }
        return ze;
    }

    public void delRecommendToDisk() {
        DBLite dblite = new DBLite(mContext, null, KeyWord_RecommendTag);
        dblite.deleteData();
    }


    public void delRecommendItemToDisk(RecommendItemKey zItem) {
        try {
            if (zItem == null || zItem.object == null)
                return;
            String newHist = zItem.getKeyId();
            DBLite dblite = new DBLite(mContext, null, KeyWord_RecommendTag);
            dblite.loadData();
            int length = dblite.getRecordSize();
            for (int i = 0; i < length; i++) {
                DBRecordItem aitem = dblite.getRecord(i);
                if (newHist.contentEquals(aitem.getStringValue("RecommentItemKey",
                        ""))) {
                    dblite.deleteRecord(i);
                    break;
                }
            }

            dblite.saveToDisk();
        } catch (Exception e) {

        }

    }

    public RecommendEntry getRecommendEntry(RecommendItemKey zItem) {
        try {
            if (zItem == null || zItem.object == null)
                return null;
            String newHist = zItem.getKeyId();
            DBLite dblite = new DBLite(mContext, null, KeyWord_RecommendTag);
            dblite.loadData();
            int length = dblite.getRecordSize();
            for (int i = 0; i < length; i++) {
                DBRecordItem aitem = dblite.getRecord(i);
                if (newHist.contentEquals(aitem.getStringValue("RecommentItemKey",
                        ""))) {
                    return parseRecommentItem(aitem.getStringValue("RecommentItem", ""));
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void saveRecommendToDisk(RecommendItemKey zItem) {
        try {
            if (zItem == null || zItem.object == null)
                return;
            String newHist = zItem.getKeyId();
            DBLite dblite = new DBLite(mContext, null, KeyWord_RecommendTag);
            dblite.loadData();
            int length = dblite.getRecordSize();
            for (int i = 0; i < length; i++) {
                DBRecordItem aitem = dblite.getRecord(i);
                if (newHist.contentEquals(aitem.getStringValue("RecommentItemKey",
                        ""))) {
                    dblite.deleteRecord(i);
                    break;
                }
            }
            length = dblite.getRecordSize();
            if (length >= MAX_COUNT) {
                dblite.deleteRecord(length - 1);
            }
            DBRecordItem item = new DBRecordItem();
            item.setStringValue("RecommentItemKey", newHist);
            RecommendEntry zEntry = (RecommendEntry) zItem.object;
            item.setStringValue("RecommentItem", JsonUtilsParser.toJson(zEntry));
            dblite.insertRecord(item, 0);
            dblite.saveToDisk();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private RecommendEntry parseRecommentItem(String result) {
        if (TextUtils.isEmpty(result))
            return null;
        try {
            return JsonUtilsParser.fromJson(result, RecommendEntry.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    final String KeyWord_UserLoginCacheTag = "UserLoginCacheTag";
    final String KeyWord_RecommendTag = "UserRecommendTag";
    final String KeyWord_UserReceivingAddressTag = "UserReceivingAddressTag";
    final int MAX_COUNT = 100;
}