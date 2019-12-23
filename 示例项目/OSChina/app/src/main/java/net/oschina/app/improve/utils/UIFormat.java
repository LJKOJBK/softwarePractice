package net.oschina.app.improve.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.QuestionTagFragment;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.tweet.activities.TopicActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UI解析类
 * Created by huanghaibin on 2017/12/12.
 */
@SuppressWarnings("unused")
public final class UIFormat {

    private static final Pattern PATTERN_URL = Pattern.compile(
            "(?:http|https)://([^/]+)(.+)"
    );

    private static final Pattern PATTERN_PATH_NEWS = Pattern.compile(
            "/news/([0-9]+).*"
    );

    private static final Pattern PATTERN_PATH_SOFTWARE = Pattern.compile(
            "/p/([^/]+)"
    );

    private static final Pattern PATTERN_PATH_TOPIC = Pattern.compile(
            "/question/tag/(\\w+)"
    );

    private static final Pattern PATTERN_PATH_TWEET_TOPIC = Pattern.compile(
            "/tweet-topic/([^/]+)"
    );

    private static final Pattern PATTERN_PATH_QUESTION = Pattern.compile(
            "/question/(\\w+)"
    );

    private static final Pattern PATTERN_PATH_USER_BLOG = Pattern.compile(
            "/([^/]+)/blog/([0-9]+)"
    );

    private static final Pattern PATTERN_PATH_USER_TWEET = Pattern.compile(
            "/([^/]+)/tweet/([0-9]+)"
    );

    private static final Pattern PATTERN_PATH_USER_UID = Pattern.compile(
            "/u/([0-9]+)"
    );

    private static final Pattern PATTERN_PATH_USER_SUFFIX = Pattern.compile(
            "/([^/]+)"
    );

    private static final Pattern PATTERN_PATH_CITY_EVENT = Pattern.compile(
            "/([^/]+)/event/([0-9]+)"
    );

    private static final Pattern PATTERN_PATH_EVENT = Pattern.compile(
            "/event/([0-9]+)"
    );

    private static final Pattern PATTERN_IMAGE = Pattern.compile(
            ".*?(gif|jpeg|png|jpg|bmp)"
    );

    private static final Pattern PATTERN_GIT = Pattern.compile(
            ".*(git.oschina.net|gitee.com)/(.*)/(.*)"
    );

    private static final String PREFIX_IMAGE = "ima-api:action=showImage&data=";


    public static void show(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (checkIsDownload(url)) {
            openExternalBrowser(context, url);
            return;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Matcher matcher;

        // image url ?
        matcher = PATTERN_IMAGE.matcher(url);
        if (matcher.matches()) {
            ImageGalleryActivity.show(context, url);
            return;
        }

        matcher = PATTERN_URL.matcher(url);
        if (!matcher.find()) {
            parseNonstandardUrl(context, url);
            return;
        }

        // own ?
        String host = matcher.group(1);
        String path = matcher.group(2);

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(path)) {
            return;
        }

        long oid;
        switch (host) {
            case "www.oschina.net":
                matcher = PATTERN_PATH_NEWS.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    NewsDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_SOFTWARE.matcher(path);
                if (matcher.find()) {
                    SoftwareDetailActivity.show(context, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_TOPIC.matcher(path);
                if (matcher.find()) {
                    showPostListByTag(context, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_TWEET_TOPIC.matcher(path);
                if (matcher.find()) {
                    // TODO replace by new activity
                    TopicActivity.show(context, TweetFragment.CATALOG_TAG, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_QUESTION.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1).split("_")[1]);
                    QuestionDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_EVENT.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    if (oid > 0) {
                        EventDetailActivity.show(context, oid);
                        break;
                    }
                }
                openAPPBrowser(context, url);
                break;
            case "team.oschina.net":
                // TODO team要独立?
                openAPPBrowser(context, url);
                break;
            case "git.oschina.net":
                // TODO 如果用户安装了git@osc application, 使用git@osc打开
            case "gitee.com":
//                Matcher matcherGit = PATTERN_GIT.matcher(uri);
//                if (matcherGit.find() && matcherGit.groupCount() >= 2) {
//                    String group1 = matcherGit.group(2);
//                    String group2 = matcherGit.group(3);
//                    if ("explore".equals(group1) || "gists".equals(group1) || "enterprises".equals(group1)) {
//                        UIHelper.openInternalBrowser(context, url);
//                    } else {
//                        ProjectDetailActivity.show(context, group1, group2, uri);
//                    }
//                } else {
//                    UIHelper.openInternalBrowser(context, url);
//                }
                openAPPBrowser(context, TypeFormat.formatUrl(url));
                break;
            case "my.oschina.net":
                matcher = PATTERN_PATH_USER_BLOG.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(2));
                    BlogDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_USER_TWEET.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(2));
                    TweetDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_USER_UID.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    OtherUserHomeActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_USER_SUFFIX.matcher(path);
                if (matcher.find()) {
                    OtherUserHomeActivity.show(context, 0, matcher.group(1));
                    break;
                }
                UIHelper.openInternalBrowser(context, url);
                break;
            case "city.oschina.net":
                matcher = PATTERN_PATH_CITY_EVENT.matcher(url);
                if (matcher.find()) {
                    long eid = StringUtils.toInt(matcher.group(2), 0);
                    if (eid <= 0) return;
                    UIHelper.showEventDetail(context, eid);
                    return;
                }
                openAPPBrowser(context, url);
                break;
            default:
                openAPPBrowser(context, url);
        }
    }


    private static boolean checkIsDownload(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        for (String suffix : SUFFIX) {
            if (url.endsWith(suffix))
                return true;
        }
        return false;
    }

    private static final String[] SUFFIX = new String[]{".apk", ".zip", ".rar",
            ".mp3", ".mp4", ".rm", ".rmvb", ".avi", ".7z", ".flv", ".iso", ".txt", ".pdf"};


    /**
     * 打开内置浏览器
     *
     * @param context context
     * @param url     url
     */
    private static void openAPPBrowser(Context context, String url) {
        WebActivity.show(context, url);
    }

    /**
     * 打开外置的浏览器
     *
     * @param context context
     * @param url     url
     */
    private static void openExternalBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, "选择打开的应用"));
    }


    private static void parseNonstandardUrl(Context context, String url) {

        if (url.startsWith("mailto:")) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            context.startActivity(Intent.createChooser(intent, "选择发送应用"));
            return;
        }

        // image, 我不懂老代码的思路...所以直接copy过来
        if (url.startsWith(PREFIX_IMAGE)) {
            String jos = url.substring(PREFIX_IMAGE.length());
            try {
                JSONObject json = new JSONObject(jos);
                String[] urls = json.getString("urls").split(",");
                ImageGalleryActivity.show(context, urls[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 显示相关Tag帖子列表
     *
     * @param context context
     * @param tag     tag
     */
    private static void showPostListByTag(Context context, String tag) {
        Bundle args = new Bundle();
        args.putString(QuestionTagFragment.BUNDLE_KEY_TAG, tag);
        showSimpleBack(context, SimpleBackPage.QUESTION_TAG, args);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page,
                                      Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

}
