package vn.com.nimbus.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class DateToTimestampUtil {
    public static Long toMilliseconds(LocalDate lcd) {
        if (lcd == null) {
            return null;
        }
        try {
            return lcd.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond();
        } catch (Exception e) {
            log.error("error when parse LocalDate: {} to Long", lcd);
            return null;
        }
    }

    public static LocalDate toLocalDate(Long l) {
        if (l == null || l <= 0L) {
            return null;
        }
        try {
            return Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            log.error("error when parse long: {} to LocalDate", l);
            return null;
        }
    }

    public static LocalDateTime toLocalDateTime(Long l) {
        if (l == null || l <= 0L) {
            return null;
        }
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(l),
                    ZoneId.systemDefault());
        } catch (Exception e) {
            log.error("error when parse long: {} to LocalDateTime", l);
            return null;
        }
    }

    public static Long toSecond(LocalDateTime l) {
        if (l == null) {
            return null;
        }
        try {
            return l.atZone(ZoneId.systemDefault()).toEpochSecond();
        } catch (Exception e) {
            log.error("error when parse long: {} to LocalDateTime", l);
            return null;
        }
    }

    public static String toRFC3339(LocalDate lcd) {
        if (lcd == null) return null;

        try {
            return lcd.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();
        } catch (Exception e) {
            log.error("error when parse LocalDate: {} to RFC3339 string format", lcd);
            return null;
        }
    }

}
