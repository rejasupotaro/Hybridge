package com.rejasupotaro.hybridge.bridge.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

public class JavaScriptHelper {
    public static String makeJavaScript(String func, JSONObject json) throws UnsupportedEncodingException {
        String encodedJsonString = URLEncoder.encode(json.toString(), "UTF-8").replace("+", "%20");
        return makeJavaScript(func, encodedJsonString);
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
