package com.lynp.ui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * Created by nmg on 16/1/30.
 */

public class SQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "lynp.db";
    private static final int  DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "items";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db)
    {
        Log.e("create", "开始创建数据库");

        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + "id TEXT,"
                + "name TEXT,"
                + "photo TEXT,"
                + "price FLOAT,"
                + "count INTEGER"
                + ")";
        db.execSQL(sql);

        Log.e("create","数据库创建成功");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        this.onCreate(db);
        System.out.println("数据库已经更新");
        /**
         * 在此添加更新数据库是要执行的操作
         */
    }

}