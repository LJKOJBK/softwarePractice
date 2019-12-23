package net.oschina.app.improve.main.tweet.praise;

import android.os.Bundle;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.tweet.adapter.TweetLikeUsersAdapter;

/**
 * 动弹点赞列表
 * Created by huanghaibin on 2017/12/18.
 */

public class TweetPraiseFragment extends BaseRecyclerFragment<TweetPraiseContract.Presenter, TweetLike>
        implements TweetPraiseContract.View {
    private Tweet mTweet;

    public static TweetPraiseFragment newInstance(Tweet tweet) {
        TweetPraiseFragment fragment = new TweetPraiseFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tweet", tweet);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTweet = (Tweet) bundle.getSerializable("tweet");
    }

    @Override
    protected void initData() {
        new TweetPraisePresenter(this, mTweet);
        super.initData();
    }

    @Override
    protected void onItemClick(TweetLike tweetLike, int position) {

    }

    @Override
    protected BaseRecyclerAdapter<TweetLike> getAdapter() {
        return new TweetLikeUsersAdapter(mContext);
    }
}
