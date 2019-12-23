package net.oschina.app.improve.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.tweet.activities.TopicActivity;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析URL链接, 如果是站内链接, 用相应的activity打开, 否则用浏览器打开
 * Created by thanatos on 16/9/27.
 */

public class URLUtils {

    public static final Pattern PATTERN_URL = Pattern.compile(
            "(?:http|https)://([^/]+)(.+)"
    );

    public static final Pattern PATTERN_PATH_NEWS = Pattern.compile(
            "/news/([0-9]+).*"
    );

    public static final Pattern PATTERN_PATH_SOFTWARE = Pattern.compile(
            "/p/([^/]+)"
    );

    public static final Pattern PATTERN_PATH_TOPIC = Pattern.compile(
            "/question/tag/(\\w+)"
    );

    public static final Pattern PATTERN_PATH_TWEET_TOPIC = Pattern.compile(
            "/tweet-topic/([^/]+)"
    );

    public static final Pattern PATTERN_PATH_QUESTION = Pattern.compile(
            "/question/[0-9]+_([0-9]+)]"
    );

    public static final Pattern PATTERN_PATH_USER_BLOG = Pattern.compile(
            "/([^/]+)/blog/([0-9]+)"
    );

    public static final Pattern PATTERN_PATH_USER_TWEET = Pattern.compile(
            "/([^/]+)/tweet/([0-9]+)"
    );

    public static final Pattern PATTERN_PATH_USER_UID = Pattern.compile(
            "/u/([0-9]+)"
    );

    public static final Pattern PATTERN_PATH_USER_SUFFIX = Pattern.compile(
            "/([^/]+)"
    );

    public static final Pattern PATTERN_PATH_CITY_EVENT = Pattern.compile(
            "/([^/]+)/event/([0-9]+)"
    );

    public static final Pattern PATTERN_PATH_EVENT = Pattern.compile(
            "/event/([0-9]+)"
    );

    public static final Pattern PATTERN_IMAGE = Pattern.compile(
            ".*?(gif|jpeg|png|jpg|bmp)"
    );

    private static final Pattern PATTERN_GIT = Pattern.compile(
            ".*(git.oschina.net|gitee.com)/(.*)/(.*)"
    );

    private static final String PREFIX_IMAGE = "ima-api:action=showImage&data=";


    /**
     * 解析跳转链接, 使用对应应用打开
     *
     * @param context Context
     * @param uri     give me a uri
     */
    public static void parseUrl(Context context, String uri) {
        if (TextUtils.isEmpty(uri)) return;

        String url = uri;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // do nothing
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
            // other ?
            parseNonstandardUrl(context, uri);
            return;
        }

        // own ?
        String host = matcher.group(1);
        String path = matcher.group(2);

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(path)) return;

        long oid = 0;
        switch (host) {
            case "www.oschina.net":
                matcher = PATTERN_PATH_NEWS.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    net.oschina.app.improve.detail.general.NewsDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_SOFTWARE.matcher(path);
                if (matcher.find()) {
                    // https://www.oschina.net/p/parallels-desktop
                    SoftwareDetailActivity.show(context, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_TOPIC.matcher(path);
                if (matcher.find()) {
                    // TODO replace by new activity
                    UIHelper.showPostListByTag(context, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_TWEET_TOPIC.matcher(path);
                if (matcher.find()) {
                    // TODO replace by new activity
                    //  https://www.oschina.net/tweet-topic/Navicat+for+Postgresql
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(TweetFragment.BUNDLE_KEY_REQUEST_CATALOG, TweetFragment.CATALOG_TAG);
//                    bundle.putString(TweetFragment.BUNDLE_KEY_TAG, matcher.group(1));
//                    UIHelper.showSimpleBack(context, SimpleBackPage.TWEET_TOPIC_LIST, bundle);
                    TopicActivity.show(context, TweetFragment.CATALOG_TAG, matcher.group(1));
                    break;
                }
                matcher = PATTERN_PATH_QUESTION.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    QuestionDetailActivity.show(context, oid);
                    break;
                }
                matcher = PATTERN_PATH_EVENT.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(1));
                    if (oid > 0) {
                        UIHelper.showEventDetail(context, oid);
                        break;
                    }
                }
                UIHelper.openInternalBrowser(context, url);
                break;
            case "team.oschina.net":
                // TODO team要独立?
                UIHelper.openInternalBrowser(context, url);
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
                WebActivity.show(context, TypeFormat.formatUrl(url));
                break;
            case "my.oschina.net":
                matcher = PATTERN_PATH_USER_BLOG.matcher(path);
                if (matcher.find()) {
                    oid = StringUtils.toLong(matcher.group(2));
                    net.oschina.app.improve.detail.general.BlogDetailActivity.show(context, oid);
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
                UIHelper.openInternalBrowser(context, url);
                break;
            default:
                // pass
                UIHelper.openInternalBrowser(context, url);
        }

    }

    public static void parseNonstandardUrl(Context context, String url) {

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

}
