package vn.com.nimbus.common.utils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


public class Util {
    private Util() {
    }

    public static String generateCode() {
        return UUIDUtils.generateUUID();
    }

    public static String generateRequestId() {
        return UUIDUtils.generateUUID();
    }

    public static boolean fileExists(String path) {
        if (Objects.isNull(path) || path.trim().isEmpty()) {
            return false;
        }

        File f = new File(path);
        return f.exists();
    }

    public static String getFileNameWithoutExt(String path) {
        int pos = path.lastIndexOf('.');
        if (pos > 0) {
            return path.substring(0, pos);
        } else {
            return "";
        }
    }

    public static String getInsensitiveValue(Map<String, String> map, String key) {
        if (key == null) return null;
        if (map.get(key) != null) return map.get(key);
        Map<String, String> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(map);
        return treeMap.get(key.toLowerCase());
    }

    public static String getFileExtension(String filename) {
        var dotPos = filename.lastIndexOf('.');
        if (dotPos > -1) {
            return filename.substring(dotPos);
        }
        return "";
    }

    public static Date fromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date fromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Long localDateTimeToEpoch(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static Long localDateTimeToEpochMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Long localDateToEpochMillis(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String toString(Object value) {
        return Objects.toString(value, "");
    }

    public static String getValueFromMap(Map<String, Object> map, String key) {
        var dotIndex = key.indexOf('.');
        if (dotIndex > -1) {
            var newKey = StringUtils.substring(key, 0, dotIndex);
            var childKey = StringUtils.substring(key, dotIndex + 1);
            var parentMap = map.getOrDefault(newKey, null);
            return getValueFromMap((Map<String, Object>) parentMap, childKey);
        }
        var value = map.getOrDefault(key, null);
        return value == null ? null : value.toString();
    }
}
