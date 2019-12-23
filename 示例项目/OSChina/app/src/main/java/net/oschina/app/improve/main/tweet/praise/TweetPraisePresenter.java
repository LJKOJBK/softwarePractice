package net.oschina.app.improve.main.tweet.praise;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetLike;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹点赞列表
 * Created by huanghaibin on 2017/12/18.
 */

class TweetPraisePresenter implements TweetPraiseContract.Presenter {
    private final TweetPraiseContract.View mView;
    private final Tweet mTweet;
    private String mNextToken;

    TweetPraisePresenter(TweetPraiseContract.View mView, Tweet mTweet) {
        this.mView = mView;
        this.mTweet = mTweet;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getTweetCommentList(mTweet.getId(), "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<TweetLike>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        PageBean<TweetLike> pageBean = resultBean.getResult();
                        mNextToken = pageBean.getNextPageToken();
                        List<TweetLike> list = pageBean.getItems();
                        mView.onRefreshSuccess(list);
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
        OSChinaApi.getTweetCommentList(mTweet.getId(), mNextToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<TweetLike>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        PageBean<TweetLike> pageBean = resultBean.getResult();
                        mNextToken = pageBean.getNextPageToken();
                        List<TweetLike> list = pageBean.getItems();
                        mView.onLoadMoreSuccess(list);
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

    private Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetLike>>>() {
        }.getType();
    }
}
