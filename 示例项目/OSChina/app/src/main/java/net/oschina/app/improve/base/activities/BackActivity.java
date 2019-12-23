package net.oschina.app.improve.base.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.oschina.app.R;
import net.oschina.app.improve.utils.DialogHelper;

/**
 * Created by haibin
 * on 2016/12/1.
 */

public abstract class BackActivity extends BaseActivity {
    protected Toolbar mToolBar;
    private ProgressDialog mWaitDialog;

    @Override
    protected void initWindow() {
        super.initWindow();
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(false);
            }
        }
    }


    @SuppressWarnings("ConstantConditions")
    protected void setDarkToolBar() {
        if (mToolBar != null) {
            mToolBar.setTitleTextColor(Color.BLACK);
            DrawableCompat.setTint(mToolBar.getNavigationIcon(), Color.BLACK);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    protected void showLoadingDialog(String message) {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(this, true);
        }
        mWaitDialog.setMessage(message);
        mWaitDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (mWaitDialog == null) return;
        mWaitDialog.dismiss();
    }
}
