package net.oschina.app.improve.main.synthesize.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.share.ShareDialog;
import net.oschina.app.improve.widget.OSCWebView;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * WebView
 * Created by huanghaibin on 2017/10/27.
 */

public class ZBWebActivity extends BackActivity implements OSCWebView.OnFinishListener {

    protected OSCWebView mWebView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.ll_root)
    LinearLayout mLinearRoot;
    private String mTitle;
    protected ShareDialog mShareDialog;
    private String mUrl;
    private boolean isShowAd;
    private boolean isWebViewFinish;

    public static void show(Context context, String url) {
        if (!TDevice.hasWebView(context))
            return;
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent(context, ZBWebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void show(Context context, String url, boolean isShowAd) {
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent(context, ZBWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isShowAd", isShowAd);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_web;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget() {
        super.initWidget();

        mWebView = new OSCWebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setLayoutParams(params);
        mLinearRoot.addView(mWebView);
        mWebView.setOnFinishFinish(this);

        mUrl = getIntent().getStringExtra("url");
        isShowAd = getIntent().getBooleanExtra("isShowAd", false);
        setStatusBarDarkMode();
        setSwipeBackEnable(true);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(false);
            }
            mToolBar.setTitleTextColor(Color.BLACK);
            DrawableCompat.setTint(mToolBar.getNavigationIcon(), Color.BLACK);
        }
        mShareDialog = new ShareDialog(this);
        if (!TextUtils.isEmpty(mUrl))
            getRule(mUrl);
    }

    protected void getRule(final String url) {
        OSChinaApi.getWebRule(url,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (isDestroyed())
                            return;
                        mWebView.loadUrl(url);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (isDestroyed())
                            return;
                        try {
                            Type type = new TypeToken<ResultBean<Rule>>() {
                            }.getType();
                            ResultBean<Rule> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                mWebView.setRule(bean.getResult());
                                mWebView.loadUrl(url);
                            } else {
                                mWebView.loadUrl(url);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mWebView.loadUrl(url);
                        }
                    }
                });
    }


    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mUrl)) {
                    mShareDialog.init(this, mWebView.getTitle(),
                            " ",
                            mWebView.getUrl());
                    mShareDialog.setThumbBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_share_zb));
                    mShareDialog.show();
                }
                break;
        }
        return false;
    }

    @Override
    public void onReceivedTitle(String title) {
        if (isDestroyed())
            return;
        mShareDialog.setTitle(title);
        mTitle = title;
        mShareDialog.init(this, title, " ", mWebView.getUrl());
    }

    @Override
    public void onProgressChange(int progress) {
        if (isDestroyed())
            return;
        mProgressBar.setProgress(progress);
        if(!mWebView.hasRule()){
            mWebView.setVisibility(View.VISIBLE);
            return;
        }
        if (progress >= 60 && !isWebViewFinish) {
            isWebViewFinish = true;
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isDestroyed())
                        return;
                    mWebView.setVisibility(View.VISIBLE);
                }
            }, 800);
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        if (isDestroyed())
            return;
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void finish() {
        super.finish();
        if (isShowAd) {
            MainActivity.show(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.onDestroy();
        }
    }
}
