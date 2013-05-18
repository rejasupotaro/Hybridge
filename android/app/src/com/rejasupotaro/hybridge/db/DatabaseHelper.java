package com.rejasupotaro.hybridge.db;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rejasupotaro.hybridge.db.entity.PreloadedContent;
import com.rejasupotaro.hybridge.utils.ExpiresTime;
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
            db.execSQL(PreloadedContent.toQuery());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void savePreloadContent(String url, String content, ExpiresTime expires) {
        String baseUrl = UriUtils.buildBaseUrl(url);

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                "INSERT INTO " + PreloadedContent.getTableName() +
                "(url, base_url, content, expires, mimetype, encoding, created_at) " +
                "values (?, ?, ?, ?, ?, ?, ?);");

        statement.bindString(1, url);
        statement.bindString(2, baseUrl);
        statement.bindString(3, content);
        statement.bindLong(4, expires.getMillis());
        statement.bindString(5, "text/html"); // TODO enable to save images and scripts
        statement.bindString(6, HTTP.UTF_8); // TODO get encode from http client
        statement.bindLong(7, System.currentTimeMillis()); // TODO get encode from http client

        statement.executeInsert();
        Log.d("DEBUG", "statement: " + statement.toString());
    }

    public Cursor getAllContents() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PreloadedContent.getTableName() + ";", null);
    }

    public void deleteContent(String key) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(PreloadedContent.getTableName(), "url = ?", new String[]{key});
    }
}
