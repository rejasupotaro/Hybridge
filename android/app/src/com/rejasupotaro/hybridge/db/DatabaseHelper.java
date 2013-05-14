package com.rejasupotaro.hybridge.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rejasupotaro.hybridge.utils.SQLiteUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "hybridge_webview_cache";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "prefetched_contents";
    private static final String QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            "_id INTEGER PRIMARY KEY," +
            "url TEXT," +
            "base_url TEXT," + 
            "lastmodify TEXT," +
            "expires INTEGER," +
            "mimetype TEXT," +
            "encoding TEXT," +
            "UNIQUE (url) ON CONFLICT REPLACE" +
            "); CREATE INDEX cacheUrlIndex ON cache (url);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        executePragmas(db);
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        executePragmas(db);
        executeCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        executePragmas(db);
        executeCreate(db);
    }

    private void executePragmas(SQLiteDatabase db) {
        if (SQLiteUtils.FOREIGN_KEYS_SUPPORTED) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    private void executeCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(QUERY_CREATE_TABLE);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
}
