# Hybridge
Hybrid application framework that provides JSON-RPC based communication protocol between web client and native client.

inspired by [Triaina](https://github.com/mixi-inc/triaina)

## Abstract
Hybridge consists of WebView Bridge and Content Preload.

![](https://raw.github.com/takiguchi0817/Hybridge/master/abstract.jpg)

See also [JSON-RPC](http://json-rpc.org/)

## Usage
### WebView Bridge
#### Preparation
```xml
<your.package.name.YourWebView
    android:id="@+id/webview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    hybridge:validDomains="yourdomain.com othervaliddomain.com" />
```

```java
public class YourWebView extends HybridgeWebView {

    public YourWebView(Context context) {
        super(context);
    }

    public YourWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YourWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Activity activity, LoadStateListener listener) {
        this.activity = activity;
        super.registerBridge(this);
        setupWebViewClient();
        ...
    }
}
```

and call yourWebView.init() on activiy created.

**WebView ----> Device ----> WebView**
```java
public class YourWebView extends HybridgeWebView {

    ...

    @Bridge('greet.method.name')
    public String void greet(JSONObject json) {
        try {
            String greetText = "Hello " + json.getString('name');
            Toast.makeText(context, greetText, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e("DEBUG", e.getMessage(), e);
        }
        return "Enjoy development!";
    }

}
```

```javascript
hybridge.call(
    'greet.method.name',
    { name: 'rejasupotaro' },
    function(json) { alert(json.result); });
```

**Device ----> WebView ---> Device**
```javascript
hybridge.register(
    'greet.method.name',
    function(json) {
      alert("Hello" + json.param);
      return "Enjoy development!";
    });
```

```java
JSONOBject json = new JSONObject();
json.put("name", "rejasupotaro");
call("greet.method.name", json, new Callback() {
    @Override invoke(JSONObject json) {
        try {
            Toast.makeText(context, json.getString("result"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e("DEBUG", e.getMessage(), e);
        }
    }
});
```

### Content Preload(WIP)
!!!Preload is not work well due to WebView#loadDataWithBaseUrl's bug for now!!!
```java
public class YourApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Hybridge.initialize(this);
    }

    @Override
    public void onTerminate() {
        Hybridge.dispose();
    }
}
```

and call ```Hybridge.preload(targetUrl, ExpiresTime.ONE_DAY)``` beforehand.

## Tips
### Store/Restore WebView
```java
public class YourActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = (HybridgeWebView) findViewById(R.id.webview);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

        ...
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }

    ...
}
```

### Don't recreate activity on configuration changed.
Add configChanges attribute on your activity.

```xml
<activity
    android:name=".YourActivity"
    android:configChanges="keyboardHidden|orientation|screenSize" >
</activity>
```

### show error page
...
