package ro.deiutzblaxo.cloud.utils;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeUtils {

    public static long getCurrentEpochSeconds(String zoneId) {
        return LocalDateTime.now(ZoneId.of(zoneId)).toEpochSecond(ZoneOffset.UTC);
    }

    public static long getFutureEpochSecondsInSeconds(int seconds, String zoneId) {
        return LocalDateTime.now(ZoneId.of(zoneId)).plusSeconds(seconds).toEpochSecond(ZoneOffset.UTC);
    }


}
