package net.oschina.app.improve.main.synthesize;

import android.text.TextUtils;

import net.oschina.app.improve.bean.Article;

/**
 * 类型
 * Created by huanghaibin on 2017/12/11.
 */

public final class TypeFormat {
    public static boolean  isGit(Article article) {
        String url = article.getUrl();
        return !TextUtils.isEmpty(url) && (url.startsWith("https://gitee.com/") || url.startsWith("https://git.oschina.net/"));
    }

    public static boolean isZB(Article article) {
        String url = article.getUrl();
        return !TextUtils.isEmpty(url) && (url.startsWith("https://zb.oschina.net/"));
    }


    public static String formatUrl(Article article){
        return article.getUrl().contains("?") ?
                String.format("%s&%s",article.getUrl(),"utm_source=oschina-app") :
                String.format("%s?%s",article.getUrl(),"utm_source=oschina-app");
    }

    public static String formatUrl(String url){
        return url.contains("?") ?
                String.format("%s&%s",url,"utm_source=oschina-app") :
                String.format("%s?%s",url,"utm_source=oschina-app");
    }
}
