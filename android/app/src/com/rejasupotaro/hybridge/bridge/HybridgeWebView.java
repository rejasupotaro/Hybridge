package com.rejasupotaro.hybridge.bridge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rejasupotaro.hybridge.R;
import com.rejasupotaro.hybridge.annotation.Bridge;

public class HybridgeWebView extends WebView {
    private static final String DEFAULT_JAVASCRIPT_INTERFACE_NAME = "DeviceBridge";

    private String javascriptInterfaceName = DEFAULT_JAVASCRIPT_INTERFACE_NAME;
    private WebSettings webSettings;
    private String[] allowingDomains;
    private DeviceBridgeProxy deviceBridgeProxy;

    public HybridgeWebView(Context context) {
        super(context);
    }

    public HybridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context.obtainStyledAttributes(attrs, R.styleable.HybridgeWebView));
    }

    public HybridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttributes(context.obtainStyledAttributes(attrs, R.styleable.HybridgeWebView, defStyle, 0));
    }

    private void readAttributes(TypedArray typedArray) {
        String jsiName = typedArray.getString(R.styleable.HybridgeWebView_javascriptInterfaceName);
        if (jsiName != null) {
            javascriptInterfaceName = jsiName;
        }

        allowingDomains =
                typedArray.getString(R.styleable.HybridgeWebView_allowingDomains).split(" ");
        if (allowingDomains == null) throw new SecurityException("Don't use HybridgeWebView without setting domains");

        typedArray.recycle();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init() {
        webSettings = getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setPluginState(PluginState.ON);

        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
    }

    public void registerBridge(Object receiver) {
        Class<?> clazz = receiver.getClass();
        Method[] methods = clazz.getMethods();
        Map<String, Method> bridgeMethodSet = new HashMap<String, Method>();
        for (Method method : methods) {
            Bridge bridgeAnn = method.getAnnotation(Bridge.class);
            if (bridgeAnn != null) {
                bridgeMethodSet.put(bridgeAnn.value(), method);
            }
        }

        Handler handler = new Handler(getContext().getMainLooper());
        deviceBridgeProxy = new DeviceBridgeProxy(this, handler);
        addJavascriptInterface(deviceBridgeProxy, javascriptInterfaceName);

        deviceBridgeProxy.registerBridgeReceiver(receiver, bridgeMethodSet);
    }

    @Override
    public void setWebViewClient(WebViewClient webViewClient) {
        super.setWebViewClient(new WebViewClientProxy(this, webViewClient, allowingDomains));
    }

    public boolean browserBack() {
        if (canGoBack()) {
            goBack();
            return true;
        }
        return false;
    }
    
    public void call(String jsMethodName, String... args) {
        call(null, jsMethodName, args);
    }

    public void call( Callback callback, String jsMethodName, String... args) {
    }
}
