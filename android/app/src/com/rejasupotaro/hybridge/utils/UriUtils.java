package com.rejasupotaro.hybridge.utils;

import android.net.Uri;

public final class UriUtils {
    private UriUtils() {}

    public static String buildBaseUrl(String urlString) {
        Uri uri = Uri.parse(urlString);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        return scheme + "://" + host + ((port == -1) ? "" : ":" + port);
    }

    public static boolean compareDomain(Uri uri, String domain) {
        if (domain == null) {
            return false;
        }

        String host = uri.getHost();
        int d = host.length() - domain.length();
        if (d < 0) {
            return false;
        } else if (d > 0) {
            if (host.substring(d - 1).charAt(0) != '.')
                return false;
        }

        return domain.equals(host.substring(d));
    }
}
