package com.rejasupotaro.hybridge.db.entity;

import java.lang.reflect.Field;

import android.database.Cursor;

import com.rejasupotaro.hybridge.annotation.Attribute;
import com.rejasupotaro.hybridge.annotation.Table;

@Table("preload_content")
public class CacheContent {
    @Attribute("INTEGER PRIMARY KEY") public long _id;
    @Attribute("TEXT") public String url;
    @Attribute("TEXT") public String base_url;
    @Attribute("TEXT") public String content;
    @Attribute("INTEGER") public long expires;
    @Attribute("TEXT") public String minetype;
    @Attribute("TEXT") public String encoding;

    private static String sTableName;

    public CacheContent(Cursor c) {
        // TODO impl me
    }

    public static String getTableName() {
        if (sTableName != null) return sTableName;

        Table table = (Table) CacheContent.class.getAnnotation(Table.class);
        if (table == null) throw new RuntimeException("FIXME"); // FIXME

        sTableName = table.value();
        return sTableName;
    }

    public static String toQuery() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE ").append(getTableName()).append("(");
        Field[] fields = CacheContent.class.getDeclaredFields();
        for (Field field : fields) {
            Attribute attr = (Attribute) field.getAnnotation(Attribute.class);
            if (attr == null) continue;

            stringBuffer.append(field.getName()).append(" ").append(attr.value()).append(",");
        }
        stringBuffer.append("UNIQUE (url) ON CONFLICT REPLACE);");
        stringBuffer.append("CREATE INDEX cacheUrlIndex ON cache (url);");

        return stringBuffer.toString();
    }
}
