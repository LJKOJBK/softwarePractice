package net.oschina.app.util;

import net.oschina.app.R;

/**
 * 白天和夜间模式切换
 * Created by 火蚁 on 15/5/26.
 */
public class ThemeSwitchUtils {

    public static int getTitleReadedColor() {
        if (false) {
            return R.color.night_infoTextColor;
        } else {
            return R.color.day_infoTextColor;
        }
    }

    public static int getTitleUnReadedColor() {
        if (false) {
            return R.color.night_textColor;
        } else {
            return R.color.day_textColor;
        }
    }

    public static String getWebViewBodyString() {
        if (false) {
            return "<body class='night'><div class='contentstyle' id='article_body'>";
        } else {
            return "<body style='background-color: #FFF'><div class='contentstyle' id='article_body' >";
        }
    }
}
