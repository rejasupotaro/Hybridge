package com.rejasupotaro.hybridge.db;

public enum ExpiresTime {
    ONE_DAY((1000 * 60 * 60 * 24) - 1000 * 60),
    HARF_A_DAY((1000 * 60 * 60 * 12) - 1000 * 60);

    private long millis;

    private ExpiresTime(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }
}
