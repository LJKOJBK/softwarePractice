package net.oschina.app.improve.tweet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.improve.tweet.fragments.TweetDetailViewPagerFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.tweet.share.TweetShareActivity;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.improve.utils.parser.StringParser;
import net.oschina.app.improve.utils.parser.TweetParser;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.improve.widget.TweetPicturesLayout;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapterV2;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.RecordButtonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情
 * Created by thanatos
 * on 16/6/13.
 */
@SuppressWarnings("deprecation")
public class TweetDetailActivity extends BackActivity implements TweetDetailContract.Operator {

    public static final String BUNDLE_KEY_TWEET = "BUNDLE_KEY_TWEET";

    @Bind(R.id.identityView)
    IdentityView mIdentityView;
    @Bind(R.id.iv_portrait)
    PortraitView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_client)
    TextView tvClient;
//    @Bind(R.id.iv_thumbup)
//    ImageView ivThumbup;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.fragment_container)
    FrameLayout mFrameLayout;
    @Bind(R.id.tweet_img_record)
    ImageView mImgRecord;
    @Bind(R.id.tweet_tv_record)
    TextView mSecondRecord;
    @Bind(R.id.tweet_bg_record)
    RelativeLayout mRecordLayout;
    @Bind(R.id.tv_content)
    TextView mContent;
    @Bind(R.id.tweet_pics_layout)
    TweetPicturesLayout mLayoutGrid;
    @Bind(R.id.tv_ref_title)
    TextView mViewRefTitle;
    @Bind(R.id.tv_ref_content)
    TextView mViewRefContent;
    @Bind(R.id.layout_ref_images)
    TweetPicturesLayout mLayoutRefImages;
