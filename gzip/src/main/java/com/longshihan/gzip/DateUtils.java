package com.longshihan.gzip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dfYMD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static String getDate() {
        return dfYMD.format(Calendar.getInstance().getTime());
    }
}
