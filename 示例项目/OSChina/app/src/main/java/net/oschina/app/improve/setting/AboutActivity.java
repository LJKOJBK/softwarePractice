package net.oschina.app.improve.setting;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.fragment.AboutOSCFragment;
import net.oschina.app.improve.base.activities.BackActivity;

/**
 * 关于界面
 * Created by huanghaibin on 2017/10/28.
 */

public class AboutActivity extends BackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
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
        addFragment(R.id.fl_content, new AboutOSCFragment());
    }
}
