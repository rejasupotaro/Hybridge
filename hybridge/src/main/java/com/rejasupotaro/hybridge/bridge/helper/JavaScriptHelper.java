package com.rejasupotaro.hybridge.bridge.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.util.Log;

public class JavaScriptHelper {
    private static final String TAG = JavaScriptHelper.class.getName();

    public static String makeJavaScript(String func, JSONObject json) {
        try {
            String encodedJsonString = URLEncoder.encode(json.toString(), "UTF-8").replace("+", "%20");
            return makeJavaScript(func, encodedJsonString);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage() + "", e);
            return "";
        }
    }

    public static String makeJavaScript(String func, String... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("javascript: ");
        builder.append(func);
        builder.append('(');
        for (int i = 0; i < args.length; ++i) {
            builder.append('\'');
            builder.append(args[i]);
            builder.append('\'');

            if (args.length - 1 != i) {
                builder.append(',');
            }
        }
        builder.append(");");

        return builder.toString();
    }
}
