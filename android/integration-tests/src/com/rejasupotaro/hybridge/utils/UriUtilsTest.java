package com.rejasupotaro.hybridge.utils;

import android.net.Uri;
import android.test.AndroidTestCase;

public class UriUtilsTest extends AndroidTestCase {
    public void testAppendSlashIfNecessary() {
        assertEquals("http://rejasupotaro.com/", UriUtils.appendSlashIfNecessary("http://rejasupotaro.com/"));
        assertEquals("http://rejasupotaro.com/", UriUtils.appendSlashIfNecessary("http://rejasupotaro.com/"));
    }

    public void testBuildBaseUrl() {
        assertEquals("http://rejasupotaro.com/", UriUtils.buildBaseUrl("http://rejasupotaro.com/images/1"));
        assertEquals("http://rejasupotaro.com/", UriUtils.buildBaseUrl("http://rejasupotaro.com"));
    }

    public void testIsValidDomain() {
        String[] validDomains = new String[]{"rejasupotaro.com", "rejamotion.com"};
        assertTrue(UriUtils.isValidDomain("http://rejasupotaro.com/images/1", validDomains));
        assertTrue(UriUtils.isValidDomain("http://rejamotion.com/images/2", validDomains));
        assertFalse(UriUtils.isValidDomain("http://reja.com/images/3", validDomains));
    }

    public void testCompareDomain() {
        Uri uri = Uri.parse("http://rejasupotaro.com");
        assertTrue(UriUtils.compareDomain(uri, "rejasupotaro.com"));
        assertFalse(UriUtils.compareDomain(uri, "rejamotion.com"));
    }
}
