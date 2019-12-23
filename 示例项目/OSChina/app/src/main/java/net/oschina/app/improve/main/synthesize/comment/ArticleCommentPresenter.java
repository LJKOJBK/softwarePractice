package net.oschina.app.improve.main.synthesize.comment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 头条评论列表
 * Created by huanghaibin on 2017/10/28.
 */
class ArticleCommentPresenter implements ArticleCommentContract.Presenter {
    private final ArticleCommentContract.View mView;
    private final ArticleCommentContract.Action mActionView;
    private final Article mArticle;
    private String mNextToken;
    ArticleCommentPresenter(ArticleCommentContract.View mView,
                            ArticleCommentContract.Action mActionView,
                            Article article) {
        this.mView = mView;
        this.mArticle = article;
        this.mActionView = mActionView;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getArticleComments(mArticle.getKey(),
                1, null,
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
                            Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                            }.getType();
                            ResultBean<PageBean<Comment>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                PageBean<Comment> pageBean = bean.getResult();
                                mNextToken = pageBean.getNextPageToken();
                                List<Comment> list = pageBean.getItems();
                                mView.onRefreshSuccess(list);
                                if (list.size() < 20) {
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
        OSChinaApi.getArticleComments(mArticle.getKey(),
                1, mNextToken,
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
                            Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                            }.getType();
                            ResultBean<PageBean<Comment>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                PageBean<Comment> pageBean = bean.getResult();
                                mNextToken = pageBean.getNextPageToken();
                                List<Comment> list = pageBean.getItems();
                                mView.onLoadMoreSuccess(list);
                                if (list.size() < 20) {
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
                                    mView.showAddCommentSuccess(respComment,R.string.pub_comment_success);
                                    mActionView.showAddCommentSuccess(respComment,R.string.pub_comment_success);
                                }
                            } else {
                                mView.showAddCommentFailure(R.string.pub_comment_failed);
                                mActionView.showAddCommentFailure(R.string.pub_comment_failed);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                            mView.showAddCommentFailure(R.string.pub_comment_failed);
                            mActionView.showAddCommentFailure(R.string.pub_comment_failed);
                        }
                    }
                });
    }
}
