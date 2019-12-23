package net.oschina.app.improve.utils.parser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索高亮显示
 * Created by huanghaibin on 2018/1/10.
 */

public class SearchParser extends RichTextParser {
    private static SearchParser mInstance = new SearchParser();

    public static SearchParser getInstance() {
        return mInstance;
    }

    @Override
    public Spannable parse(Context context, String content) {
        return null;
    }

    @SuppressWarnings("all")
    public Spannable parse(String content, String keyword) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        if (true) {//去掉高亮
            return builder;
        }
        if (TextUtils.isEmpty(keyword)) {
            return builder;
        }
        Pattern pattern = Pattern.compile(escape(keyword), Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        matcher = pattern.matcher(builder.toString());
        while (matcher.find()) {
            CharSequence s = builder.subSequence(matcher.start(), matcher.end());
            builder.replace(matcher.start(), matcher.end(), s);
            ForegroundColorSpan span = new ForegroundColorSpan(0xffED5B5B);
            builder.setSpan(span, matcher.start(), matcher.start() + s.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    /**
     * 转义字符串
     */
    private static String escape(String keyword) {
        String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
        for (String key : fbsArr) {
            if (keyword.contains(key)) {
                keyword = keyword.replace(key, "\\" + key);
            }
        }
        return keyword;
    }
}
