package vn.com.nimbus.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    public static final String READALBE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(READALBE_DATETIME_FORMAT);
        return dateTime.format(formatter);
    }
}
