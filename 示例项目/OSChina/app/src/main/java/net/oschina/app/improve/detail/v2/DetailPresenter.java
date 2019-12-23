package net.oschina.app.improve.detail.v2;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.detail.db.API;
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.pay.wx.WeChatPay;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/11/30.
 */
@SuppressWarnings("all")
public class DetailPresenter implements DetailContract.Presenter {
    private final DetailContract.View mView;
    private final DetailContract.EmptyView mEmptyView;
    private SubBean mBean;
    private SubBean mCacheBean;
    private String mIdent;
    private String mNextToken;
    DetailPresenter(DetailContract.View mView, DetailContract.EmptyView mEmptyView, SubBean bean, String ident) {
        this.mView = mView;
        this.mBean = bean;
        this.mIdent = ident;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getCache() {
        mCacheBean = DetailCache.readCache(mBean);
        if (mCacheBean == null)
            return;
        mView.showGetDetailSuccess(mCacheBean);
        mEmptyView.showGetDetailSuccess(mCacheBean);
    }

    @Override
    public void getDetail() {
        OSChinaApi.getDetail(mBean.getType(), mIdent, mBean.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mCacheBean != null)
                    return;
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SubBean>>() {
                    }.getType();
                    ResultBean<SubBean> bean = AppOperator.createGson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mBean = bean.getResult();
                        mView.showGetDetailSuccess(mBean);
                        mEmptyView.showGetDetailSuccess(mBean);
                    } else {
                        if (mCacheBean != null)
                            return;
                        mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (mCacheBean != null)
                    return;
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }
        });
    }

    @Override
    public void favReverse() {
        OSChinaApi.getFavReverse(mBean.getId(), mBean.getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showFavError();
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();
                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Collection collection = resultBean.getResult();
                        mBean.setFavorite(collection.isFavorite());
                        mBean.getStatistics().setFavCount(collection.getFavCount());
                        mView.showFavReverseSuccess(collection.isFavorite(), collection.getFavCount(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);
                        mEmptyView.showFavReverseSuccess(collection.isFavorite(), collection.getFavCount(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);
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

    @Override
    public void addComment(long sourceId, int type, String content, long referId, long replyId, long reAuthorId) {
        OSChinaApi.pubComment(sourceId, type, content, referId, replyId, reAuthorId, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
                mEmptyView.showCommentError("");
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

            @Override
            public void onStart() {
                super.onStart();
                SubBean subBean = mBean;
                if (subBean != null)
                    ContactsCacheManager.addRecentCache(subBean.getAuthor());
            }
        });
    }

    @Override
    public void addUserRelation(long authorId) {
        OSChinaApi.addUserRelationReverse(authorId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showAddRelationError();
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserRelation>>() {
                    }.getType();

                    ResultBean<UserRelation> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        int relation = resultBean.getResult().getRelation();
                        mBean.getAuthor().setRelation(relation);
                        boolean isRelation = relation == UserRelation.RELATION_ALL
                                || relation == UserRelation.RELATION_ONLY_YOU;
                        mView.showAddRelationSuccess(isRelation,
                                isRelation ? R.string.add_relation_success : R.string.cancel_relation_success);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showAddRelationError();
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
                        mEmptyView.showUploadBehaviorsSuccess(behaviors.get(behaviors.size() - 1).getId()
                                , bean.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        OSChinaApi.getArticleRecommends(
                String.format("osc_%s_%s",mBean.getType(),mBean.getId()),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                "",
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showMoreMore();
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
                                    mView.showMoreMore();
                                }
                            } else {
                                mView.showMoreMore();
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showMoreMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getArticleRecommends(
                String.format("osc_%s_%s",mBean.getType(),mBean.getId()),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                mNextToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showMoreMore();
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
                                    mView.showMoreMore();
                                }
                            } else {
                                mView.showMoreMore();
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showMoreMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void shareComment(Comment comment) {
        mEmptyView.showShareCommentView(comment);
    }

    boolean isHideCommentBar() {
        return mBean.getType() == News.TYPE_EVENT;
    }

    @Override
    public void scrollToTop() {
        mView.showScrollToTop();
    }


    @Override
    public void payDonate(long authorId, long objId, float money, final int payType) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        OSChinaApi.getPayDonate(authorId, objId, (float) (Math.round(money * 100) / 100), payType, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showPayDonateError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = null;
                    if (payType == 1) {
                        type = new TypeToken<ResultBean<String>>() {
                        }.getType();
                        ResultBean<String> resultBean = new Gson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            mView.showPayDonateSuccess(payType, resultBean.getResult(), null);
                        } else {
                            mView.showPayDonateError();
                        }
                    } else {
                        type = new TypeToken<ResultBean<WeChatPay.PayResult>>() {
                        }.getType();
                        ResultBean<WeChatPay.PayResult> resultBean = new Gson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            mView.showPayDonateSuccess(payType, null, resultBean.getResult());
                        } else {
                            mView.showPayDonateError();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showPayDonateError();
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
}
