package com.rejasupotaro.hybridge.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.rejasupotaro.hybridge.db.entity.CacheContent;
import com.rejasupotaro.hybridge.utils.SQLiteUtils;
import com.rejasupotaro.hybridge.utils.UriUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME =  "com.rejasupotaro.hybridge.webview_cache.db";
    private static final int DB_VERSION = 1;

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
            db.execSQL(CacheContent.toQuery());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void savePreloadContent(String url, String content) {
        String baseUrl = UriUtils.buildBaseUrl(url);

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                "INSERT INTO " + CacheContent.getTableName() +
                "(url, base_url, content, expires, mimetype, encoding) " +
                "values (?, ?, ?, ?, ?, ?);");

        statement.bindString(1, url);
        statement.bindString(2, baseUrl);
        statement.bindString(3, content);
        statement.bindLong(4, System.currentTimeMillis());
        statement.bindString(5, "text/html"); // TODO enable to save images and scripts
        statement.bindString(6, "utf-8"); // TODO get encode from http client

        statement.executeInsert();
    }

    public Cursor getAllContents() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + CacheContent.getTableName() + ";", null);
    }
}
