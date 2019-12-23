package net.oschina.app.improve.main.tags;

import android.content.Context;
import android.content.Intent;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;


/**
 * 选择标签界面
 * Created by haibin on 2018/6/11.
 */

public class SelectTagsActivity extends BaseActivity {

    public static void show(Context context) {
        Intent intent = new Intent(context, SelectTagsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_select_tags;
    }

    @Override
    public void onBackPressed() {

    }
}
