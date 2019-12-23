package net.oschina.app.improve.tweet.share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.utils.parser.TweetParser;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.improve.widget.TweetPicturesLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 评论分享
 * Created by huanghaibin on 2017/10/18.
 */

public class ShareCommentAdapter extends BaseRecyclerAdapter<TweetComment> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {

    private Tweet mTweet;

    ShareCommentAdapter(Context context, int mode, Tweet tweet) {
        super(context, mode);
        this.mTweet = tweet;
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new CommentHeaderView(LayoutInflater.from(mContext).inflate(R.layout.layout_tweet_share_header_view, parent, false));
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = mTweet;
        Author author = tweet.getAuthor();
        CommentHeaderView h = (CommentHeaderView) holder;
        if (author != null) {
            h.ivPortrait.setup(author);
            h.tvNick.setText(author.getName());
        } else {
            h.ivPortrait.setup(0, "匿名用户", "");
            h.tvNick.setText("匿名用户");
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            h.tvTime.setText(StringUtils.getDateString(tweet.getPubDate()));
        if (!TextUtils.isEmpty(tweet.getContent())) {
            String content = tweet.getContent().replaceAll("[\n\\s]+", " ");
            CommentsUtil.formatHtml(h.mContent.getResources(), h.mContent, content, true,false);
        }

        h.mLayoutGrid.setImage(tweet.getImages());

        /* -- about reference -- */
        if (tweet.getAbout() != null) {
            h.mLayoutRef.setVisibility(View.VISIBLE);
            About about = tweet.getAbout();
            h.mLayoutRefImages.setImage(about.getImages());

            if (!About.check(about)) {
                h.mViewRefTitle.setVisibility(View.VISIBLE);
                h.mViewRefTitle.setText("不存在或已删除的内容");
                h.mViewRefContent.setText("抱歉，该内容不存在或已被删除");
            } else {
                if (about.getType() == OSChinaApi.COMMENT_TWEET) {
                    h.mViewRefTitle.setVisibility(View.GONE);
                    String con = "@" + about.getTitle() + "： " +about.getContent();
                    CommentsUtil.formatHtml(h.mViewRefContent.getResources(), h.mViewRefContent, con, true,true);
                } else {
                    h.mViewRefTitle.setVisibility(View.VISIBLE);
                    h.mViewRefTitle.setText(about.getTitle());
                    h.mViewRefContent.setText(about.getContent());
                }
            }
        } else {
            h.mLayoutRef.setVisibility(View.GONE);
        }
        h.mTextComment.setVisibility(mItems.size() == 0 ? View.GONE : View.VISIBLE);
        h.mTextCommentCount.setText(String.valueOf(tweet.getCommentCount()));
        h.mTextLikeCount.setText(String.valueOf(tweet.getLikeCount()));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CommentHolderView(LayoutInflater.from(mContext).inflate(R.layout.item_list_share_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, TweetComment item, int position) {
        CommentHolderView h = (CommentHolderView) holder;

        h.identityView.setup(item.getAuthor());
        h.ivPortrait.setup(item.getAuthor());
        h.ivPortrait.setTag(R.id.iv_tag, item);
        h.tvName.setText(item.getAuthor().getName());
        h.tvContent.setText(InputHelper.displayEmoji(mContext.getResources(), item.getContent()));
        h.tvTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }

    static final class CommentHeaderView extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_portrait)
        PortraitView ivPortrait;
        @Bind(R.id.tv_nick)
        TextView tvNick;
        @Bind(R.id.tv_time)
        TextView tvTime;
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
        @Bind(R.id.layout_ref)
        LinearLayout mLayoutRef;
        @Bind(R.id.tv_tweet_like_count)
        TextView mTextLikeCount;
        @Bind(R.id.tv_tweet_comment_count)
        TextView mTextCommentCount;
        @Bind(R.id.tv_comment)
        TextView mTextComment;

        CommentHeaderView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static final class CommentHolderView extends RecyclerView.ViewHolder {
        @Bind(R.id.identityView)
        IdentityView identityView;
        @Bind(R.id.iv_avatar)
        PortraitView ivPortrait;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_pub_date)
        public TextView tvTime;
        @Bind(R.id.tv_content)
        TweetTextView tvContent;

        CommentHolderView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}