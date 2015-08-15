package com.shequcun.farm.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

/**
 * 缓存管理
 */
public class DBManager {
    public static final String SP_CACHE_TOUTIAO = "SP_Data_Cache_Headlines";
    public static final String SP_CACHE_KEJI = "SP_Data_Cache_Technology";
    public static final String SP_CACHE_ZHENGCE = "SP_Data_Cache_Policy";
    public static final String SP_CACHE_PEIXUN = "SP_Data_Cache_Train";
    public static final String SP_LOCAL_CONFIG = "SP_Local_Province";
    private static int CACHE_TIME_MINUTES = 5; // 缓存有效时间（单位：分钟）
    private Context mContext;
    private CacheDBHelper mDBManager;
    private SQLiteDatabase mDatabase = null;
    private static DBManager instance;

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    private DBManager(Context context) {
        mContext = context;
        mDBManager = new CacheDBHelper(mContext);
    }

    /**
     * 打开数据库
     */
    public void openDatabase() {
        mDatabase = mDBManager.GetWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public void release() {
        closeDatabase();
        if (mDBManager != null) {
            mDBManager.Close();
            mDBManager = null;
        }
        instance = null;
    }

    /**
     * 取本地数据库中url对应的本地缓存
     *
     * @param key
     * @return
     */
    public byte[] getCacheData(String key) {
        Cursor cur = null;
        byte[] data = null;
        if (mDatabase == null) {
            openDatabase();
        }
        String shortUrl = NetUtil.shortUrl(key); // 将url加密转化成短串
        try {
            mDatabase = mDBManager.GetWritableDatabase();
            cur = mDatabase.query(true, CacheDBHelper.TABLE_CACHE,
                    new String[]{"key", "data", "updatetime"}, "key=?",
                    new String[]{shortUrl}, null, null, null, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                data = cur.getBlob(cur.getColumnIndex("data"));
                // String timestamp = cur.getString(cur
                // .getColumnIndex("updatetime"));
                // long milliseconds = Long.parseLong(timestamp);
                // Calendar cal = Calendar.getInstance();
                // long curtime = cal.getTimeInMillis();
                // long timedif = curtime - milliseconds;
                // int timeMinutes = (int) (timedif / 1000 / 60);
                // if (timeMinutes >= CACHE_TIME_MINUTES) {
                // data = null;
                // }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cur != null)
            cur.close();
        return data;
    }

    /**
     * 将指定key对应的数据存储到本地缓存数据库
     *
     * @param key
     * @param data
     */
    public void saveCacheData(String key, byte[] data) {
        if (data == null) {
            return;
        }
        if (mDatabase == null) {
            openDatabase();
        }
        String shortUrl = NetUtil.shortUrl(key); // 将url加密转化成短串
        Calendar cal = Calendar.getInstance();
        Cursor cur = mDatabase.query(true, CacheDBHelper.TABLE_CACHE,
                new String[]{"key", "data", "updatetime"}, "key=?",
                new String[]{shortUrl}, null, null, null, null);
        if (cur.moveToFirst()) {
            String[] args = {shortUrl};
            mDatabase.delete(CacheDBHelper.TABLE_CACHE, "key=?", args);
        }
        String sqlstr = "insert into " + CacheDBHelper.TABLE_CACHE
                + " (key, data,updatetime) values (?,?,?);";
        Object[] args = new Object[]{shortUrl, data, cal.getTimeInMillis()};
        try {
            mDatabase.execSQL(sqlstr, args);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    // public String getCacheFileDir() {
    // String filepath = Environment.getExternalStorageDirectory()
    // + NetUtil.IMAGE_CACHE_DIR;
    // return filepath;
    // }
    public void saveLocalProvince() {

    }

    public void clearCache() throws Exception {
        // 1.清空本地图片缓存
        // String filepath = getCacheFileDir();
        // FileUtil.delFileFolder(filepath);
        // 2.清空本地sp缓存
        String[] spName = new String[]{"SharedPreferences", SP_CACHE_TOUTIAO,
                SP_CACHE_KEJI, SP_CACHE_ZHENGCE, SP_CACHE_PEIXUN,
                SP_LOCAL_CONFIG};
        for (int i = 0, spNameLen = spName.length; i < spNameLen; i++) {
            SharedPreferences sp = mContext.getSharedPreferences(spName[i], 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }
        // 3.清空本地数据库缓存
        mDatabase = mDBManager.GetWritableDatabase();
        mDatabase.delete(CacheDBHelper.TABLE_CACHE, null, null);
    }

//    public List<PushModel> selectPushModel() {
//        if (mDatabase == null) {
//            openDatabase();
//        }
//        List<PushModel> list = null;
//        try {
//            Cursor cursor = mDatabase.query(CacheDBHelper.TABLE_PUSH, null, null, null, null, null, null);
//            list = new ArrayList<PushModel>();
//            if (cursor != null)
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                    list.add(PushModel.getModelFromCursor(cursor));
//                }
//        } catch (Exception e) {
//
//        } finally {
//            closeDatabase();
//        }
//        return list;
//    }

    public long insertPushModle(ContentValues values) {
        if (mDatabase == null) {
            openDatabase();
        }
        long i = -1;
        try {
            i = mDatabase.insert(CacheDBHelper.TABLE_PUSH, null, values);
        } catch (Exception e) {

        } finally {
            closeDatabase();
        }
        return i;
    }

    public int deletePushModel(String whereClause, String whereArgs[]) {
        if (mDatabase == null) {
            openDatabase();
        }
        int id = -1;
        try {
            id = mDatabase.delete(CacheDBHelper.TABLE_PUSH, whereClause, whereArgs);
        } catch (Exception e) {

        } finally {
            closeDatabase();
        }
        return id;
    }
}
