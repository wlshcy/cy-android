package com.lynp.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lynp.ui.data.ItemDetailEntry;

/**
 * Created by nmg on 16/1/30.
 */
public class DataBase2 extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="lynp.db";
    private static final int  DATABASE_VERSION=17;//更改版本后数据库将重新创建
    private static final String TABLE_NAME="items";

    //调用父类构造器
//    public DataBase2(Context context, String name, CursorFactory factory,
//                    int version) {
    public DataBase2(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
     * 重写onCreate方法，调用execSQL方法创建表
     * */

    /**
     * 该函数是在第一次创建数据库时执行，只有当其调用getreadabledatebase()
     * 或者getwrittleabledatebase()而且是第一创建数据库是才会执行该函数
     */

    public void onCreate(SQLiteDatabase db)
    {

        // TODO Auto-generated method stub
        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + "id TEXT,"
                + "name TEXT,"
                + "photo TEXT,"
                + "price FLOAT,"
                + "count INTEGER,"
                + ")";
        db.execSQL(sql);
        Log.e("create", "数据库创建成功");
    }
    /**
     *数据库更新函数，当数据库更新时会执行此函数
     */
    //当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        this.onCreate(db);
        // TODO Auto-generated method stub
        System.out.println("数据库已经更新");
        /**
         * 在此添加更新数据库是要执行的操作
         */
    }

    public void addItem(ItemDetailEntry entry) {

        ContentValues values = new ContentValues();
        values.put("id", entry.id);
        values.put("name", entry.id);
        values.put("photo", entry.id);
        values.put("price", entry.id);
        values.put("count", entry.id);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteItem(String id) {

        String sql = "DELETE FROM " + TABLE_NAME +" WHERE id=?";
        Object args[]=new Object[]{id};

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sql, args);
        db.close();
    }

    public void update(String id,int count)
    {
        String sql = "UPDATE " + TABLE_NAME + " SET count=? WHERE id=?";
        Object args[]=new Object[]{count,id};

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sql, args);
        db.close();
    }

}
