package com.rejasupotaro.hybridge.bridge;

import org.json.JSONObject;

public interface Callback {
    public void invoke(JSONObject json);
}
