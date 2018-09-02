package com.nhatton.ggtalkvn.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateConverter {
    private static final DateFormat timestampFormatter = new SimpleDateFormat("yyyyMMdd-hhmmss", Locale.UK);

    private DateConverter() {
    }

    public static String getTimeStamp() {
        Date date = new Date(System.currentTimeMillis());
        return timestampFormatter.format(date);
    }
}
