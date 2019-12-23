package net.oschina.app.improve.main.synthesize.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.detail.v2.ReportDialog;
import net.oschina.app.improve.main.ClipManager;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.comment.ArticleCommentActivity;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.util.TDevice;

/**
 * 头条浏览器
 * Created by huanghaibin on 2017/10/28.
 */

public class ArticleWebActivity extends WebActivity implements ArticleWebContract.View {
    private CommentBar mDelegation;
    protected boolean mInputDoubleEmpty = false;
    protected long mCommentId;
    protected long mCommentAuthorId;
    private Article mArticle;
    private String mCommentHint;
    private ArticleWebPresenter mPresenter;

    public static void show(Context context, Article article) {
        if (!TDevice.hasWebView(context))
            return;
        if (article == null)
            return;
        Intent intent = new Intent(context, ArticleWebActivity.class);
        intent.putExtra("article", article);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_article_web;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mArticle = (Article) getIntent().getSerializableExtra("article");
        mPresenter = new ArticleWebPresenter(this, mArticle);
        mCommentHint = getString(R.string.pub_comment_hint);
        mDelegation = CommentBar.delegation(this, mLinearRoot);
        mDelegation.setCommentHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        mDelegation.setFavListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.fav();
            }
        });
        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    UserSelectFriendsActivity.show(ArticleWebActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(ArticleWebActivity.this, 1);
                }
            }
        });

        mDelegation.setCommentCountListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleCommentActivity.show(ArticleWebActivity.this, mArticle);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
        mDelegation.setCommentCount(mArticle.getCommentCount());
        mDelegation.getBottomSheet().hideSyncAction();
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog("正在提交评论...");
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(false);
                mPresenter.putArticleComment(mDelegation.getBottomSheet().getCommentText(),
                        mCommentId,
                        mCommentAuthorId
                );
            }
        });
        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapterV2(this));

    }

    @Override
    protected void initData() {
        super.initData();
        mToolBar.setTitle("返回");
        mShareDialog.setTitle(mArticle.getTitle());
        mShareDialog.init(this, mArticle.getTitle(), mArticle.getDesc(), mArticle.getUrl());
        //mWebView.loadUrl(TypeFormat.formatUrl(mArticle));
        getRule(TypeFormat.formatUrl(mArticle));
    }

    @Override
    public void showFavReverseSuccess(boolean isFav) {
        if (isDestroyed()) {
            return;
        }
        mDelegation.setFavDrawable(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
    }

    @Override
    public void showFavError() {

    }

    @Override
    public void showNetworkError(int strId) {
        if (isDestroy())
            return;
        dismissLoadingDialog();
        SimplexToast.show(this, "评论失败");
    }

    @Override
    public void showCommentSuccess(Comment comment) {
        if (isDestroy())
            return;
        if (mDelegation == null)
            return;
        mCommentId = 0;
        mCommentAuthorId = 0;
        mDelegation.getBottomSheet().dismiss();
        mDelegation.setCommitButtonEnable(true);
        AppContext.showToastShort(R.string.pub_comment_success);
        mDelegation.getCommentText().setHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        mDelegation.getBottomSheet().dismiss();
        dismissLoadingDialog();
        SimplexToast.show(this, "评论成功");
    }

    @Override
    public void showCommentError(String message) {
        if (isDestroy())
            return;
        dismissLoadingDialog();
        SimplexToast.show(this, "评论失败");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_web_artivle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                ClipManager.IS_SYSTEM_URL = true;
                mShareDialog.show();
                break;
            case R.id.menu_report:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this);
                    return false;
                }
                if (mArticle != null) {
                    ReportDialog.create(this, 0, TypeFormat.formatUrl(mArticle), Report.TYPE_ARTICLE, mArticle.getKey()).show();
                }
                break;
        }
        return false;
    }

    protected void handleKeyDel() {
        if (mCommentId != 0) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = 0;
                    mCommentAuthorId = 0;
                    mDelegation.setCommentHint(mCommentHint);
                    mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }

    @Override
    public void setPresenter(ArticleWebContract.Presenter presenter) {
        // TODO: 2017/10/30
    }

    @Override
    protected void onStop() {
        super.onStop();
        ClipManager.IS_SYSTEM_URL = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClipManager.IS_SYSTEM_URL = false;
    }
}
