package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.user.fragments.UserBlogFragment;

/**
 * 用户博客
 * Created by huanghaibin on 2017/10/30.
 */

public class UserBlogActivity extends BackActivity {

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, UserBlogActivity.class);
        intent.putExtra(UserBlogFragment.BUNDLE_KEY_USER_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_blog;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        addFragment(R.id.fl_content,UserBlogFragment.instantiate(getIntent().getLongExtra(UserBlogFragment.BUNDLE_KEY_USER_ID,0)));
    }
}
