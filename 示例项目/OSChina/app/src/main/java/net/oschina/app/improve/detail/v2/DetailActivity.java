package net.oschina.app.improve.detail.v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.NetworkUtil;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 新版本详情页实现
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailActivity extends BackActivity implements
        DetailContract.EmptyView, Runnable,
        CommentView.OnCommentClickListener, EasyPermissions.PermissionCallbacks {

    protected String mCommentHint;
    protected DetailPresenter mPresenter;
    protected EmptyLayout mEmptyLayout;
    protected DetailFragment mDetailFragment;
    protected ShareDialog mAlertDialog;

    protected long mStay;//该界面停留时间
    private long mStart;
    protected Behavior mBehavior;
    @SuppressWarnings("unused")
    private boolean isInsert;

    protected CommentBar mDelegation;

    protected SubBean mBean;
    protected String mIdent;

    protected long mCommentId;
    protected long mCommentAuthorId;
    protected boolean mInputDoubleEmpty = false;

    protected CommentShareView mShareView;
    private AlertDialog mShareCommentDialog;
    protected Comment mComment;

    @Override
    protected int getContentView() {
        return R.layout.activity_detail_v2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("all")
    @Override
    protected void initWidget() {
        super.initWidget();
        setSwipeBackEnable(true);
        DBManager.getInstance()
                .create(Behavior.class);
        DBManager.getInstance()
                .alter(Behavior.class);
        CommentShareView.clearShareImage();
        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }
        if (TextUtils.isEmpty(mCommentHint))
            mCommentHint = getString(R.string.pub_comment_hint);
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mShareView = (CommentShareView) findViewById(R.id.shareView);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getDetail();
                }
            }
        });
        mBean = (SubBean) getIntent().getSerializableExtra("sub_bean");
        mIdent = getIntent().getStringExtra("ident");
        mDetailFragment = getDetailFragment();
        addFragment(R.id.lay_container, mDetailFragment);
        mPresenter = new DetailPresenter(mDetailFragment, this, mBean, mIdent);
        if (!mPresenter.isHideCommentBar()) {
            mDelegation = CommentBar.delegation(this, layComment);
            mDelegation.setCommentHint(mCommentHint);
            mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
            mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);

            mDelegation.setFavListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountHelper.isLogin()) {
                        LoginActivity.show(DetailActivity.this);
                        return;
                    }
                    mPresenter.favReverse();
                }
            });

            mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((AccountHelper.isLogin())) {
                        UserSelectFriendsActivity.show(DetailActivity.this, mDelegation.getBottomSheet().getEditText());
                    } else {
                        LoginActivity.show(DetailActivity.this, 1);
                    }
                }
            });

            mDelegation.setCommentCountListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentsActivity.show(DetailActivity.this, mBean.getId(), mBean.getType(), OSChinaApi.COMMENT_NEW_ORDER, mBean.getTitle());
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
            mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // showDialog("正在提交评论...");
                    if (mDelegation == null) return;
                    mDelegation.getBottomSheet().dismiss();
                    mDelegation.setCommitButtonEnable(false);
                    mPresenter.addComment(mBean.getId(),
                            mBean.getType(),
                            mDelegation.getBottomSheet().getCommentText(),
                            0,
                            mCommentId,
                            mCommentAuthorId);
                }
            });
            mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapterV2(this));
        }
        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getCache();
                mPresenter.getDetail();
            }
        });
        if (mToolBar != null)

            mToolBar.setOnTouchListener(new OnDoubleTouchListener() {
                @Override
                void onMultiTouch(View v, MotionEvent event, int touchCount) {
                    if (touchCount == 2) {
                        mPresenter.scrollToTop();
                    }
                }
            });

        if (AccountHelper.isLogin() &&
                DBManager.getInstance()
                        .getCount(Behavior.class) >= 3) {
            mPresenter.uploadBehaviors(DBManager.getInstance().get(Behavior.class));
        }
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
                                LoginActivity.show(DetailActivity.this, 1);
                                return;
                            }
                            if (mComment.getAuthor() == null || mComment.getAuthor().getId() == 0) {
                                SimplexToast.show(DetailActivity.this, "该用户不存在");
                                return;
                            }
                            mCommentId = mComment.getId();
                            mCommentAuthorId = mComment.getAuthor().getId();
                            mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), mComment.getAuthor().getName()));
                            mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), mComment.getAuthor().getName()));
                            break;
                        case 2:
                            mShareView.init(mBean.getTitle(), mComment);
                            saveToFileByPermission();
                            break;
                    }
                    mShareCommentDialog.dismiss();
                }
            }).create();
        }
    }

    private void initBehavior() {
        if (AccountHelper.isLogin() && mBean.getType() != News.TYPE_EVENT && !isInsert) {
            mBehavior = new Behavior();
            mBehavior.setUser(AccountHelper.getUserId());
            mBehavior.setUserName(AccountHelper.getUser().getName());
            mBehavior.setNetwork(NetworkUtil.getNetwork(this));
            mBehavior.setUrl(mBean.getHref());
            mBehavior.setOperateType(mBean.getType());
            mBehavior.setOperateTime(System.currentTimeMillis());
            mStart = mBehavior.getOperateTime();
            mBehavior.setOperation("read");
            mBehavior.setDevice(android.os.Build.MODEL);
            mBehavior.setVersion(TDevice.getVersionName());
            mBehavior.setOs(android.os.Build.VERSION.RELEASE);
            mBehavior.setKey(String.format("%s_%s_%s", "osc", mBean.getType(), mBean.getId()));
            mBehavior.setUuid(OSCSharedPreference.getInstance().getDeviceUUID());
            // TODO: 2017/11/6 暂时取消收藏接口 
//            isInsert = DBManager.getInstance()
//                    .insert(mBehavior);
        }
    }


    @Override
    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    @Override
    public void showErrorLayout(int errorType) {
        mEmptyLayout.setErrorType(errorType);
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        this.mBean = bean;
        initBehavior();
        if (mDelegation != null) {
            if (bean.getStatistics() != null) {
                mDelegation.setCommentCount(bean.getStatistics().getComment());
            }
            mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        }
        if (mEmptyLayout != null) {
            mEmptyLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mEmptyLayout != null)
                        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
            }, 2000);
        }
    }

    @Override
    public void run() {
        hideEmptyLayout();
        mDetailFragment.onPageFinished();
    }


    @Override
    public void showFavReverseSuccess(boolean isFav, int favCount, int strId) {
        if (mBehavior != null) {
            mBehavior.setIsCollect(isFav ? 1 : 0);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
        }
        if (mDelegation != null) {
            mDelegation.setFavDrawable(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
        }
    }

    @Override
    public void showCommentSuccess(Comment comment) {
        //hideDialog();
        if (mBehavior != null) {
            mBehavior.setIsComment(1);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
        }
        if (mDelegation == null)
            return;
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            TweetPublishService.startActionPublish(this,
                    mDelegation.getBottomSheet().getCommentText(), null,
                    About.buildShare(mBean.getId(), mBean.getType()));
        }
        mDelegation.getBottomSheet().dismiss();
        mDelegation.setCommitButtonEnable(true);
        AppContext.showToastShort(R.string.pub_comment_success);
        mDelegation.getCommentText().setHint(mCommentHint);
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint(mCommentHint);
        mDelegation.getBottomSheet().dismiss();
        mDelegation.setCommentCount(mBean.getStatistics().getComment());
    }

    @Override
    public void showShareCommentView(Comment comment) {
        if (mShareView == null)
            return;
        mShareView.init(mBean.getTitle(), comment);
    }

    @Override
    public void showCommentError(String message) {
        //hideDialog();
        AppContext.showToastShort(R.string.pub_comment_failed);
        mDelegation.setCommitButtonEnable(true);
    }

    @Override
    public void showUploadBehaviorsSuccess(int index, String time) {
        DBManager.getInstance()
                .delete(Behavior.class, "id<=?", String.valueOf(index));
        AppConfig.getAppConfig(this)
                .set("upload_behavior_time", time);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mBean != null) {
                    if (mBean.getType() != News.TYPE_SOFTWARE) {
                        toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                    } else {
                        Map<String, Object> extras = mBean.getExtra();
                        if (extras != null) {
                            toShare(getExtraString(extras.get("softwareTitle")) + "   " + getExtraString(extras.get("softwareName")), mBean.getBody(), mBean.getHref());
                        }
                    }
                }
                break;
            case R.id.menu_report:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this);
                    return false;
                }
                toReport(mBean.getId(), mBean.getHref());
                break;
        }
        return false;
    }

    protected void toReport(long id, String href) {
        ReportDialog.create(this, id, href, Report.TYPE_BLOG, "").show();
    }

    @SuppressWarnings({"LoopStatementThatDoesntLoop", "SuspiciousMethodCalls"})
    protected void toShare(String title, String content, String url) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(url) || mBean == null)
            return;
        if (content == null)
            content = "";
        String imageUrl = null;
        List<SubBean.Image> images = mBean.getImages();
        switch (mBean.getType()) {
            case News.TYPE_EVENT:
                if (images != null && images.size() > 0) {
                    imageUrl = images.get(0).getHref();
                }
                break;
            case News.TYPE_SOFTWARE:
                if (images != null && images.size() > 0) {
                    imageUrl = images.get(0).getThumb();
                    if (imageUrl != null && imageUrl.contains("logo/default.png")) {
                        imageUrl = null;
                    }
                    break;
                }
            default:
                String regex = "<img[^>]+\\s?src=\"([^\"]+)\"\\s?[^>]*>";

                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(mBean.getBody());

                while (matcher.find()) {
                    imageUrl = matcher.group(1);
                    break;
                }
                break;
        }
        content = content.trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";

        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new
                    ShareDialog(this, mBean.getId(), (mBean.getType() == News.TYPE_BLOG || mBean.getType() == News.TYPE_NEWS))
                    .type(mBean.getType())
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
            mAlertDialog.setBean(mBean);
        }
        mAlertDialog.show();
        if (mBehavior != null) {
            mBehavior.setIsShare(1);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
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
    public void onShowComment(View view) {
        if (mDelegation != null) {
            mDelegation.getBottomSheet().show(mCommentHint);
        }
    }

    protected void handleKeyDel() {
        if (mCommentId != mBean.getId()) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mBean.getId();
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

    @SuppressWarnings("all")
    protected boolean getExtraBool(Object object) {
        return object == null ? false : Boolean.valueOf(object.toString());
    }

    protected int getExtraInt(Object object) {
        return object == null ? 0 : Double.valueOf(object.toString()).intValue();
    }

    @SuppressWarnings("all")
    protected String getExtraString(Object object) {
        return object == null ? "" : object.toString();
    }


    protected abstract DetailFragment getDetailFragment();

    @Override
    public void finish() {
        if (mEmptyLayout != null && mEmptyLayout.getErrorState() == EmptyLayout.HIDE_LAYOUT)
            DetailCache.addCache(mBean);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mShareCommentDialog != null)
            mShareCommentDialog.dismiss();
        if (mStart != 0)
            mStart = System.currentTimeMillis();
        if (mAlertDialog == null)
            return;
        mAlertDialog.hideProgressDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mShareCommentDialog != null) {
            mShareCommentDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStay += (System.currentTimeMillis() - mStart) / 1000;
        if (mBehavior != null) {
            mBehavior.setStay(mStay);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && mBehavior != null) {
            mBehavior.setIsComment(1);
            DBManager.getInstance()
                    .update(mBehavior, "operate_time=?", String.valueOf(mBehavior.getOperateTime()));
        }
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

    @Override
    public void onBackPressed() {
        if(!MainActivity.IS_SHOW){
            MainActivity.show(this);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!MainActivity.IS_SHOW){
            MainActivity.show(this);
        }
    }
}
