package net.oschina.app.improve.tweet.activities;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.tweet.fragments.TweetFragment;

/**
 * 新版话题界面
 * Created by huanghaibin on 2017/10/28.
 */

public class TopicActivity extends BackActivity {

    public static void show(Context context, int topicType, String topic) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra(TweetFragment.BUNDLE_KEY_REQUEST_CATALOG, topicType);
        intent.putExtra(TweetFragment.BUNDLE_KEY_TAG, topic);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topic;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        TweetFragment fragment = new TweetFragment();
        fragment.setArguments(getIntent().getExtras());
        addFragment(R.id.fl_content, fragment);
    }
}
