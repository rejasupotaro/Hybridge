package com.rejasupotaro.hybridge.db.entity;

import java.lang.reflect.Field;

import android.database.Cursor;
import android.util.Log;

import com.rejasupotaro.hybridge.annotation.Column;
import com.rejasupotaro.hybridge.annotation.Table;

@Table("preload_content")
public class PreloadedContent {
    private static final String TAG = PreloadedContent.class.getName();

    // FIXME define table schema more simply
    @Column({"_id", "INTEGER PRIMARY KEY"}) public long id;
    @Column({"url", "TEXT"}) public String url;
    @Column({"base_url", "TEXT"}) public String baseUrl;
    @Column({"content", "TEXT"}) public String content;
    @Column({"expires", "INTEGER"}) public long expires;
    @Column({"mimetype", "TEXT"}) public String mimetype;
    @Column({"encoding", "TEXT"}) public String encoding;
    @Column({"created_at", "TEXT"}) public long createdAt;

    private static String sTableName;

    @SuppressWarnings("unused")
    private PreloadedContent() {}

    public PreloadedContent(Cursor c) {
        if (c.moveToFirst()) {
            do {
                // FIXME don't hard code
                url = c.getString(c.getColumnIndex("url"));
                baseUrl = c.getString(c.getColumnIndex("base_url"));
                content = c.getString(c.getColumnIndex("content"));
                expires = c.getLong(c.getColumnIndex("expires"));
                mimetype = c.getString(c.getColumnIndex("mimetype"));
                encoding = c.getString(c.getColumnIndex("encoding"));
                createdAt = c.getLong(c.getColumnIndex("created_at"));
            } while (c.moveToNext());
        }
    }

    public String getUrl() { return url; }
    public String getBaseUrl() { return baseUrl; }
    public String getContent() { return content; }
    public String getMimetype() { return mimetype; }
    public String getEncoding() { return encoding; }
    public boolean isExpired() {
        return expires + createdAt < System.currentTimeMillis();
    }

    public static String getTableName() {
        if (sTableName != null) return sTableName;

        Table table = (Table) PreloadedContent.class.getAnnotation(Table.class);
        if (table == null) throw new IllegalStateException();

        sTableName = table.value();
        return sTableName;
    }

    public static String toQuery() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stringBuffer.append("CREATE TABLE ").append(getTableName()).append("(");
            Field[] fields = PreloadedContent.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Column column = (Column) field.getAnnotation(Column.class);
                if (column == null) continue;

                stringBuffer.append(column.value()[0]).append(" ").append(column.value()[1]).append(",");
            }
            stringBuffer.append("UNIQUE (url) ON CONFLICT REPLACE);");
            stringBuffer.append("CREATE INDEX cacheUrlIndex ON cache (url);");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return stringBuffer.toString();
    }
}
