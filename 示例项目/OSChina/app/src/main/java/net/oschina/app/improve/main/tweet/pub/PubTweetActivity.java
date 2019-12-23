package net.oschina.app.improve.main.tweet.pub;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.improve.base.activities.BackActivity;

/**
 * 新版本发布动弹界面
 * Created by huanghaibin on 2018/1/3.
 */

public class PubTweetActivity extends BackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, PubTweetActivity.class));
    }

    @Override
    protected int getContentView() {
        return 0;
    }
}
