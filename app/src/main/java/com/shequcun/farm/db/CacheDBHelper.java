package com.shequcun.farm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 缓存数据库工具类
 */
public class CacheDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "cache.db";
    private final static int DATABASE_VERSION = 1;
    public final static String TABLE_CACHE = "datacache";
    public final static String TABLE_PUSH = "push";

    public CacheDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 数据缓存
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CACHE
                + "(key text primary key, data BLOB, updatetime text);");
        /**
         * 推送
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PUSH
                + "(_id VARCHAR,push_json VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

    public void Close() {
        if (null != GetWritableDatabase())
            GetWritableDatabase().close();
        close();
    }

    public SQLiteDatabase GetReadableDatabase() {
        SQLiteDatabase db = null;
        try {
            // 由于getReadableDatabase 获取的DB对象会被 getWritableDatabase 关闭,
            // 所以统一用getWritableDatabase
            db = getWritableDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return db;
    }

    public SQLiteDatabase GetWritableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return db;
    }
}
