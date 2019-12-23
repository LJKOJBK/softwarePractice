package net.oschina.app.improve.main.synthesize;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 头条时间格式化
 * Created by huanghaibin on 2017/11/7.
 */

public final class DataFormat {

    private static final SimpleDateFormat CUR_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String parsePubDate(String pubDate) {
        if (TextUtils.isEmpty(pubDate) || pubDate.length() < 8)
            return pubDate;
        int year = parseInt(pubDate.substring(0, 4));
        int month = parseInt(pubDate.substring(4, 6));
        int day = parseInt(pubDate.substring(6, 8));
        String date = String.format("%s-%s-%s", year,
                pubDate.substring(4, 6),
                pubDate.substring(6, 8));
        if (isToday(date)) {
            return "今天";
        }
        if (isYesterday(year, month, day))
            return "昨天";
        return date;
    }

    private static int parseInt(String intStr) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean isToday(String pubDate) {
        String today = CUR_FORMAT.format(new Date());
        return pubDate.equalsIgnoreCase(today);
    }

    private static boolean isYesterday(int year, int month, int day) {
        Calendar mCurrentDate = Calendar.getInstance();
        mCurrentDate.set(year, month -1, day , 0, 0, 0);
        long delta = new Date().getTime() - mCurrentDate.getTimeInMillis();
        return delta > 0 && delta <= 48L * 3600000L;
    }
}
