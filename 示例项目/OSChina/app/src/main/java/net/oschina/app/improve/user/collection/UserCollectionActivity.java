package net.oschina.app.improve.user.collection;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;

/**
 * Created by haibin
 * on 2016/12/30.
 */

public class UserCollectionActivity extends BackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, UserCollectionActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_collection;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        UserCollectionFragment fragment = UserCollectionFragment.newInstance();
        addFragment(R.id.fl_content, fragment);
        new UserCollectionPresenter(fragment);
    }
}
