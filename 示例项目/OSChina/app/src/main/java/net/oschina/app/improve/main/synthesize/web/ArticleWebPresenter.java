package net.oschina.app.improve.main.synthesize.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 头条浏览器
 * Created by huanghaibin on 2017/10/30.
 */

class ArticleWebPresenter implements ArticleWebContract.Presenter {
    private final ArticleWebContract.View mView;
    private final Article mArticle;
    ArticleWebPresenter(ArticleWebContract.View mView, Article article) {
        this.mView = mView;
        this.mArticle = article;
        this.mView.setPresenter(this);
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
                                Comment respComment = resultBean.getResult();
                                if (respComment != null) {
                                    mView.showCommentSuccess(respComment);
                                }
                            } else {
                                mView.showCommentError(resultBean.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                            mView.showCommentError("评论失败");
                        }
                    }
                });
    }


    @Override
    public void fav() {
        OSChinaApi.articleFav(new Gson().toJson(mArticle),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showFavError();
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
                                mView.showFavReverseSuccess(collection.isFavorite());
                            } else {
                                mView.showFavError();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                    }
                });
    }
}
