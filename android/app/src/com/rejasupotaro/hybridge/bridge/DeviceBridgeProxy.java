package com.rejasupotaro.hybridge.bridge;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Handler;
import android.util.Log;

public class DeviceBridgeProxy {

    private static final String TAG = DeviceBridgeProxy.class.getSimpleName();

    private HybridgeWebView hybridgeWebView;
    private Handler handler;
    private Object bridgeReceiver;
    private Map<String, Method> bridgeMethodSet = new HashMap<String, Method>();
    private AtomicInteger seq = new AtomicInteger();
    private Map<String, Callback> callbacks = new ConcurrentHashMap<String, Callback>();

    public DeviceBridgeProxy(HybridgeWebView hybridgeWebView, Handler handler) {
        this.hybridgeWebView = hybridgeWebView;
        this.handler = handler;
    }

    public void registerBridgeReceiver(Object bridgeReceiver, Map<String, Method> bridgeMethodSet) {
        this.bridgeReceiver = bridgeReceiver;
        this.bridgeMethodSet = bridgeMethodSet;
    }

    // WebView => Device
    public void notifyToDevice(String data) {
        final String jsonText = decode(data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                String id = null;
                String dest = null;
                try {
                    final JSONObject json = new JSONObject(jsonText);

                    if (json.has("id"))
                        id = json.optString("id");
                    dest = json.getString("dest");
                    if (json.has("params")) {
                        invoke(id, dest, json.getJSONObject("params"));
                    } else {
                        invoke(id, dest, null);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "", e);
                }
            }
        });
    }

    private String decode(String data) {
        try {
            return URLDecoder.decode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage() + "", e);
        }
        return null;
    }

    private void invoke(String id, String dest, JSONObject params) {
        try {
            Method method = bridgeMethodSet.get(dest);
            if (method == null) return;

            if (params == null) {
                method.invoke(bridgeReceiver);
            } else {
                method.invoke(bridgeReceiver, params);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage() + "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage() + "", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage() + "", e);
        }
    }
}
