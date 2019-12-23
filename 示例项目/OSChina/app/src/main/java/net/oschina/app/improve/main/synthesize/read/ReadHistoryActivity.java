package net.oschina.app.improve.main.synthesize.read;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;

/**
 * 阅读记录
 * Created by huanghaibin on 2017/12/4.
 */

public class ReadHistoryActivity extends BackActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, ReadHistoryActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_read_history;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setDarkToolBar();
        setStatusBarDarkMode();
        ReadHistoryFragment fragment = ReadHistoryFragment.newInstance();
        addFragment(R.id.fl_content, fragment);
        new ReadHistoryPresenter(fragment);
    }
}
