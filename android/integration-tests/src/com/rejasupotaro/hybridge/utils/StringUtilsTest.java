package com.rejasupotaro.hybridge.utils;

import android.test.AndroidTestCase;

public class StringUtilsTest extends AndroidTestCase {
    public void testIsEmpty() {
        assertFalse(StringUtils.isEmpty("not empty"));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(null));
    }
}
