package com.rejasupotaro.hybridge.bridge;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.rejasupotaro.hybridge.bridge.helper.JavaScriptHelper;

public class DeviceBridgeProxy {
    private static final String TAG = DeviceBridgeProxy.class.getSimpleName();

    public static final String CONTAINER_RESULT = "result";
    public static final String CONTAINER_PARAMS = "params";
    public static final String HYBRIDGE_PREFIX = "hybridge.";
    public static final String METHOD_RETURN_TO_WEB = HYBRIDGE_PREFIX + "returnToWeb";
    public static final String METHOD_NOTIFY_TO_WEB = HYBRIDGE_PREFIX + "notifyToWeb";

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

    @JavascriptInterface
    public void notifyToDevice(String data) {
        final String jsonText = decode(data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                String id = null;
                String dest = null;
                try {
                    final JSONObject json = new JSONObject(jsonText);

                    if (json.has("id")) id = json.optString("id");
                    dest = json.getString("dest");
                    if (json.has("params")) {
                        invokeBridgeMethod(id, dest, json.getJSONObject("params"));
                    } else {
                        invokeBridgeMethod(id, dest, null);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "", e);
                }
            }
        });
    }

    @JavascriptInterface
    public void returnToDevice(String data) {
        final String jsonText = decode(data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                String id = null;
                try {
                    final JSONObject json = new JSONObject(jsonText);

                    if (json.has("id")) id = json.optString("id");
                    if (id == null) return;

                    Callback callback = callbacks.get(id);
                    if (callback == null) return;

                    if (json.has("result")) {
                        callback.invoke(json);
                    } else {
                        callback.invoke(null);
                    }
                    callbacks.remove(id);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "", e);
                }
            }
        });
    }

    private String decode(String data) {
        try {
            return URLDecoder.decode(data, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage() + "", e);
        }
        return null;
    }

    private void invokeBridgeMethod(String id, String dest, JSONObject params) {
        try {
            Method method = bridgeMethodSet.get(dest);
            if (method == null) return;

            Object result = null;
            if (params == null) {
                result = method.invoke(bridgeReceiver);
            } else {
                result = method.invoke(bridgeReceiver, params);
            }
            returnToWeb(id, dest, result);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage() + "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage() + "", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage() + "", e);
        }
    }

    private void returnToWeb(String id, String dest, Object result) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("dest", dest);
            json.put(CONTAINER_RESULT, result);
            String js = JavaScriptHelper.makeJavaScript(METHOD_RETURN_TO_WEB, json);
            hybridgeWebView.loadUrl(js);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void notifyToWeb(String dest, JSONObject params) {
        notifyToWeb(null, dest, params);
    }

    public void notifyToWeb(String dest, JSONObject params, Callback callback) {
        String id = seq.incrementAndGet() + "";
        notifyToWeb(id, dest, params);
        callbacks.put(id, callback);
    }

    public void notifyToWeb(String id, String dest, JSONObject params) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("dest", dest);
            json.put(CONTAINER_PARAMS, params);
            String js = JavaScriptHelper.makeJavaScript(METHOD_NOTIFY_TO_WEB, json);
            hybridgeWebView.loadUrl(js);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
