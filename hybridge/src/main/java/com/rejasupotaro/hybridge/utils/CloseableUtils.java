package com.rejasupotaro.hybridge.utils;

import java.io.Closeable;
import java.io.IOException;

import android.util.Log;

public final class CloseableUtils {
    private static final String TAG = CloseableUtils.class.getName();

    private CloseableUtils() {}

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
