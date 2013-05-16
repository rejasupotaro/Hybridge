package com.rejasupotaro.hybridge.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rejasupotaro.hybridge.utils.SQLiteUtils;
import com.rejasupotaro.hybridge.utils.UriUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME =  "com.rejasupotaro.hybridge.webview_cache";
    public static final String TABLE_NAME = "preload_content";
    private static final int DB_VERSION = 1;
    private static final String QUERY_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            "_id INTEGER PRIMARY KEY," +
            "url TEXT," +
            "base_url TEXT," +
            "content TEXT," +
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
        Log.d("DEBUG", "onOpen");
        executePragmas(db);
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DEBUG", "onCreate");
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

    public void savePreloadContent(String url, String content) {
        String baseUrl = UriUtils.buildBaseUrl(url);

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                "INSERT INTO " + TABLE_NAME + "(url, base_url, content, expires, mimetype, encoding) " +
                "values (?, ?, ?, ?, ?, ?);");

        statement.bindString(1, url);
        statement.bindString(2, baseUrl);
        statement.bindString(3, content);
        statement.bindLong(4, System.currentTimeMillis());
        statement.bindString(5, "text/html");
        statement.bindString(6, "utf-8");

        statement.executeInsert();
    }

    public Cursor getAllContents() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + ";", null);
    }
}
