package com.lynp.ui.db;

/**
 * Created by niuminguo on 16/1/31.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.content.ContentValues;

import com.lynp.ui.data.ItemDetailEntry;

public class PostsDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "lynp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "items";

//    // Table Names
//    private static final String TABLE_POSTS = "posts";
//    private static final String TABLE_USERS = "users";
//
//    // Post Table Columns
//    private static final String KEY_POST_ID = "id";
//    private static final String KEY_POST_USER_ID_FK = "userId";
//    private static final String KEY_POST_TEXT = "text";
//
//    // User Table Columns
//    private static final String KEY_USER_ID = "id";
//    private static final String KEY_USER_NAME = "userName";
//    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    private static PostsDatabaseHelper sInstance;

    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
//    @Override
//    public void onConfigure(SQLiteDatabase db) {
//        super.onConfigure(db);
//        db.setForeignKeyConstraintsEnabled(true);
//    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + "id TEXT,"
                + "name TEXT,"
                + "photo TEXT,"
                + "price FLOAT,"
                + "count INTEGER,"
                + ")";
        db.execSQL(sql);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(sql);
            this.onCreate(db);
        }
    }

    public void addItem(ItemDetailEntry entry) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", entry.id);
        values.put("name", entry.name);
        values.put("photo", entry.photo);
        values.put("price", entry.price);
        values.put("count", entry.count);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


}