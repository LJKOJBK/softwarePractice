package net.oschina.app.improve.main.synthesize.pub;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.main.ClipManager;
import net.oschina.app.improve.widget.OSCWebView;
import net.oschina.app.improve.widget.SimplexToast;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布分享文章
 * Created by huanghaibin on 2017/12/1.
 */

public class PubArticleActivity extends BackActivity implements PubArticleContract.View,
        OSCWebView.OnFinishListener,
        View.OnClickListener {

    @Bind(R.id.et_url)
    EditText mTextUrl;

    @Bind(R.id.tv_title)
    TextView mTextTitle;

    @Bind(R.id.fl_web)
    FrameLayout mFrameWebView;

    OSCWebView mWebView;

    private PubArticlePresenter mPresenter;

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, PubArticleActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pub_article;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setDarkToolBar();
        setStatusBarDarkMode();
        mWebView = new OSCWebView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0);
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setLayoutParams(params);
        mWebView.setOnFinishFinish(this);
        mWebView.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36");
        mFrameWebView.addView(mWebView);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();

        String mUrl = "";
        String action = intent.getAction();//action
        String type = intent.getType();//类型

        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            try {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (!TextUtils.isEmpty(text)) {
                    mUrl = PubArticlePresenter.findUrl(text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mUrl = "";
            }

        } else {
            mUrl = intent.getStringExtra("url");
        }

        if (TextUtils.isEmpty(mUrl)) {
            mUrl = ClipManager.getClipUrl();
        }
        mTextUrl.setText(mUrl);
        mPresenter = new PubArticlePresenter(this);
        mTextUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (PubArticlePresenter.checkUrl(s.toString()) && mWebView != null) {
                    if(mPresenter.isWechatUrl(s.toString())){
                        mPresenter.getTitle(s.toString());
                    }else {
                        mWebView.loadUrl(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(mPresenter.isWechatUrl(mUrl)){
            mPresenter.getTitle(mUrl);
        }else {
            mWebView.loadUrl(mUrl);
        }
    }

    @OnClick({R.id.btn_commit})
    @Override
    public void onClick(View v) {
        mPresenter.putArticle(mTextUrl.getText().toString().trim(), "");
    }


    @Override
    public void onReceivedTitle(String title) {
        if (isDestroy())
            return;
        mTextTitle.setText(title);
    }

    @Override
    public void onProgressChange(int progress) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void showGetTitleSuccess(String title) {
        if (isDestroy())
            return;
        mTextTitle.setText(title);
    }

    @Override
    public void showGetTitleFailure(String message) {
        if (isDestroy())
            return;
        mTextTitle.setText(message);
    }

    @Override
    public void showPubSuccess(int strId) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, strId);
        finish();
    }

    @Override
    public void showPubFailure(String message) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, message);
    }


    @Override
    public void setPresenter(PubArticleContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.onDestroy();
        }
    }
}
