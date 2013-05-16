package com.rejasupotaro.hybridge.utils;

import android.net.Uri;

public final class UriUtils {
    private UriUtils() {}

    public static String deleteLastSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    public static String buildBaseUrl(String urlString) {
        Uri uri = Uri.parse(urlString);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        return scheme + "://" + host + ((port == -1) ? "" : ":" + port);
    }

    public static boolean isValidDomain(String urlString, String[] allowingDomains) {
        return isValidDomain(Uri.parse(urlString), allowingDomains);
    }

    public static boolean isValidDomain(Uri uri, String[] allowingDomains) {
        boolean isValid = false;
        for (String allowingDomain : allowingDomains) {
            if (UriUtils.compareDomain(uri, allowingDomain)) {
                isValid = true;
            }
        }
        return isValid;
    }

    public static boolean compareDomain(Uri uri, String domain) {
        if (domain == null) return false;
        return domain.equals(uri.getHost());
    }
}
