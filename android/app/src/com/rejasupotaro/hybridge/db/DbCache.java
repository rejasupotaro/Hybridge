package com.rejasupotaro.hybridge.db;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rejasupotaro.hybridge.utils.CloseableUtils;

public class DbCache {
    private static final String TAG = DbCache.class.getName();

    private static boolean sIsInitialized = false;
    private static Context sContext;
    private static DatabaseHelper sDatabaseHelper;
    private static final AsyncHttpClient sClient = new AsyncHttpClient();

    public static synchronized void initialize(Application application) {
        if (sIsInitialized) {
            Log.v(TAG, "Hybridge is already initialized.");
            return;
        }

        sContext = application;
        sDatabaseHelper = new DatabaseHelper(sContext);

        Cursor cursor = null;
        try {
            cursor = sDatabaseHelper.getAllContents();
            Log.d("DEBUG", "count: " + cursor.getCount());
        } finally {
            CloseableUtils.close(cursor);
        }

        sIsInitialized = true;
    }

    public static synchronized void dispose() {
        sDatabaseHelper = null;
        sIsInitialized = false;
    }

    public static void preload(final String url) {
        sClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                sDatabaseHelper.savePreloadContent(url, response);
            }
        });
    }
}
