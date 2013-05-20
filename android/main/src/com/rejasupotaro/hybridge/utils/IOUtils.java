package com.rejasupotaro.hybridge.utils;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public final class IOUtils {
    private static final String TAG = IOUtils.class.getName();

    private IOUtils() {}

    public static String streamToString(InputStream in) {
        StringBuffer stringBuffer = new StringBuffer();
        byte[] line = new byte[1024];
        int size;
        try {
            while(true) {
                size = in.read(line);
                if(size <= 0)
                    break;
                stringBuffer.append(new String(line));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            CloseableUtils.close(in);
        }
        return stringBuffer.toString();
    }
}
