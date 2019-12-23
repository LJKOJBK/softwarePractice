package net.oschina.app.improve.setting;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;

/**
 * 设置界面
 * Created by huanghaibin on 2017/10/27.
 */

public class SettingActivity extends BackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
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
        addFragment(R.id.fl_content, SettingFragment.newInstance());
    }
}
