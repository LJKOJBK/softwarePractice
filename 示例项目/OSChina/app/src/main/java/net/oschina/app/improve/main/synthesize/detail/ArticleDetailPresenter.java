package net.oschina.app.improve.main.synthesize.detail;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppConfig;
import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.detail.db.API;
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 头条详情
 * Created by huanghaibin on 2017/10/23.
 */

class ArticleDetailPresenter implements ArticleDetailContract.Presenter {
    private final ArticleDetailContract.View mView;
    private final ArticleDetailContract.EmptyView mEmptyView;
    private String mNextToken;
    private Article mArticle;

    ArticleDetailPresenter(ArticleDetailContract.View mView, ArticleDetailContract.EmptyView mEmptyView, Article article) {
        this.mView = mView;
        this.mArticle = article;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }


    @Override
    public void getArticleDetail() {
        OSChinaApi.getArticleDetail(mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Article>>() {
                            }.getType();
                            ResultBean<Article> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess() && bean.getResult() != null) {
                                mArticle = bean.getResult();
                                mView.showGetDetailSuccess(mArticle);
                                mEmptyView.showGetDetailSuccess(mArticle);
                            } else {
                                mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                        }
                    }
                });
    }

    @Override
    public void addClickCount() {
        OSChinaApi.addClickCount(mArticle.getKey(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    @Override
    public void scrollToTop() {
        mView.showScrollToTop();
    }

    @Override
    public void fav() {
        OSChinaApi.articleFav(new Gson().toJson(mArticle),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mEmptyView.showFavError();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Collection>>() {
                            }.getType();
                            ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean != null && resultBean.isSuccess()) {
                                Collection collection = resultBean.getResult();
                                mArticle.setFavorite(collection.isFavorite());
                                mEmptyView.showFavReverseSuccess(collection.isFavorite());
                            } else {
                                mEmptyView.showFavError();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                    }
                });
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getArticleRecommends(
                mArticle.getKey(),
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
                        }
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
                                mView.onRefreshSuccess(list);
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

    @Override
    public void onLoadMore() {
        OSChinaApi.getArticleRecommends(
                mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                mNextToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showNotMore();
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                                mView.showNetworkError(R.string.footer_type_net_error);
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
    public void putArticleComment(String content, long referId, long reAuthorId) {
        OSChinaApi.pubArticleComment(mArticle.getKey(),
                content,
                referId,
                reAuthorId,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showNetworkError(R.string.tip_network_error);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Comment>>() {
                            }.getType();

                            ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                mArticle.setCommentCount(mArticle.getCommentCount() + 1);
                                Comment respComment = resultBean.getResult();
                                if (respComment != null) {
                                    getArticleDetail();
                                    mView.showCommentSuccess(respComment);
                                    mEmptyView.showCommentSuccess(respComment);
                                }
                            } else {
                                mView.showCommentError(resultBean.getMessage());
                                mEmptyView.showCommentError(resultBean.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                            mView.showCommentError("评论失败");
                            mEmptyView.showCommentError("评论失败");
                        }
                    }
                });
    }


    @Override
    public void uploadBehaviors(final List<Behavior> behaviors) {
        API.addBehaviors(new Gson().toJson(behaviors), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO: 2017/5/25 不需要处理失败的情况
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<String>>() {
                    }.getType();
                    ResultBean<String> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.getCode() == 1) {
                        DBManager.getInstance()
                                .delete(Behavior.class, "id<=?", String.valueOf(behaviors.get(behaviors.size() - 1).getId()));
                        AppConfig.getAppConfig(OSCApplication.getInstance()).set("upload_behavior_time", bean.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public String formatTextCount(int count) {
        String text = String.valueOf(count);
        if (count < 1000)
            return String.format(" %s ", text);
        if (count > 1000 && count < 10000)
            return String.format(" %s,%s ", text.substring(0, 1), text.substring(1, text.length()));
        if (count > 10000 && count < 100000)
            return String.format(" %s,%s ", text.substring(0, 2), text.substring(2, text.length()));
        if (count > 100000 && count < 1000000)
            return String.format(" %s,%s ", text.substring(0, 3), text.substring(3, text.length()));
        if (count > 1000000 && count < 10000000)
            return String.format(" %s,%s,%s ", text.substring(0, 1), text.substring(1, 4), text.substring(4, text.length()));
        return String.format(" %s ", String.valueOf(count));
    }

    @Override
    public String formatTime(long time) {
        if (time < 60) {
            return String.format(" %s ", time);
        }
        if (time >= 3600) {
            return " 1 ";
        }
        return String.format(" %s ", time / 60 + 1);
    }

    @Override
    public String formatTimeUnit(long time) {
        if (time >= 3600) {
            return "小时";
        } else if (time >= 60) {
            return "分钟";
        } else
            return "秒";
    }

}
