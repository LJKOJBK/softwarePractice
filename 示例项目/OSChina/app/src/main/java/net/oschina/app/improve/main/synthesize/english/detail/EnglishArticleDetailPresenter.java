package net.oschina.app.improve.main.synthesize.english.detail;

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
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.common.utils.CollectionUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 英文详情界面
 * Created by huanghaibin on 2018/1/15.
 */

class EnglishArticleDetailPresenter implements EnglishArticleDetailContract.Presenter {
    private final EnglishArticleDetailContract.View mView;
    private final EnglishArticleDetailContract.EmptyView mEmptyView;
    private static final int TYPE_ENGLISH = 8000;//获取英文
    private Article mArticle;
    private Article mSource;
    private String mNextToken;
    private Article mTranslateArticle;
    private boolean hasGetDetail;
    private boolean hasGetENDetail;//获取了英文
    private boolean isEnglish;

    EnglishArticleDetailPresenter(EnglishArticleDetailContract.View mView,
                                  EnglishArticleDetailContract.EmptyView emptyView,
                                  Article mArticle) {
        this.mView = mView;
        this.mEmptyView = emptyView;
        this.mSource = mArticle;
        this.mArticle = mArticle;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getArticleRecommends(
                mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                TYPE_ENGLISH,
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
                TYPE_ENGLISH,
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
    public void getArticleDetail() {
        addClickCount();

        if (!TextUtils.isEmpty(mArticle.getTitleTranslated())) {
            isEnglish = true;
            getEnglishDetailCN();
            return;
        }
        isEnglish = false;
        getEnglishDetailEN();
    }

    /**
     * 获取英文详情
     */
    private void getEnglishDetailEN() {
        OSChinaApi.getArticleDetail(mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                TYPE_ENGLISH,
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
                                hasGetDetail = true;
                                hasGetENDetail = true;
                                isEnglish = true;
                                mView.showGetDetailSuccess(mArticle);
                                mEmptyView.showTranslateChange(true);
                                mEmptyView.showGetDetailSuccess(mArticle);
                                mEmptyView.hideEmptyLayout();
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

    /**
     * 获取翻译详情
     */
    private void getEnglishDetailCN() {
        OSChinaApi.translate(mArticle.getKey(), Article.TYPE_ENGLISH, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showTranslateFailure("网络错误");
                mEmptyView.showTranslateFailure("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Article>>() {
                    }.getType();
                    ResultBean<Article> bean = new Gson().fromJson(responseString, type);
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            mTranslateArticle = bean.getResult();
                            hasGetDetail = true;
                            isEnglish = false;
                            parseTranslate();
                        } else {
                            mEmptyView.showTranslateFailure(bean.getMessage());
                            mView.showTranslateFailure(bean.getMessage());
                        }
                    } else {
                        mEmptyView.showTranslateFailure("网络错误");
                        mView.showTranslateFailure("翻译错误");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showTranslateFailure("翻译错误");
                    mEmptyView.showTranslateFailure("网络错误");
                }
            }
        });
    }

    @Override
    public void putArticleComment(String content, long referId, long reAuthorId) {
        OSChinaApi.pubArticleComment(mArticle.getKey(),
                content,
                TYPE_ENGLISH,
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
    public void fav() {

        OSChinaApi.articleFav(new Gson().toJson(mSource),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("onFailure", "  --  " + statusCode + "  --  " + responseString);
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
    public void report() {
        mEmptyView.showReport();
    }


    @Override
    public void translate() {
        if (isEnglish) {
            if (mTranslateArticle != null) {
                try {
                    parseTranslate();
                } catch (Exception e) {
                    mView.showTranslateFailure("网络错误");
                    mEmptyView.showTranslateFailure("网络错误");
                    e.printStackTrace();
                }
            } else {
                getEnglishDetailCN();
            }
        } else {
            if (hasGetENDetail) {
                isEnglish = true;
                mView.showGetDetailSuccess(mArticle);
                mEmptyView.showTranslateChange(true);
            } else {
                getEnglishDetailEN();
            }
        }
    }

    /**
     * 解析翻译结果
     */
    private void parseTranslate() {
        Type type = new TypeToken<List<Translate>>() {
        }.getType();
        List<Translate> result = new Gson().fromJson(mTranslateArticle.getContent(), type);
        if (result == null || result.size() == 0) {
            mView.showTranslateFailure("网络错误");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Translate translate : result) {
            sb.append(translate.src);
            sb.append("\n");
            sb.append(translate.dest);
        }
        mView.showTranslateSuccess(mTranslateArticle, sb.toString());
        isEnglish = false;
        mEmptyView.showTranslateChange(false);
    }


    @Override
    public void scrollToTop() {
        mView.showScrollToTop();
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

    @Override
    public boolean hasGetDetail() {
        return hasGetDetail;
    }

    @SuppressWarnings("unused")
    private static class Translate implements Serializable {
        private int index;
        private String src;
        private String dest;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }


        public String getDest() {
            return dest;
        }

        public void setDest(String dest) {
            this.dest = dest;
        }
    }
}
