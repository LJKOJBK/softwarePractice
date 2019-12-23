package net.oschina.app.improve.main.synthesize.pub;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.update.OSCSharedPreference;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布提醒界面
 * Created by huanghaibin on 2017/12/5.
 */

public class PubTipActivity extends BaseActivity implements View.OnClickListener {

    private static boolean IS_SHOW = false;

    @Bind(R.id.tv_url)
    TextView mTextUrl;

    private String mUrl;

    public static void show(Context context, String url) {
        if(!AccountHelper.isLogin()){
            LoginActivity.show(context);
            return;
        }
        if (IS_SHOW || !OSCSharedPreference.getInstance().isRelateClip()) {
            return;
        }
        if (TextUtils.isEmpty(url) ||
                url.startsWith("https://www.oschina.net") ||
                url.startsWith("https://my.oschina.net")) {
            return;
        }
        IS_SHOW = true;
        Intent intent = new Intent(context, PubTipActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pub_tip;
    }


    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mUrl = getIntent().getStringExtra("url");
        mTextUrl.setText(mUrl);
    }

    @OnClick({R.id.btn_share, R.id.btn_cancel, R.id.fl_content})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                PubArticleActivity.show(this, mUrl);
                break;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IS_SHOW = false;
    }
}
