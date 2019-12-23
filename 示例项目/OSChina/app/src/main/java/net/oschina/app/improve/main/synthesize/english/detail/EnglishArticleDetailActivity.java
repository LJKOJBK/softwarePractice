package net.oschina.app.improve.main.synthesize.english.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.detail.v2.ReportDialog;
import net.oschina.app.improve.main.ClipManager;
import net.oschina.app.improve.main.OnDoubleTouchListener;
import net.oschina.app.improve.main.synthesize.comment.ArticleCommentActivity;
import net.oschina.app.improve.main.synthesize.detail.CommentView;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.share.ShareDialog;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 英文详情界面
 * Created by huanghaibin on 2018/1/15.
 */

public class EnglishArticleDetailActivity extends BackActivity implements
        CommentView.OnCommentClickListener,
        EnglishArticleDetailContract.EmptyView,
        EasyPermissions.PermissionCallbacks,
        Runnable {


    private MenuItem mMenuTra;
    private CommentBar mDelegation;
    private String mCommentHint;
    private Article mArticle;
    private long mCommentId;
    private long mCommentAuthorId;
    private CommentShareView mShareView;
    private AlertDialog mShareCommentDialog;
    private ShareDialog mShareDialog;
    private EmptyLayout mEmptyLayout;
    private EnglishArticleDetailPresenter mPresenter;
    protected Comment mComment;
    protected boolean mInputDoubleEmpty = false;

    public static void show(Context context, Article article) {
        Intent intent = new Intent(context, EnglishArticleDetailActivity.class);
        intent.putExtra("article", article);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_english_article_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("all")
    @Override
    protected void initData() {
        super.initData();
        setStatusBarDarkMode();
        setDarkToolBar();
        mArticle = (Article) getIntent().getSerializableExtra("article");
        EnglishArticleDetailFragment mFragment = EnglishArticleDetailFragment.newInstance(mArticle);
        mPresenter = new EnglishArticleDetailPresenter(mFragment, this, mArticle);
        addFragment(R.id.fl_content, mFragment);

        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        if (TextUtils.isEmpty(mCommentHint))
            mCommentHint = getString(R.string.pub_comment_hint);
        mDelegation = CommentBar.delegation(this, layComment);
        mDelegation.setCommentHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        //mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mShareView = (CommentShareView) findViewById(R.id.shareView);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getArticleDetail();
                }
            }
        });

        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mDelegation.setFavListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    mPresenter.fav();
                } else {
                    LoginActivity.show(EnglishArticleDetailActivity.this, 1);
                }
            }
        });

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    UserSelectFriendsActivity.show(EnglishArticleDetailActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(EnglishArticleDetailActivity.this, 1);
                }
            }
        });


        mDelegation.setCommentCountListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleCommentActivity.show(EnglishArticleDetailActivity.this, mArticle);
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

        if (mShareView != null) {
            mShareCommentDialog = DialogHelper.getRecyclerViewDialog(this, new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    switch (position) {
                        case 0:
                            TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(mComment.getContent()));
                            break;
                        case 1:
                            if (!AccountHelper.isLogin()) {
                                LoginActivity.show(EnglishArticleDetailActivity.this, 1);
                                return;
                            }
                            if (mComment.getAuthor() == null || mComment.getAuthor().getId() == 0) {
                                SimplexToast.show(EnglishArticleDetailActivity.this, "该用户不存在");
                                return;
                            }
                            mCommentId = mComment.getId();
                            mCommentAuthorId = mComment.getAuthor().getId();
                            mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), mComment.getAuthor().getName()));
                            mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), mComment.getAuthor().getName()));
                            break;
                        case 2:
                            mShareView.init(mArticle.getTitle(), mComment);
                            saveToFileByPermission();
                            break;
                    }
                    mShareCommentDialog.dismiss();
                }
            }).create();
        }
        mDelegation.setCommentCount(mArticle.getCommentCount());

        mShareDialog = new ShareDialog(this);
        mShareDialog.setTitle(mArticle.getTitle());
        mShareDialog.init(this, mArticle.getTitle(), mArticle.getDesc(), mArticle.getUrl());
        if (mArticle.getImgs() != null && mArticle.getImgs().length != 0) {
            getImageLoader().load(mArticle.getImgs()[0])
                    .asBitmap()
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (isDestroy())
                                return false;
                            mShareDialog.setThumbBitmap(resource);
                            return false;
                        }
                    })
                    .into(Util.dipTopx(this, 80),
                            Util.dipTopx(this, 80));
        }


        mToolBar.setOnTouchListener(new OnDoubleTouchListener() {
            @Override
            public void onMultiTouch(View v, MotionEvent event, int touchCount) {
                if (touchCount == 2) {
                    mPresenter.scrollToTop();
                }
            }
        });
    }

    @Override
    public void showTranslateChange(boolean isEnglish) {
        dismissLoadingDialog();
        if (mMenuTra == null)
            return;
        mMenuTra.setIcon(isEnglish ? R.mipmap.ic_translate_en : R.mipmap.ic_translate);
    }

    @Override
    public void showTranslateFailure(String message) {
        dismissLoadingDialog();
    }

    @Override
    public void showReport() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(this);
            return;
        }
        if (mArticle != null) {
            ReportDialog.create(this, 0, mArticle.getUrl(), Report.TYPE_ARTICLE, mArticle.getKey()).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_english_detail, menu);
        mMenuTra = menu.getItem(0);
        if (mArticle == null || TextUtils.isEmpty(mArticle.getTitleTranslated())) {
            mMenuTra.setVisible(false);
            mMenuTra.setIcon(!TextUtils.isEmpty(mArticle.getTitleTranslated()) ? R.mipmap.ic_translate_en : R.mipmap.ic_translate);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mArticle != null) {
                    ClipManager.IS_SYSTEM_URL = true;
                    mShareDialog.show();
                }
                break;
            case R.id.menu_translate:
                if(!mPresenter.hasGetDetail()){
                    return false;
                }
                showLoadingDialog("正在获取内容...");
                mPresenter.translate();
                break;
        }
        return false;
    }


    @Override
    public void onClick(View view, Comment comment) {
        this.mComment = comment;
        if (mShareCommentDialog != null) {
            mShareCommentDialog.show();
        }
    }

    @Override
    public void run() {
        hideEmptyLayout();
    }

    @Override
    public void onShowComment(View view) {
        if (mDelegation != null) {
            mDelegation.getBottomSheet().show(mCommentHint);
        }
    }

    @Override
    public void hideEmptyLayout() {
        if (isDestroy()) {
            return;
        }
        dismissLoadingDialog();
        mEmptyLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mEmptyLayout.setVisibility(View.GONE);
            }
        }, 600);
    }

    @Override
    public void showErrorLayout(int errorType) {
        if (isDestroy()) {
            return;
        }
        dismissLoadingDialog();
        mEmptyLayout.setErrorType(errorType);
    }

    @Override
    public void showCommentSuccess(Comment comment) {
        if (isDestroyed())
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
        mDelegation.setCommentCount(mArticle.getCommentCount());
    }

    @Override
    public void showCommentError(String message) {
        if (isDestroy()) {
            return;
        }
        SimplexToast.show(this, message);
    }

    @Override
    public void showFavReverseSuccess(boolean isFav) {
        if (isDestroy()) {
            return;
        }
        mDelegation.setFavDrawable(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
    }

    @Override
    public void showFavError() {
        if (isDestroy()) {
            return;
        }
        SimplexToast.show(this, "收藏失败");
    }

    @Override
    public void showGetDetailSuccess(Article article) {
        if (isDestroy()) {
            return;
        }
        dismissLoadingDialog();
        mDelegation.setFavDrawable(article.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mDelegation.setCommentCount(article.getCommentCount());
    }

    private void handleKeyDel() {
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

    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            mShareView.share();
        } else {
            EasyPermissions.requestPermissions(this, "请授予文件读写权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                //finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
