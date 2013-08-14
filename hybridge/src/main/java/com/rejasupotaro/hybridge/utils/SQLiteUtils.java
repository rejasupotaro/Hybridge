package com.rejasupotaro.hybridge.utils;

import android.os.Build;

public final class SQLiteUtils {
    private SQLiteUtils() {}

    public static final boolean FOREIGN_KEYS_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;

}
