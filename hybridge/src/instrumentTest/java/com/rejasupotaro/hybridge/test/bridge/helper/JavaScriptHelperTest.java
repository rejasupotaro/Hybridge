package com.rejasupotaro.hybridge.test.bridge.helper;

import com.rejasupotaro.hybridge.bridge.helper.JavaScriptHelper;

import org.json.JSONObject;

import android.test.AndroidTestCase;

public class JavaScriptHelperTest extends AndroidTestCase {

    public void testMakeJavaScript() throws Exception {
        String func = "func";
        JSONObject json = new JSONObject();
        json.put("first", "one");
        json.put("second", "two");

        String javascript = JavaScriptHelper.makeJavaScript(func, json);
        assertEquals(
                "javascript: func('%7B%22second%22%3A%22two%22%2C%22first%22%3A%22one%22%7D');",
                javascript);
    }
}

