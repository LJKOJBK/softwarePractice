package net.oschina.app.improve.user.tags;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.user.tags.search.SearchTagsActivity;

import butterknife.OnClick;

/**
 * 用户标签界面
 * Created by haibin on 2018/05/22.
 */
public class UserTagsActivity extends BackActivity implements View.OnClickListener {

    public static void show(Context context) {
        if (!AccountHelper.isLogin()) {
            return;
        }
        context.startActivity(new Intent(context, UserTagsActivity.class));
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_user_tags;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        addFragment(R.id.fl_content, UserTagsFragment.newInstance());
    }

    @OnClick({R.id.ll_search, R.id.iv_add})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                SearchTagsActivity.show(this);
                break;
            case R.id.iv_add:
                SearchTagsActivity.show(this);
                break;
        }
    }
}
