package net.oschina.app.improve.detail;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.subscription.SubFragment;

/**
 * 活动界面
 * Created by huanghaibin on 2017/10/28.
 */

public class SubActivity extends BackActivity {

    public static void show(Context context, SubTab bean) {
        if (bean == null)
            return;
        Intent intent = new Intent(context, SubActivity.class);
        intent.putExtra("sub_tab", bean);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        SubTab tab = (SubTab) getIntent().getSerializableExtra("sub_tab");
        mToolBar.setTitle(tab.getName());
        setTitle(tab.getName());
        addFragment(R.id.fl_content, SubFragment.newInstance(tab));
    }
}
