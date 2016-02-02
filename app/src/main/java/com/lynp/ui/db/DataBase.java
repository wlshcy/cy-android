package com.lynp.ui.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import com.lynp.ui.data.ItemDetailEntry;

import android.content.Context;

/**
 * Created by niuminguo on 16/1/30.
 */
public class DataBase {

    private SQLiteHelper helper = null;
    private static final String TABLE_NAME="items";

    public DataBase(Context context) {
        helper = new SQLiteHelper(context);
    }

    public void addItem(ItemDetailEntry entry) {

        ContentValues values = new ContentValues();
        values.put("id", entry.id);
        values.put("name", entry.name);
        values.put("photo", entry.photo);
        values.put("price", entry.price);
        values.put("count", entry.count);

        SQLiteDatabase db = helper.getWritableDatabase();

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteItem(String id) {

        String sql = "DELETE FROM " + TABLE_NAME +" WHERE id=?";
        Object args[]=new Object[]{id};

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(sql, args);
        db.close();
    }

    public void updateCount(String id,int count)
    {
        String sql = "UPDATE " + TABLE_NAME + " SET count=? WHERE id=?";
        Object args[]=new Object[]{count,id};

        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(sql, args);
        db.close();
    }

    public List<ItemDetailEntry> getItems()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        List<ItemDetailEntry> all = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor result = db.rawQuery(sql, null);
        if (result.moveToFirst()) {
            do {
                ItemDetailEntry entry = new ItemDetailEntry();
                entry.id = result.getString(result.getColumnIndex("id"));
                entry.name = result.getString(result.getColumnIndex("name"));
                entry.photo = result.getString(result.getColumnIndex("photo"));
                entry.price = result.getFloat(result.getColumnIndex("price"));
                entry.count = result.getInt(4);

                all.add(entry);
            } while(result.moveToNext());
        }
        db.close();
        return all;
    }

    //返回指定ID的列表
//    public int getItem(String id)
//    {
//        int num=-1;
//        List<string> all = new ArrayList<string>(); //此时只是String
//        String sql = "SELECT ＊ FROM " + TABLE_NAME + " where id=?" ;
//        String args[] = new String[]{String.valueOf(id)};
//        Cursor result = this.db.rawQuery(sql, args);
//        for(result.moveToFirst();!result.isAfterLast();result.moveToNext()  )
//        {
//            num=result.getInt(0);
//        }
//
//        Log.e("database", "图片状态state"+ String.valueOf(num));
//        this.db.close();
//        return num;
//    }

    //判断插入数据的ID是否已经存在数据库中。
    public boolean item_exits(String id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql="SELECT id from " + TABLE_NAME + " where id = ?";
        String args[]=new String[]{id};
        Cursor result=db.rawQuery(sql,args);


        if(result.getCount()==0)
        {
            db.close();
            return false;
        }
        db.close();
        return true;

    }
}