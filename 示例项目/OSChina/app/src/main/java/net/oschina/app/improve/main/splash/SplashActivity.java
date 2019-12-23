package net.oschina.app.improve.main.splash;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.Launcher;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.main.introduce.IntroduceActivity;
import net.oschina.app.improve.main.tabs.DynamicTabFragment;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.utils.CacheManager;

import java.io.File;

import butterknife.Bind;

/**
 * 启动页设计
 * Created by huanghaibin on 2017/11/25.
 */

public class SplashActivity extends BaseActivity {


    @Bind(R.id.frameSplash)
    FrameLayout mFlameSplash;

    @Bind(R.id.fl_content)
    FrameLayout mFlameContent;

    private boolean isShowAd;

    public static void show(Context context) {
        context.startActivity(new Intent(context, SplashActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }


    @Override
    protected void initData() {
        super.initData();
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
        StatService.start(this);
        Launcher launcher = CacheManager.readJson(OSCApplication.getInstance(), "Launcher", Launcher.class);
        String savePath = OSCApplication.getInstance().getCacheDir() + "/launcher";
        File file = new File(savePath);
        if (launcher != null && !launcher.isExpired() && file.exists()) {
            isShowAd = true;
            mFlameSplash.setVisibility(View.GONE);
            mFlameContent.setVisibility(View.VISIBLE);
            addFragment(R.id.fl_content, AdFragment.newInstance(launcher));
        }
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                doMerge();
            }
        });
    }

    private void doMerge() {
        // 判断是否是新版本
        if (Setting.checkIsNewVersion(this)) {
            // Cookie迁移
            String cookie = OSCApplication.getInstance().getProperty("cookie");
            if (!TextUtils.isEmpty(cookie)) {
                OSCApplication.getInstance().removeProperty("cookie");
                User user = AccountHelper.getUser();
                user.setCookie(cookie);
                AccountHelper.updateUserCache(user);
                OSCApplication.reInit();
            }
        }

        // 栏目相关数据合并操作
        DynamicTabFragment.initTabPickerManager();

        if (isShowAd)
            return;
        // Delay...
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 完成后进行跳转操作
        redirectTo();
    }

    private void redirectTo() {
        if (OSCSharedPreference.getInstance().isFirstInstall()) {
            IntroduceActivity.show(this);
        } else {
            MainActivity.show(this);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
