package com.rejasupotaro.hybridge;

import android.app.Application;

import com.rejasupotaro.hybridge.db.DbCache;
import com.rejasupotaro.hybridge.utils.ExpiresTime;

public class Hybridge {
    public static void initialize(Application application) {
        DbCache.initialize(application);
    }

    public static void dispose() {
        DbCache.dispose();
    }

    public static void preload(String url, ExpiresTime expires) {
        DbCache.preload(url, expires);
    }

    public static void drop(String url) {
        DbCache.drop(url);
    }

    // TODO impl setUserAgent method
}
