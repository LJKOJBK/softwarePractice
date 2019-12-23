package net.oschina.app.improve.comment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.adapter.CommentAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.CommentShareView;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by  fei
 * on  16/11/17
 * desc:详情评论列表ui
 */
public class CommentsActivity extends BackActivity implements
        BaseRecyclerAdapter.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks {

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    @Bind(R.id.activity_comments)
    LinearLayout mCoordinatorLayout;


    @Bind(R.id.shareView)
    CommentShareView mShareView;
    private CommentAdapter mCommentAdapter;

    private CommentBar mDelegation;
    private boolean mInputDoubleEmpty = true;
    private boolean isAddCommented;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(false);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (isDestroy())
                return;
            AppContext.showToastShort(getResources().getString(R.string.pub_comment_failed));
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            if (isDestroy())
                return;
            try {
                Type type = new TypeToken<ResultBean<Comment>>() {
                }.getType();

                ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    isAddCommented = true;
                    Comment respComment = resultBean.getResult();
                    if (respComment != null) {
                        handleSyncTweet();
                        mDelegation.setCommentHint(getString(mSourceId));
                        mDelegation.getBottomSheet().getEditText().setHint(getString(mSourceId));
                        AppContext.showToastShort(getString(R.string.pub_comment_success));
                        mDelegation.getBottomSheet().getEditText().setText("");
                        mDelegation.getBottomSheet().getBtnCommit().setTag(null);
                        mDelegation.getBottomSheet().dismiss();
                        getData(true, null);
                    }
                } else {
                    AppContext.showToastShort(resultBean.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (isDestroy())
                return;
            if (mDelegation == null) return;
            mDelegation.getBottomSheet().dismiss();
            mDelegation.setCommitButtonEnable(true);
        }
    };

    private int mOrder;
    private int mSourceId;

    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;
    private AlertDialog mShareCommentDialog;
    private Comment mComment;
    private String mShareTitle;

    public static void show(Activity activity, long id, int type, int order, String title) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("order", order);
        intent.putExtra("title", title);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong("id");
        mType = bundle.getInt("type");
        mOrder = bundle.getInt("order");
        mShareTitle = bundle.getString("title");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mShareCommentDialog = DialogHelper.getRecyclerViewDialog(this, new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                switch (position) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(mComment.getContent()));
                        break;
                    case 1:
                        if (!AccountHelper.isLogin()) {
                            LoginActivity.show(CommentsActivity.this, 1);
                            return;
                        }
                        if (mComment.getAuthor() == null ||
                                mComment.getAuthor().getId() == 0) {
                            SimplexToast.show(CommentsActivity.this,"不能回复游客...");
                            return;
                        }
                        mDelegation.getBottomSheet().getBtnCommit().setTag(mComment);

                        mDelegation.getBottomSheet().show(String.format("%s %s",
                                getString(R.string.reply_hint), mComment.getAuthor().getName()));
                        break;
                    case 2:
                        mShareView.init(mShareTitle, mComment);
                        //mShareView.share();
                        saveToFileByPermission();
                        break;
                }
                mShareCommentDialog.dismiss();
            }
        }).create();


        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);
        mSourceId = R.string.pub_comment_hint;
        if (mType == OSChinaApi.COMMENT_QUESTION) {
            mSourceId = R.string.answer_hint;
        }
        if (mType == OSChinaApi.COMMENT_EVENT) {
            mSourceId = R.string.comment_hint;
        }
        mDelegation.getBottomSheet().getEditText().setHint(mSourceId);
        mDelegation.hideFav();
        mDelegation.hideCommentCount();

        if (mType == OSChinaApi.COMMENT_SOFT) {
            mDelegation.getBottomSheet().hideSyncAction();
        }

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    UserSelectFriendsActivity.show(CommentsActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(CommentsActivity.this);
                }
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapterV2(this));

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText view = (EditText) v;
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Button mBtnView = mDelegation.getBottomSheet().getBtnCommit();
                    Object o = mBtnView.getTag();
                    if (o == null) return false;
                    if (!TextUtils.isEmpty(view.getText().toString())) {
                        mInputDoubleEmpty = false;
                        return false;
                    }
                    if (TextUtils.isEmpty(view.getText().toString()) && !mInputDoubleEmpty) {
                        mInputDoubleEmpty = true;
                        return false;
                    }
                    mBtnView.setTag(null);
                    view.setHint(mSourceId);
                    return true;
                }
                return false;
            }
        });

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(mType, mId, (Comment) v.getTag(), mDelegation.getBottomSheet().getCommentText());
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mCommentAdapter = new CommentAdapter(this, getImageLoader(), BaseRecyclerAdapter.ONLY_FOOTER);
        mCommentAdapter.setSourceId(mId);
        mCommentAdapter.setCommentType(mType);
        mCommentAdapter.setDelegation(mDelegation);
        //mCommentAdapter.setOnItemLongClickListener(this);
        mCommentAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mComment = mCommentAdapter.getItem(position);
                if (mType == OSChinaApi.COMMENT_QUESTION) {
                    QuesAnswerDetailActivity.show(CommentsActivity.this, mComment, mId, mType);
                } else {
                    mShareCommentDialog.show();
                }
            }
        });
        mLayComments.setAdapter(mCommentAdapter);
    }

    @Override
    protected void initData() {
        super.initData();

        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true, null);
            }

            @Override
            public void onLoadMore() {
                String token = null;
                if (mPageBean != null)
                    token = mPageBean.getNextPageToken();
                getData(false, token);
            }

            @Override
            public void onScrollToBottom() {

            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //第一次请求初始化数据
                getData(true, null);

            }
        });

    }

    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private long requestCheck() {
        if (mId == 0) {
            AppContext.showToast(getResources().getString(R.string.state_loading_error));
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AccountHelper.getUserId();
    }


    /**
     * sync the tweet
     */
    private void handleSyncTweet() {
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            TweetPublishService.startActionPublish(CommentsActivity.this,
                    mDelegation.getBottomSheet().getCommentText(), null,
                    About.buildShare(mId, mType));
        }
    }

    /**
     * handle send comment
     */
    private void sendComment(int type, long id, Comment comment, String content) {
        if (requestCheck() == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        long uid = comment == null ? 0 : comment.getAuthor().getId();
        long cid = comment == null ? 0 : comment.getId();

        switch (type) {
            case OSChinaApi.COMMENT_QUESTION:
                OSChinaApi.pubQuestionComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_BLOG:
                OSChinaApi.pubBlogComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_TRANSLATION:
                OSChinaApi.pubTranslateComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_EVENT:
                if (comment != null) {
                    content = "回复@" + comment.getAuthor().getName() + " : " + content;
                }
                OSChinaApi.pubEventComment(id, 0, 0, content, mHandler);
                break;
            case OSChinaApi.COMMENT_NEWS:
                OSChinaApi.pubNewsComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_SOFT:
                OSChinaApi.pubSoftComment(id, 0, 0, content, mHandler);
                break;
            default:
                break;
        }

    }

    private void getData(final boolean clearData, String token) {
        OSChinaApi.getComments(mId, mType, "refer,reply", mOrder, token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (isDestroy())
                    return;
                mCommentAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestroy())
                    return;
                mRefreshLayout.onComplete();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (isDestroy())
                    return;
                try {

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());

                    if (resultBean.isSuccess()) {
                        mPageBean = resultBean.getResult();
                        int titleHintId = R.string.comment_title_hint;
                        if (mType == OSChinaApi.COMMENT_EVENT || mType == OSChinaApi.COMMENT_QUESTION) {
                            titleHintId = R.string.answer_hint;
                        }
                        mToolBar.setTitle(String.format("%d%s%s", mPageBean.getTotalResults(), getString(R.string.item_hint), getString(titleHintId)));
                        handleData(mPageBean.getItems(), clearData);
                    }

                    mCommentAdapter.setState(
                            mPageBean == null || mPageBean.getItems() == null || mPageBean.getItems().size() < 20 ?
                                    BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(List<Comment> comments, boolean clearData) {
        if (clearData)
            mCommentAdapter.clear();
        mCommentAdapter.addAll(comments);
    }

    @Override
    public void onLongClick(int position, long itemId) {

        final Comment comment = mCommentAdapter.getItem(position);
        if (comment == null) return;

        String[] items;
        // if (AccountHelper.getUserId() == (int) comment.getAuthor().getId()) {
        //   items = new String[]{getString(R.string.copy), getString(R.string.delete)};
        //} else {
        items = new String[]{getString(R.string.copy)};
        // }

        DialogHelper.getSelectDialog(this, items, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(comment.getContent()));
                        break;
                    case 1:
                        // TODO: 2016/11/30 delete comment
                        break;
                    default:
                        break;
                }
            }
        }).show();

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
    protected void onResume() {
        super.onResume();
        if (mShareView != null)
            mShareView.dismiss();
    }

    @Override
    public void finish() {
        if (isAddCommented) {
            setResult(RESULT_OK, new Intent());
        }
        super.finish();
    }
}
