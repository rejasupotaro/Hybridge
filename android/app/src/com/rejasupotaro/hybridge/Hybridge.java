package com.rejasupotaro.hybridge;

import android.app.Application;

import com.rejasupotaro.hybridge.db.DbCache;

public class Hybridge {
    public static synchronized void initialize(Application application) {
        DbCache.initialize(application);
    }

    public static void dispose() {
        DbCache.dispose();
    }

}
