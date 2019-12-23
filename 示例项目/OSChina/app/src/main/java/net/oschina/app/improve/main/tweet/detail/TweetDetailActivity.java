package net.oschina.app.improve.main.tweet.detail;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.Tweet;

/**
 * 动弹详情
 * Created by huanghaibin on 2017/11/14.
 */

public class TweetDetailActivity extends BackActivity{

    public static void show(Context context, Tweet tweet) {
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra("tweet", tweet);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Tweet tweet = new Tweet();
        tweet.setId(id);
        show(context, tweet);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
