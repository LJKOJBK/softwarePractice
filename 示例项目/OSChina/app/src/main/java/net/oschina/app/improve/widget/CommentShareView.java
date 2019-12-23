package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.comment.CommentReferView;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 评论分享的View
 * Created by haibin on 2017/4/17.
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class CommentShareView extends NestedScrollView implements Runnable {
    private CommentShareAdapter mAdapter;
    private ShareDialog mShareDialog;
    private ProgressDialog mDialog;
    private Bitmap mBitmap;
    private boolean isShare;

    public CommentShareView(Context context) {
        this(context, null);
    }

    public CommentShareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.lay_comment_share_view, this, true);
        RecyclerView mRecyclerComment = (RecyclerView) findViewById(R.id.rv_comment);
        mRecyclerComment.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new CommentShareAdapter(context);
        mRecyclerComment.setAdapter(mAdapter);
        mShareDialog = new ShareDialog((Activity) context, -1, false);
        mDialog = DialogHelper.getProgressDialog(context);
        mDialog.setMessage("请稍候...");
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isShare)
                    return;
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                removeCallbacks(CommentShareView.this);
            }
        });
    }

    public void init(String title, Comment comment) {
        if (comment == null)
            return;
        setText(R.id.tv_title, title);
        mAdapter.clear();
        mAdapter.addItem(comment);
    }

    public void dismiss() {
        isShare = false;
        if (mDialog != null)
            mDialog.dismiss();
        if (mShareDialog != null)
            mShareDialog.dismiss();
    }

    @Override
    public void run() {
        isShare = true;
        if (mDialog == null)
            return;
        mDialog.dismiss();
        mBitmap = getBitmap();
        mShareDialog.bitmap(mBitmap);
        mShareDialog.show();
    }

    public void share() {
        mDialog.show();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        postDelayed(this, 2000);
    }

    private void setText(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    private Bitmap getBitmap() {
        return create(getChildAt(0));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        clearShareImage();
    }

    public static void clearShareImage() {
        try {
            String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/";
            File file = new File(url);
            if (!file.exists())
                return;
            File[] files = file.listFiles();
            if (files == null || files.length == 0)
                return;
            for (File f : file.listFiles()) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap create(View v) {
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

    static class CommentShareAdapter extends BaseRecyclerAdapter<Comment> {
        private RequestManager mLoader;

        CommentShareAdapter(Context context) {
            super(context, NEITHER);
            mLoader = Glide.with(context);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_list_comment_share, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            ((CommentHolder) holder).addComment(item);
        }

        static class CommentHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.iv_avatar)
            PortraitView mIvAvatar;

            @Bind(R.id.tv_name)
            TextView mName;
            @Bind(R.id.tv_pub_date)
            TextView mPubDate;

            @Bind(R.id.lay_refer)
            CommentReferView mCommentReferView;

            @Bind(R.id.tv_content)
            TweetTextView mTweetTextView;

            CommentHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @SuppressLint("DefaultLocale")
            void addComment(final Comment comment) {
                Author author = comment.getAuthor();
                mIvAvatar.setup(author);

                String name;
                if (author == null || TextUtils.isEmpty(name = author.getName()))
                    name = mName.getResources().getString(R.string.martian_hint);
                mName.setText(name);
                mPubDate.setText(StringUtils.formatDayTime(comment.getPubDate()));

                mCommentReferView.addComment(comment);
                if (comment.getRefer() == null || comment.getRefer().length == 0) {
                    CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent(), true, false);
                } else {
                    CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent(), false, false);
                }

            }
        }
    }
}
