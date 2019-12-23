package net.oschina.app.improve.main.synthesize.article;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.Launcher;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.common.utils.CollectionUtil;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import cz.msebera.android.httpclient.Header;

/**
 * 头条界面
 * Created by huanghaibin on 2017/10/23.
 */

class ArticlePresenter implements ArticleContract.Presenter {
    private final ArticleContract.View mView;
    private String mNextToken;
    private static final String CACHE_NAME = "article_list";

    ArticlePresenter(ArticleContract.View nView) {
        this.mView = nView;
        this.mView.setPresenter(this);
    }

    @Override
    public void loadCache() {
        List<Article> items = CacheManager.readListJson(OSCApplication.getInstance(), CACHE_NAME, Article.class);
        if (items != null) {
            mView.onRefreshSuccess(items);
            mView.onComplete();
        }
    }

    @Override
    public void onRefreshing() {
        getLaunch();
        OSChinaApi.getArticles(
                OSCSharedPreference.getInstance().getDeviceUUID(),
                "",
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showNotMore();
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.onComplete();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<PageBean<Article>>>() {
                            }.getType();
                            ResultBean<PageBean<Article>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    PageBean<Article> pageBean = bean.getResult();
                                    mNextToken = pageBean.getNextPageToken();
                                    List<Article> list = pageBean.getItems();
                                    for (Article article : list) {
                                        article.setImgs(removeImgs(article.getImgs()));
                                    }
                                    CacheManager.saveToJson(OSCApplication.getInstance(), CACHE_NAME, list);
                                    mView.onRefreshSuccess(list);
                                    if (list.size() == 0) {
                                        mView.showNotMore();
                                    }
                                } else if ("该版本不受支持,请下载最新的客户端".equals(bean.getMessage())) {
                                    mView.versionPast();
                                } else {
                                    mView.showNotMore();
                                }
                            } else {
                                mView.showNotMore();
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showNotMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getArticles(
                OSCSharedPreference.getInstance().getDeviceUUID(),
                mNextToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showNetworkError(R.string.state_network_error);
                        mView.onComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<PageBean<Article>>>() {
                            }.getType();
                            ResultBean<PageBean<Article>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                PageBean<Article> pageBean = bean.getResult();
                                mNextToken = pageBean.getNextPageToken();
                                List<Article> list = pageBean.getItems();
                                for (Article article : list) {
                                    article.setImgs(removeImgs(article.getImgs()));
                                }
                                mView.onLoadMoreSuccess(list);
                                if (list.size() == 0) {
                                    mView.showNotMore();
                                }
                            } else {
                                mView.showNotMore();
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showNotMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    private static String[] removeImgs(String[] imgs) {
        if (imgs == null || imgs.length == 0)
            return null;
        List<String> list = new ArrayList<>();
        for (String img : imgs) {
            if (!TextUtils.isEmpty(img)) {
                if (img.startsWith("http")) {
                    list.add(img);
                }
            }
        }
        return CollectionUtil.toArray(list, String.class);
    }

    private static void getLaunch() {
        OSChinaApi.getLauncher(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Launcher>>() {
                    }.getType();
                    ResultBean<Launcher> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess() && bean.getResult() != null) {
                        CacheManager.saveToJson(OSCApplication.getInstance(), "Launcher.json", bean.getResult());
                        saveAdImage(bean.getResult());
                    }else {
                        CacheManager.removeCahche(OSCApplication.getInstance(), "Launcher.json");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void saveAdImage(Launcher launcher) {
        final Future<File> future = Glide.with(OSCApplication.getInstance())
                .load(launcher.getImgUrl())
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    File sourceFile = future.get();
                    if (sourceFile == null || !sourceFile.exists())
                        return;
                    String savePath = OSCApplication.getInstance().getCacheDir() + "/launcher";
                    final File saveFile = new File(savePath);
                    StreamUtil.copyFile(sourceFile, saveFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
