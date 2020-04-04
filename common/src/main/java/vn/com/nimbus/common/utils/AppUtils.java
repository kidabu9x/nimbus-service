package vn.com.nimbus.common.utils;

import java.util.Map;
import java.util.TreeMap;

public class AppUtils {
    public static String headerOption(Map<String, String> headers, String key) {
        if (key == null) return null;
        if (headers.get(key) != null) return headers.get(key);
        Map<String, String> headerModify = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headerModify.putAll(headers);
        return headerModify.get(key.toLowerCase());
    }
}
