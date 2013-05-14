package com.rejasupotaro.hybridge.db;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rejasupotaro.hybridge.Hybridge;

public class DbCache {
    private static final String TAG = Hybridge.class.getSimpleName();
    private static boolean sIsInitialized = false;
    private static Context sContext;
    private static DatabaseHelper sDatabaseHelper;

    public static synchronized void initialize(Application application) {
        if (sIsInitialized) {
            Log.v(TAG, "Hybridge is already initialized.");
            return;
        }

        sContext = application;
        sDatabaseHelper = new DatabaseHelper(sContext);
        openDatabase();

        sIsInitialized = true;
    }

    public static synchronized void dispose() {
        closeDatabase();
        sDatabaseHelper = null;
        sIsInitialized = false;
    }

    public static synchronized SQLiteDatabase openDatabase() {
        return sDatabaseHelper.getWritableDatabase();
    }

    public static synchronized void closeDatabase() {
        sDatabaseHelper.close();
    }
}
