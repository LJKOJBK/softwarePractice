package net.oschina.app.improve.main.synthesize.pub;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * 发布分享文章
 * Created by huanghaibin on 2017/12/1.
 */

class PubArticlePresenter implements PubArticleContract.Presenter {
    private final PubArticleContract.View mView;
    private static String rule = null;

    PubArticlePresenter(PubArticleContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        getWXRule();
    }

    private void getWXRule() {
        OSChinaApi.getWXRule(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<String>>() {
                    }.getType();
                    ResultBean<String> bean = new Gson().fromJson(responseString, type);
                    rule = bean.getResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void putArticle(String url, String title) {
        if (!checkUrl(url)) {
            mView.showPubFailure("请填写正确的url");
            return;
        }
        OSChinaApi.putArticle(url,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showPubFailure("投递失败");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    mView.showPubSuccess(R.string.pub_article_success);
                                } else {
                                    mView.showPubFailure(bean.getMessage());
                                }
                            } else {
                                mView.showPubFailure("投递失败");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static final AsyncHttpClient mClient = new AsyncHttpClient();

    @Override
    public void getTitle(String url) {
        mClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showGetTitleFailure("获取标题失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    mView.showGetTitleSuccess(matcherTitle(responseString));
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showGetTitleFailure("获取标题失败");
                }
            }
        });
    }

    @Override
    public boolean isWechatUrl(String url) {
        return !TextUtils.isEmpty(url) && url.startsWith("https://mp.weixin.qq.com/");
    }

    static boolean checkUrl(String url) {
        Pattern pattern = Pattern.compile("^https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        return pattern.matcher(url).find();
    }

    static String findUrl(String text) {
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private static String matcherTitle(String response) {
        Pattern pattern ;
        if(TextUtils.isEmpty(rule)){
            pattern = Pattern.compile("<[^!][^(title)][^(script)][^>]+>([^<][^>]+)</[^!][^(title)][^(script)][^>]+>");
        }else {
            pattern = Pattern.compile(rule);
        }
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "获取标题失败";
    }
}
