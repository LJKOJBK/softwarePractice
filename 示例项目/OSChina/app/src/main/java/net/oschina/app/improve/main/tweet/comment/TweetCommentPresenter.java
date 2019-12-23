package net.oschina.app.improve.main.tweet.comment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹评论列表
 * Created by huanghaibin on 2017/12/18.
 */

class TweetCommentPresenter implements TweetCommentContract.Presenter {
    private final TweetCommentContract.View mView;
    private final Tweet mTweet;
    private String mNextToken;

    TweetCommentPresenter(TweetCommentContract.View mView, Tweet mTweet) {
        this.mView = mView;
        this.mTweet = mTweet;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        if (mTweet == null)
            return;
        OSChinaApi.getTweetCommentList(mTweet.getId(), "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<TweetComment>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        PageBean<TweetComment> pageBean = resultBean.getResult();
                        mNextToken = pageBean.getNextPageToken();
                        List<TweetComment> list = pageBean.getItems();
                        mView.onRefreshSuccess(list);
                        mView.onRequestSuccess();
                        if (list.size() == 0) {
                            mView.showNotMore();
                        } else {
                            mView.showNotMore();
                        }
                        mView.onComplete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.network_timeout_hint);
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        if (mTweet == null)
            return;
        OSChinaApi.getTweetCommentList(mTweet.getId(), mNextToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<TweetComment>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        PageBean<TweetComment> pageBean = resultBean.getResult();
                        mNextToken = pageBean.getNextPageToken();
                        List<TweetComment> list = pageBean.getItems();
                        mView.onLoadMoreSuccess(list);
                        mView.onRequestSuccess();
                        if (list.size() == 0) {
                            mView.showNotMore();
                        } else {
                            mView.showNotMore();
                        }
                        mView.onComplete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.network_timeout_hint);
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void deleteTweetComment(long id, TweetComment comment, final int position) {
        OSChinaApi.deleteTweetComment(id, comment.getId(), new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showDeleteFailure();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean result = new Gson().fromJson(
                            responseString, new TypeToken<ResultBean>() {
                            }.getType());
                    if (result.isSuccess()) {
                        mView.showDeleteSuccess(position);
                    } else {
                        mView.showDeleteFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showDeleteFailure();
                }

            }
        });
    }

    private Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetComment>>>() {
        }.getType();
    }
}
