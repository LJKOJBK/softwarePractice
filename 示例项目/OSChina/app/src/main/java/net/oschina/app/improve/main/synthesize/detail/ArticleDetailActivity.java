package net.oschina.app.improve.main.synthesize.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
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
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.detail.v2.ReportDialog;
import net.oschina.app.improve.main.ClipManager;
import net.oschina.app.improve.main.synthesize.comment.ArticleCommentActivity;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.share.ShareDialog;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.NetworkUtil;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 头条详情
 * Created by huanghaibin on 2017/10/23.
 */

public class ArticleDetailActivity extends BackActivity implements
        CommentView.OnCommentClickListener,
        ArticleDetailContract.EmptyView,
        EasyPermissions.PermissionCallbacks {
    private CommentBar mDelegation;
    private String mCommentHint;
    private Article mArticle;
    protected long mCommentId;
    protected long mCommentAuthorId;
    protected boolean mInputDoubleEmpty = false;
    private ArticleDetailPresenter mPresenter;
    protected EmptyLayout mEmptyLayout;
    protected CommentShareView mShareView;
    private AlertDialog mShareCommentDialog;
    protected ShareDialog mShareDialog;
    protected Comment mComment;
    protected Behavior mBehavior;
    private long mStart;
    protected long mStay;//该界面停留时间
    private ArticleDetailFragment mFragment;

    public static void show(Context context, Article article) {
        if (article == null)
            return;
        Intent intent = new Intent(context, ArticleDetailActivity.class);
        intent.putExtra("article", article);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_article_detail;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mArticle = (Article) getIntent().getSerializableExtra("article");
        mFragment = ArticleDetailFragment.newInstance(mArticle);
        mPresenter = new ArticleDetailPresenter(mFragment, this, mArticle);
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
                    //mPresenter.getDetail();
                }
            }
        });

        mDelegation.setFavListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    mPresenter.fav();
                } else {
                    LoginActivity.show(ArticleDetailActivity.this, 1);
                }
            }
        });

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    UserSelectFriendsActivity.show(ArticleDetailActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(ArticleDetailActivity.this, 1);
                }
            }
        });


        mDelegation.setCommentCountListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleCommentActivity.show(ArticleDetailActivity.this, mArticle);
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
                                LoginActivity.show(ArticleDetailActivity.this, 1);
                                return;
                            }
                            if (mComment.getAuthor() == null || mComment.getAuthor().getId() == 0) {
                                SimplexToast.show(ArticleDetailActivity.this, "该用户不存在");
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
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("all")
    @Override
    protected void initData() {
        super.initData();
        if (AccountHelper.isLogin() &&
                DBManager.getInstance()
                        .getCount(Behavior.class) >= 3) {
            mPresenter.uploadBehaviors(DBManager.getInstance().get(Behavior.class));
        }
        mBehavior = new Behavior();
        mBehavior.setUser(AccountHelper.getUserId());
        mBehavior.setUserName(AccountHelper.getUser().getName());
        mBehavior.setNetwork(NetworkUtil.getNetwork(this));
        mBehavior.setUrl(mArticle.getUrl());
        mBehavior.setOperateType(mArticle.getType());
        mBehavior.setOperateTime(System.currentTimeMillis());
        mStart = mBehavior.getOperateTime();
        mBehavior.setOperation("read");
        mBehavior.setDevice(android.os.Build.MODEL);
        mBehavior.setVersion(TDevice.getVersionName());
        mBehavior.setOs(android.os.Build.VERSION.RELEASE);
        mBehavior.setKey(mArticle.getKey());
        mBehavior.setUuid(OSCSharedPreference.getInstance().getDeviceUUID());
        // TODO: 2017/11/6 暂时取消收藏习惯接口 
        //DBManager.getInstance().insert(mBehavior);


        mToolBar.setOnTouchListener(new OnDoubleTouchListener() {
            @Override
            void onMultiTouch(View v, MotionEvent event, int touchCount) {
                if (touchCount == 2) {
                    mPresenter.scrollToTop();
                }
            }
        });
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
    public void onClick(View view, Comment comment) {
        this.mComment = comment;
        if (mShareCommentDialog != null) {
            mShareCommentDialog.show();
        }
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
    public void onShowComment(View view) {
        if (mDelegation != null) {
            mDelegation.getBottomSheet().show(mCommentHint);
        }
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
        if (isDestroy())
            return;
        dismissLoadingDialog();
        SimplexToast.show(this, "评论失败");
    }

    @Override
    public void showGetDetailSuccess(Article article) {
        if (isDestroy())
            return;
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mDelegation.setFavDrawable(article.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mDelegation.setCommentCount(article.getCommentCount());
    }

    @Override
    public void showErrorLayout(int errorType) {
        if (isDestroy()) {
            return;
        }
        mEmptyLayout.setErrorType(errorType);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog_detail, menu);
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
            case R.id.menu_report:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this);
                    return false;
                }
                if (mArticle != null) {
                    ReportDialog.create(this, 0, mArticle.getUrl(), Report.TYPE_ARTICLE, mArticle.getKey()).show();
                }
                break;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mStart != 0)
            mStart = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ClipManager.IS_SYSTEM_URL = false;
        mStay += (System.currentTimeMillis() - mStart) / 1000;
        if (mBehavior != null) {
            mBehavior.setStay(mStay);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && mFragment != null) {
            mFragment.onRefreshing();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClipManager.IS_SYSTEM_URL = false;
    }

    private abstract class OnDoubleTouchListener implements View.OnTouchListener {
        private long lastTouchTime = 0;
        private AtomicInteger touchCount = new AtomicInteger(0);
        private Runnable mRun = null;
        private Handler mHandler;

        OnDoubleTouchListener() {
            mHandler = new Handler(getMainLooper());
        }

        void removeCallback() {
            if (mRun != null) {
                mHandler.removeCallbacks(mRun);
                mRun = null;
            }
        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final long now = System.currentTimeMillis();
                lastTouchTime = now;

                touchCount.incrementAndGet();
                removeCallback();

                mRun = new Runnable() {
                    @Override
                    public void run() {
                        if (now == lastTouchTime) {
                            onMultiTouch(v, event, touchCount.get());
                            touchCount.set(0);
                        }
                    }
                };

                mHandler.postDelayed(mRun, getMultiTouchInterval());
            }
            return true;
        }


        int getMultiTouchInterval() {
            return 400;
        }


        abstract void onMultiTouch(View v, MotionEvent event, int touchCount);
    }
}
