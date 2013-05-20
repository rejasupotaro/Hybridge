package com.rejasupotaro.hybridge.utils;

import android.net.Uri;

public final class UriUtils {
    private UriUtils() {}

    public static String appendSlashIfNecessary(String url) {
        if (!url.endsWith("/")) {
            return url + "/";
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

    public static boolean isValidDomain(String urlString, String[] validDomains) {
        return isValidDomain(Uri.parse(urlString), validDomains);
    }

    public static boolean isValidDomain(Uri uri, String[] validDomains) {
        boolean isValid = true;
        for (String validDomain : validDomains) {
            if (UriUtils.compareDomain(uri, validDomain)) {
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
