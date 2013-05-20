package com.rejasupotaro.hybridge.utils;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }
}
