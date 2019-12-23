package net.oschina.app.improve.tweet.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.improve.widget.TweetPicturesLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 分享动弹界面
 * Created by huanghaibin on 2017/10/16.
 */

public class TweetShareFragment extends BaseFragment implements Runnable {

    @Bind(R.id.iv_portrait)
    PortraitView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tweet_pics_layout)
    TweetPicturesLayout mLayoutGrid;

    @Bind(R.id.tv_content)
    TextView mTextContent;
    @Bind(R.id.tv_ref_title)
    TextView mViewRefTitle;
    @Bind(R.id.tv_ref_content)
    TextView mViewRefContent;
    @Bind(R.id.layout_ref_images)
    TweetPicturesLayout mLayoutRefImages;
    @Bind(R.id.layout_ref)
    LinearLayout mLayoutRef;

    @Bind(R.id.tv_tweet_like_count)
    TextView mTextLikeCount;

    @Bind(R.id.tv_tweet_comment_count)
    TextView mTextCommentCount;

    @Bind(R.id.tv_comment)
    TextView mTextComment;

    @Bind(R.id.nsv_content)
    NestedScrollView mViewScroller;
    private ShareDialog mShareDialog;
    private Bitmap mBitmap;
    private ProgressDialog mDialog;

    @Bind(R.id.recyclerView)
    RecyclerView mRecycleView;
    private ShareCommentAdapter mAdapter;

    private ArrayList<TweetComment> mComments;
    private Tweet mTweet;

    public static TweetShareFragment newInstance(Tweet tweet, List<TweetComment> comments) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("tweet", tweet);
        if (comments != null && comments instanceof ArrayList) {
            bundle.putSerializable("comments", (ArrayList) comments);
        }
        TweetShareFragment fragment = new TweetShareFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("all")
    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTweet = (Tweet) bundle.getSerializable("tweet");
        mComments = (ArrayList<TweetComment>) bundle.getSerializable("comments");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mDialog = DialogHelper.getProgressDialog(mContext);
        mDialog.setMessage("请稍候...");
        mShareDialog = new ShareDialog(getActivity(), -1, false);
        mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ShareCommentAdapter(mContext, BaseRecyclerAdapter.NEITHER, mTweet);

        mRecycleView.setAdapter(mAdapter);
        init(mTweet);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_share;
    }


    void initList(List<TweetComment> list) {
        if (mComments != null && mComments.size() > 0) {
            mAdapter.resetItem(mComments);
            return;
        }
        mAdapter.resetItem(list);
        if (mContext == null)
            return;
        if (list == null || list.size() == 0) {
            mTextComment.setVisibility(View.GONE);
        } else {
            mTextComment.setVisibility(View.VISIBLE);
        }
    }

    private void init(Tweet tweet) {
        if (mContext == null)
            return;
        Author author = tweet.getAuthor();
        if (author != null) {
            ivPortrait.setup(author);
            tvNick.setText(author.getName());
        } else {
            ivPortrait.setup(0, "匿名用户", "");
            tvNick.setText("匿名用户");
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.getDateString(tweet.getPubDate()));
        if (!TextUtils.isEmpty(tweet.getContent())) {
            String content = tweet.getContent().replaceAll("[\n\\s]+", " ");
            CommentsUtil.formatHtml(mTextContent.getResources(), mTextContent, content, true, false);
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
                    String con = "@" + about.getTitle() + "： " + about.getContent();
                    CommentsUtil.formatHtml(mViewRefContent.getResources(), mViewRefContent, con, true, true);
                } else {
                    mViewRefTitle.setVisibility(View.VISIBLE);
                    mViewRefTitle.setText(about.getTitle());
                    mViewRefContent.setText(about.getContent());
                }
            }
        } else {
            mLayoutRef.setVisibility(View.GONE);
        }
        mTextCommentCount.setText(String.valueOf(tweet.getCommentCount()));
        mTextLikeCount.setText(String.valueOf(tweet.getLikeCount()));
    }

    @Override
    public void run() {
        mBitmap = create(mViewScroller.getChildAt(0));
        mShareDialog.bitmap(mBitmap);
        mDialog.dismiss();
        mShareDialog.show();
    }

    public void share() {
        recycle();
        mDialog.show();
        mRoot.postDelayed(this, 2000);
        mBitmap = create(mViewScroller.getChildAt(0));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        recycle();
        mBitmap = create(mViewScroller.getChildAt(0));
        FileOutputStream os = null;
        try {
            String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/save/";
            File file = new File(url);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = String.format("%s%s.jpg", url, System.currentTimeMillis());
            os = new FileOutputStream(path);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            SimplexToast.show(mContext, "保存成功");
            Uri localUri = Uri.fromFile(new File(path));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            getActivity().sendBroadcast(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            SimplexToast.show(mContext, "保存失败");
        } finally {
            StreamUtil.close(os);
            recycle();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mShareDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycle();
    }

    private static Bitmap create(View v) {
        try {
            int w = v.getWidth();
            int h = v.getHeight();
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.WHITE);
            v.layout(0, 0, w, h);
            v.draw(c);
            return bmp;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            return null;
        }
    }

    private void recycle() {
        if (mBitmap != null && mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }
}
