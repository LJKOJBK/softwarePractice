package net.oschina.app.improve.main.synthesize.comment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 头条评论列表
 * Created by huanghaibin on 2017/10/28.
 */

public class ArticleCommentActivity extends BackActivity implements ArticleCommentContract.Action,
        ArticleCommentFragment.OnCommentClickListener,
        EasyPermissions.PermissionCallbacks {

    private String mMentionStr = "";
    protected CommentBar mDelegation;
    private ArticleCommentPresenter mPresenter;
    protected boolean mInputDoubleEmpty = false;
    @Bind(R.id.shareView)
    CommentShareView mShareView;
    private AlertDialog mShareCommentDialog;
    private Comment mComment;
    protected long mCommentId;
    protected long mCommentAuthorId;
    private Article mArticle;
    public static void show(AppCompatActivity activity, Article article) {
        Intent intent = new Intent(activity, ArticleCommentActivity.class);
        intent.putExtra("article", article);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_article_comment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mArticle = (Article) getIntent().getSerializableExtra("article");
        ArticleCommentFragment fragment = ArticleCommentFragment.newInstance();
        mPresenter = new ArticleCommentPresenter(fragment, this, mArticle);
        addFragment(R.id.fl_content, fragment);
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mDelegation = CommentBar.delegation(this, layComment);
        mDelegation.hideFav();
        mDelegation.hideCommentCount();
        mDelegation.getBottomSheet().hideSyncAction();
        mDelegation.getBottomSheet().hideMentionAction();
        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    UserSelectFriendsActivity.show(ArticleCommentActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(ArticleCommentActivity.this, 1);
                }
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
                mPresenter.putArticleComment(mMentionStr + mDelegation.getBottomSheet().getCommentText(), mCommentId, mCommentAuthorId);
                showLoadingDialog("正在发布评论...");
                mDelegation.getBottomSheet().dismiss();
            }
        });

        mShareCommentDialog = DialogHelper.getRecyclerViewDialog(this, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                switch (position) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(mComment.getContent()));
                        break;
                    case 1:
                        if (!AccountHelper.isLogin()) {
                            LoginActivity.show(ArticleCommentActivity.this, 1);
                            return;
                        }
                        if (mComment.getAuthor() == null ||
                                mComment.getAuthor().getId() == 0) {
                            SimplexToast.show(ArticleCommentActivity.this,"不能回复游客...");
                            return;
                        }
                        mDelegation.getBottomSheet().getBtnCommit().setTag(mComment);

                        mDelegation.getBottomSheet().show(String.format("%s %s",
                                getString(R.string.reply_hint), mComment.getAuthor().getName()));
                        break;
                    case 2:
                        mShareView.init(mArticle.getTitle(), mComment);
                        //mShareView.share();
                        saveToFileByPermission();
                        break;
                }
                mShareCommentDialog.dismiss();
            }
        }).create();

    }

    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        if (isDestroy())
            return;
        mCommentId = 0;
        mCommentAuthorId = 0;
        dismissLoadingDialog();
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint("发表评论");
        mMentionStr = "";
        mDelegation.getBottomSheet().dismiss();
        SimplexToast.show(this, strId);
    }

    @Override
    public void showAddCommentFailure(int strId) {
        if (isDestroy())
            return;
        dismissLoadingDialog();
    }

    @Override
    public void onClick(Comment comment) {
        mComment = comment;
        mShareCommentDialog.show();
        Author author = comment.getAuthor();
        if (author == null)
            return;
        mCommentId = comment.getId();
        mCommentAuthorId = author.getId();
    }

    protected void handleKeyDel() {
        if (!TextUtils.isEmpty(mMentionStr)) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mMentionStr = "";
                    mDelegation.setCommentHint("发表评论");
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        setResult(RESULT_OK);
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
