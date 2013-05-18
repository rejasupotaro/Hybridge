package com.rejasupotaro.hybridge.db;

public enum ExpiresTime {
    ONE_DAY(86340000),
    HARF_A_DAY(43140000);

    private long millis;

    private ExpiresTime(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }
}