//    @Bind(R.id.iv_dispatch)
//    ImageView mViewDispatch;
    @Bind(R.id.layout_ref)
    LinearLayout mLayoutRef;

    @Bind(R.id.fl_footer)
    FrameLayout mFrameFooter;
    private Tweet tweet;
    private final List<TweetComment> replies = new ArrayList<>();
    private RecordButtonUtil mRecordUtil;
    private TextHttpResponseHandler publishAdmireHandler;
    private TextHttpResponseHandler publishCommentHandler;

    private TweetDetailContract.ICmnView mCmnViewImp;
    private TweetDetailContract.IThumbupView mThumbupViewImp;
    private TweetDetailContract.IAgencyView mAgencyViewImp;

    private CommentBar mDelegation;
    private boolean mInputDoubleEmpty = false;

    private View.OnClickListener onPortraitClickListener;
    private ShareDialog alertDialog;

    public static void show(Context context, Tweet tweet) {
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, tweet);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Tweet tweet = new Tweet();
        tweet.setId(id);
        show(context, tweet);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        tweet = (Tweet) getIntent().getSerializableExtra(BUNDLE_KEY_TWEET);
        if (tweet == null) {
            AppContext.showToastShort("对象没找到");
            return false;
        }
        return super.initBundle(bundle);
    }

    protected void initData() {
        // admire tweet
        publishAdmireHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                AppContext.showToastShort(mDelegation.getLikeImage().isSelected() ? "取消失败" : "点赞失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<TweetLike> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<TweetLike>>() {
                        }.getType());
                if (result != null && result.isSuccess()) {
                    mDelegation.getLikeImage().setSelected(result.getResult().isLiked());
                    mThumbupViewImp.onLikeSuccess(result.getResult().isLiked(), null);
                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        };

        // publish tweet comment
        publishCommentHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                AppContext.showToastShort("评论失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mCmnViewImp.onCommentSuccess(null);
                replies.clear(); // 清除

                if (mDelegation.getBottomSheet().isSyncToTweet()) {
                    Tweet tempTweet = tweet;
                    if (tempTweet == null) return;
                    TweetPublishService.startActionPublish(TweetDetailActivity.this
                            , mDelegation.getBottomSheet().getCommentText(), null,
                            About.buildShare(tempTweet.getId(), OSChinaApi.COMMENT_TWEET));
                }

                AppContext.showToastShort("评论成功");
                mDelegation.setCommentHint("添加评论");
                mDelegation.getBottomSheet().getEditText().setText("");
                mDelegation.getBottomSheet().getEditText().setHint("添加评论");
                mDelegation.getBottomSheet().dismiss();
            }

            @Override
            public void onStart() {
                super.onStart();
                Tweet tempTweet = tweet;
                if (tempTweet != null && tempTweet.getAuthor() != null)
                    ContactsCacheManager.addRecentCache(tempTweet.getAuthor());
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(false);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(true);
            }
        };

        OSChinaApi.getTweetDetail(tweet.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Toast.makeText(TweetDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Tweet> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Tweet>>() {
                        }.getType());
                if (result.isSuccess()) {
                    if (result.getResult() == null) {
                        AppContext.showToast(R.string.tweet_detail_data_null);
                        finish();
                        return;
                    }
                    tweet = result.getResult();
                    mAgencyViewImp.resetCmnCount(tweet.getCommentCount());
                    mAgencyViewImp.resetLikeCount(tweet.getLikeCount());
                    setupDetailView();
                } else {
                    onFailure(500, headers, "妈的智障", null);
                }
            }
        });

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setSwipeBackEnable(true);
        setStatusBarDarkMode();
        setDarkToolBar();
        mToolBar.setTitle("动弹详情");
        setSupportActionBar(mToolBar);

        mDelegation = CommentBar.delegation(this, mFrameFooter);

        mDelegation.hideFav();
        mDelegation.hideCommentCount();
        mDelegation.showLike();
        mDelegation.showDispatch();
        mDelegation.setLikeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickThumbUp();
            }
        });
        mDelegation.setDispatchListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTransmit();
            }
        });
        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    handleKeyDel();
                }
                return false;
            }
        });

        mDelegation.hideCommentCount();
        mDelegation.hideFav();

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    UserSelectFriendsActivity.show(TweetDetailActivity.this, mDelegation.getBottomSheet().getEditText());
                } else
                    LoginActivity.show(TweetDetailActivity.this);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapterV2(this));
        mDelegation.getBottomSheet().showEmoji();
        mDelegation.getBottomSheet().hideSyncAction();
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getBottomSheet().getCommentText().replaceAll("[\\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(TweetDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(TweetDetailActivity.this);
                    return;
                }
                if (replies.size() > 0)
                    content = mDelegation.getBottomSheet().getEditText().getHint() + ": " + content;
                OSChinaApi.pubTweetComment(tweet.getId(), content, 0, publishCommentHandler);
            }
        });
        resolveVoice();
        setupDetailView();

        TweetDetailViewPagerFragment mPagerFrag = TweetDetailViewPagerFragment.instantiate();
        mCmnViewImp = mPagerFrag.getCommentViewHandler();
        mThumbupViewImp = mPagerFrag.getThumbupViewHandler();
        mAgencyViewImp = mPagerFrag.getAgencyViewHandler();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mPagerFrag)
                .commit();
    }

    private void resolveVoice() {
        if (tweet == null || tweet.getAudio() == null || tweet.getAudio().length == 0) return;
        mRecordLayout.setVisibility(View.VISIBLE);
        final AnimationDrawable drawable = (AnimationDrawable) mImgRecord.getBackground();
        mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet == null) return;
                getRecordUtil().startPlay(tweet.getAudio()[0].getHref(), mSecondRecord);
            }
        });
        getRecordUtil().setOnPlayListener(new RecordButtonUtil.OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgRecord.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                drawable.start();
                mImgRecord.setBackgroundDrawable(drawable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (tweet == null || tweet.getId() <= 0 || TextUtils.isEmpty(tweet.getContent()))
                    break;

                String content = StringParser.getInstance().parse(this, tweet.getContent()).toString();
                if (content.length() > 30)
                    content = content.substring(0, 30);

                if (alertDialog == null){
                    alertDialog = new ShareDialog(this, true)
                            .title(content + " - 开源中国社区 ")
                            .content(content)
                            .url(tweet.getHref()).with();
                    alertDialog.setItemClickListener(new ShareDialog.ShareItemClickListener() {
                        @Override
                        public void onShareTweet() {
                            TweetShareActivity.show(TweetDetailActivity.this,tweet,
                                    mCmnViewImp.getComments());
                        }
                    });
                }

                alertDialog.setTweet(tweet);
                alertDialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private RecordButtonUtil getRecordUtil() {
        if (mRecordUtil == null) {
            mRecordUtil = new RecordButtonUtil();
        }
        return mRecordUtil;
    }

    /**
     * 填充数据
     */
    private void setupDetailView() {
        // 有可能传入的tweet只有id这一个值
        if (tweet == null || isDestroy())
            return;
        Author author = tweet.getAuthor();
        mIdentityView.setup(author);
        if (author != null) {
            ivPortrait.setup(author);
            ivPortrait.setOnClickListener(getOnPortraitClickListener());
            tvNick.setText(author.getName());
        } else {
            ivPortrait.setup(0, "匿名用户", "");
            tvNick.setText("匿名用户");
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(tweet.getPubDate()));
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppClient());
        if (tweet.isLiked()) {
            mDelegation.getLikeImage().setSelected(true);
        } else {
            mDelegation.getLikeImage().setSelected(false);
        }
        if (!TextUtils.isEmpty(tweet.getContent())) {
            String content = tweet.getContent().replaceAll("[\n\\s]+", " ");
            mContent.setText(TweetParser.getInstance().parse(this, content));
            mContent.setMovementMethod(LinkMovementMethod.getInstance());
        }

        mLayoutGrid.setImage(tweet.getImages());

        /* -- about reference -- */
        if (tweet.getAbout() != null) {
            mLayoutRef.setVisibility(View.VISIBLE);
            About about = tweet.getAbout();
            mLayoutRefImages.setImage(about.getImages());

            if (!About.check(about)) {
                mViewRefTitle.setVisibility(View.VISIBLE);
                mViewRefTitle.setText("不存在或已删除的内容");
                mViewRefContent.setText("抱歉，该内容不存在或已被删除");
            } else {
                if (about.getType() == OSChinaApi.COMMENT_TWEET) {
                    mViewRefTitle.setVisibility(View.GONE);
                    String aName = "@" + about.getTitle();
                    String cnt = about.getContent();
                    Spannable spannable = TweetParser.getInstance().parse(this, cnt);
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(aName).append(": ");
                    builder.append(spannable);
                    ForegroundColorSpan span = new ForegroundColorSpan(
                            getResources().getColor(R.color.day_colorPrimary));
                    builder.setSpan(span, 0, aName.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    mViewRefContent.setText(builder);
                } else {
                    mViewRefTitle.setVisibility(View.VISIBLE);
                    mViewRefTitle.setText(about.getTitle());
                    mViewRefContent.setText(about.getContent());
                }
            }
        } else {
            mLayoutRef.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener getOnPortraitClickListener() {
        if (onPortraitClickListener == null) {
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showUserCenter(TweetDetailActivity.this, tweet.getAuthor().getId(),
                            tweet.getAuthor().getName());
                }
            };
        }
        return onPortraitClickListener;
    }

    @Override
    public Tweet getTweetDetail() {
        return tweet;
    }

    @Override
    public void toReply(TweetComment comment) {
        if (comment.getAuthor() == null)
            return;
        if (checkLogin()) return;
        if (replies.size() < 5) {
            for (TweetComment cmm : replies) {
                if (cmm.getAuthor().getId() == comment.getAuthor().getId()) {
                    this.mDelegation.performClick();
                    return;
                }
            }
            if (replies.size() == 0) {
                mDelegation.getBottomSheet().getEditText().setHint("回复: @" + comment.getAuthor().getName());
                mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
            } else {
                mDelegation.getBottomSheet().getEditText().setHint(mDelegation.getBottomSheet().getEditText().getHint() + " @" + comment.getAuthor().getName());
                mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
            }
            this.replies.add(comment);
        }
        this.mDelegation.performClick();
    }

    @Override
    public void onScroll() {
        if (mDelegation != null) mDelegation.getBottomSheet().dismiss();
    }


    private void onClickThumbUp() {
        if (checkLogin()) return;
        OSChinaApi.reverseTweetLike(tweet.getId(), publishAdmireHandler);
    }

    @OnClick(R.id.layout_ref)
    void onClickRef() {
        if (tweet.getAbout() == null) return;
        UIHelper.showDetail(this, tweet.getAbout().getType(), tweet.getAbout().getId(), null);
    }


    private void onClickComment() {
        if (checkLogin()) return;
        replies.clear();
        mDelegation.getBottomSheet().show("发表评论");
    }


    private void onClickTransmit() {
        if (tweet == null || tweet.getId() <= 0 && tweet.getAuthor() == null) return;

        String content = null;
        About.Share share;
        if (tweet.getAbout() == null) {
            share = About.buildShare(tweet.getId(), OSChinaApi.CATALOG_TWEET);
            share.title = tweet.getAuthor().getName();
            share.content = tweet.getContent();
        } else {
            share = About.buildShare(tweet.getAbout());
            content = "//@" + tweet.getAuthor().getName() + " :" + tweet.getContent();
            content = TweetParser.getInstance().clearHtmlTag(content).toString();
        }
        share.commitTweetId = tweet.getId();
        share.fromTweetId = tweet.getId();
        TweetPublishActivity.show(this, null, content, share);
    }

    @OnLongClick({R.id.layout_container, R.id.tv_content})
    boolean onLongClickContent() {
        QuickOptionDialogHelper.with(this)
                .addCopy(HTMLUtil.delHTMLTag(tweet.getContent()))
                .show();
        return true;
    }

    private boolean checkLogin() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //                if (!mDelegation.onTurnBack()) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleKeyDel() {
        if (replies.size() == 0) return;
        if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
            if (mInputDoubleEmpty) {
                replies.remove(replies.size() - 1);
                if (replies.size() == 0) {
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                    mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
                    return;
                }
                TweetComment comment = replies.get(0);
                if (comment == null || comment.getAuthor() == null) {
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                    mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
                    return;
                }

                mDelegation.getBottomSheet().getEditText().setHint("回复: @" + comment.getAuthor().getName());
                for (int i = 1; i < replies.size(); i++) {
                    TweetComment tc = replies.get(i);
                    if (tc != null && tc.getAuthor() != null)
                        mDelegation.getBottomSheet().getEditText().setHint(mDelegation.getBottomSheet().getEditText().getHint() + " @" + tc.getAuthor()
                                .getName());
                }
            } else {
                mInputDoubleEmpty = true;
            }
        } else {
            mInputDoubleEmpty = false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.cancelLoading();
        }
    }

}
