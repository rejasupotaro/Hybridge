package com.rejasupotaro.hybridge.db;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rejasupotaro.hybridge.db.entity.PreloadedContent;
import com.rejasupotaro.hybridge.utils.UriUtils;

public class DbCache {
    private static final String TAG = DbCache.class.getName();

    private static boolean sIsInitialized = false;
    private static Context sContext;
    private static DatabaseHelper sDatabaseHelper;
    private static final AsyncHttpClient sClient = new AsyncHttpClient();
    private static Map<String, PreloadedContent> sContentCacheMap = new HashMap<String, PreloadedContent>();

    public static synchronized void initialize(Application application) {
        if (sIsInitialized) {
            Log.v(TAG, "Hybridge is already initialized.");
            return;
        }

        sContext = application;
        sDatabaseHelper = new DatabaseHelper(sContext);

        loadPreloadedContents();

        sIsInitialized = true;
    }

    public static Map<String, PreloadedContent> getContentCacheMap() {
        return sContentCacheMap;
    }

    private static void loadPreloadedContents() {
        Cursor c = null;
        try {
            c = sDatabaseHelper.getAllContents();
            if (c.moveToFirst()) {
                do {
                    PreloadedContent cacheContent = new PreloadedContent(c);
                    sContentCacheMap.put(cacheContent.getUrl(), cacheContent);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static void dispose() {
        sDatabaseHelper = null;
        sIsInitialized = false;
    }

    public static void preload(String url, final ExpiresTime expires) {
        final String formattedUrl = UriUtils.appendSlashIfNecessary(url);
        sClient.get(formattedUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                sDatabaseHelper.savePreloadContent(formattedUrl, response, expires);
                Cursor c = sDatabaseHelper.getPreloadedContent(formattedUrl);
                sContentCacheMap.put(formattedUrl, new PreloadedContent(c));
            }

            @Override
            public void onFailure(Throwable e, String response) {
                // TODO impl me
            }
        });
    }

    public static void drop(String url) {
        sDatabaseHelper.deleteContent(url);
        if (sContentCacheMap.containsKey(url)) {
            sContentCacheMap.remove(url);
        }
    }
}
