package com.rejasupotaro.hybridge.bridge;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rejasupotaro.hybridge.Hybridge;
import com.rejasupotaro.hybridge.db.DbCache;
import com.rejasupotaro.hybridge.db.entity.CacheContent;
import com.rejasupotaro.hybridge.utils.UriUtils;

public class WebViewClientProxy extends WebViewClient {
    private HybridgeWebView webView;
    private WebViewClient webViewClient;
    private String[] allowingDomains;

    public WebViewClientProxy(HybridgeWebView webView, WebViewClient webViewClient, String[] allowingDomains) {
        this.webView = webView;
        this.webViewClient = webViewClient;
        this.allowingDomains = allowingDomains;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return webViewClient.shouldOverrideUrlLoading(view, url);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (!UriUtils.isValidDomain(url, allowingDomains)) {
            throw new SecurityException("cannot load " + url); // FIXME define specific error
        }

        Map<String, CacheContent> cacheMap = DbCache.getContentMap();
        String formattedUrl = UriUtils.deleteLastSlash(url);
        if (cacheMap.containsKey(formattedUrl)) {
            CacheContent cacheContent = cacheMap.get(formattedUrl);
            if (!cacheContent.isExpired()) {
                loadFromCache(webView, cacheContent, formattedUrl); // FIXME call to webViewClient
            } else {
                Hybridge.drop(formattedUrl);
            }
        }

        webViewClient.onPageStarted(view, formattedUrl, favicon);
    }

    private void loadFromCache(WebView webView, CacheContent cacheContent, String failUrl) {
        webView.loadDataWithBaseURL(
                cacheContent.getBaseUrl(),
                cacheContent.getContent(),
                cacheContent.getMimetype(),
                cacheContent.getEncoding(),
                failUrl);
    }

    public void onPageFinished(WebView view, String url) {
        webViewClient.onPageFinished(view, url);
    }

    public void onLoadResource(WebView view, String url) {
        webViewClient.onLoadResource(view, url);
    }

    @SuppressWarnings("deprecation")
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        webViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        webViewClient.onReceivedError(view, errorCode, description, failingUrl);
    }

    public boolean equals(Object o) {
        return webViewClient.equals(o);
    }

    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        webViewClient.onFormResubmission(view, dontResend, resend);
    }

    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        webViewClient.doUpdateVisitedHistory(view, url, isReload);
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        webViewClient.onReceivedSslError(view, handler, error);
    }

    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return webViewClient.shouldOverrideKeyEvent(view, event);
    }

    public int hashCode() {
        return webViewClient.hashCode();
    }

    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        webViewClient.onUnhandledKeyEvent(view, event);
    }

    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        webViewClient.onScaleChanged(view, oldScale, newScale);
    }

    public String toString() {
        return webViewClient.toString();
    }
}
