package com.rejasupotaro.hybridge.bridge;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rejasupotaro.hybridge.Hybridge;
import com.rejasupotaro.hybridge.db.DbCache;
import com.rejasupotaro.hybridge.db.entity.PreloadedContent;
import com.rejasupotaro.hybridge.utils.UriUtils;

public class WebViewClientProxy extends WebViewClient {
    private HybridgeWebView webView;
    private WebViewClient webViewClient;
    private String[] validDomains;

    private long measureStartMillis; // for measuring loading time

    public WebViewClientProxy(HybridgeWebView webView, WebViewClient webViewClient, String[] validDomains) {
        this.webView = webView;
        this.webViewClient = webViewClient;
        this.validDomains = validDomains;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return webViewClient.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        measureStartMillis = System.currentTimeMillis(); // for measuring loading time
        if (!UriUtils.isValidDomain(url, validDomains)) {
            throw new SecurityException("cannot load " + url); // FIXME define specific error
        }

        String formattedUrl = UriUtils.appendSlashIfNecessary(url);
        Map<String, PreloadedContent> cacheMap = DbCache.getContentMap();
        if (cacheMap.containsKey(formattedUrl)) {
            PreloadedContent cacheContent = cacheMap.get(formattedUrl);
            if (!cacheContent.isExpired()) {
                loadFromCache(webView, cacheContent, formattedUrl);
                // FIXME call webViewClient method
            } else {
                Hybridge.drop(formattedUrl);
                webViewClient.onPageStarted(view, formattedUrl, favicon);
            }
        } else {
            webViewClient.onPageStarted(view, formattedUrl, favicon);
        }
    }

    private void loadFromCache(WebView webView, PreloadedContent cacheContent, String failUrl) {
        webView.loadDataWithBaseURL(
                cacheContent.getBaseUrl(),
                cacheContent.getContent(),
                cacheContent.getMimetype(),
                cacheContent.getEncoding(),
                failUrl);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // for measuring loading time
        Log.d("DEBUG", url + " : " + String.valueOf(System.currentTimeMillis() - measureStartMillis));
        measureStartMillis = 0;

        webViewClient.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        webViewClient.onLoadResource(view, url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        webViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        webViewClient.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        webViewClient.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        webViewClient.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        webViewClient.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return webViewClient.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        webViewClient.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        webViewClient.onScaleChanged(view, oldScale, newScale);
    }
}
