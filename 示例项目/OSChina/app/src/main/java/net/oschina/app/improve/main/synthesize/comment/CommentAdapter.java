package net.oschina.app.improve.main.synthesize.comment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Vote;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.comment.CommentReferView;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 热门评论
 * Created by huanghaibin on 2017/10/28.
 */

public class CommentAdapter extends BaseRecyclerAdapter<Comment> {
    public CommentAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new Holder(mInflater.inflate(R.layout.item_list_article_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        if (holder instanceof Holder) {
            ((Holder) holder).addComment(item);
        }
    }

    static final class Holder extends RecyclerView.ViewHolder {

        private ProgressDialog mDialog;

        @Bind(R.id.iv_avatar)
        PortraitView mIvAvatar;

        @Bind(R.id.identityView)
        IdentityView mIdentityView;

        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_pub_date)
        TextView mPubDate;
        @Bind(R.id.tv_vote_count)
        TextView mVoteCount;
        @Bind(R.id.btn_vote)
        ImageView mVote;

        @Bind(R.id.lay_refer)
        CommentReferView mCommentReferView;

        @Bind(R.id.tv_content)
        TweetTextView mTweetTextView;
        @Bind(R.id.line)
        View mLine;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * add comment
         *
         * @param comment comment
         */
        @SuppressLint("DefaultLocale")
        void addComment(final Comment comment) {
            mIdentityView.setup(comment.getAuthor());
            mIvAvatar.setup(comment.getAuthor());
            mIvAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(mIvAvatar.getContext(), comment.getAuthor().getId());
                }
            });
            Author author = comment.getAuthor();
            String name;
            if (author == null || TextUtils.isEmpty(name = author.getName()))
                name = mName.getResources().getString(R.string.martian_hint);
            mName.setText(name);
            mPubDate.setText(String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));


            mVoteCount.setText(String.valueOf(comment.getVote()));
            mVoteCount.setVisibility(View.VISIBLE);
            mVote.setVisibility(View.VISIBLE);
            if (comment.getVoteState() == 1) {
                mVote.setImageResource(R.mipmap.ic_thumbup_actived);
                mVote.setTag(true);
            } else if (comment.getVoteState() == 0) {
                mVote.setImageResource(R.mipmap.ic_thumb_normal);
                mVote.setTag(null);
            }
            mVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handVote();
                }

                private void handVote() {
                    if (!AccountHelper.isLogin()) {
                        LoginActivity.show(mVote.getContext());
                        return;
                    }
                    if (!TDevice.hasInternet()) {
                        AppContext.showToast(mVote.getResources().getString(R.string.state_network_error), Toast.LENGTH_SHORT);
                        return;
                    }
                    OSChinaApi.voteArticleComment(comment.getId(), comment.getAuthor().getId(), new TextHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            showWaitDialog(R.string.progress_submit);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            requestFailureHint(throwable);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            ResultBean<Vote> resultBean = AppOperator.createGson().fromJson(responseString, getVoteType());
                            if (resultBean.isSuccess()) {
                                Vote vote = resultBean.getResult();
                                if (vote != null) {
                                    if (vote.getVoteState() == 1) {
                                        comment.setVoteState(1);
                                        mVote.setTag(true);
                                        mVote.setImageResource(R.mipmap.ic_thumbup_actived);
                                    } else if (vote.getVoteState() == 0) {
                                        comment.setVoteState(0);
                                        mVote.setTag(null);
                                        mVote.setImageResource(R.mipmap.ic_thumb_normal);
                                    }
                                    long voteVoteCount = vote.getVote();
                                    comment.setVote(voteVoteCount);
                                    mVoteCount.setText(String.valueOf(voteVoteCount));
                                }
                            } else {
                                AppContext.showToast(resultBean.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            hideWaitDialog();
                        }
                    });
                }
            });

            mCommentReferView.addComment(comment);

            CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent());
        }

        /**
         * show WaitDialog
         *
         * @return progressDialog
         */
        private ProgressDialog showWaitDialog(@StringRes int messageId) {

            if (mDialog == null) {
                if (messageId <= 0) {
                    mDialog = DialogHelper.getProgressDialog(mVote.getContext(), true);
                } else {
                    String message = mVote.getContext().getResources().getString(messageId);
                    mDialog = DialogHelper.getProgressDialog(mVote.getContext(), message, true);
                }
            }
            mDialog.show();

            return mDialog;
        }

        /**
         * hide waitDialog
         */
        private void hideWaitDialog() {
            ProgressDialog dialog = mDialog;
            if (dialog != null) {
                mDialog = null;
                try {
                    dialog.cancel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * request network error
         *
         * @param throwable throwable
         */
        private void requestFailureHint(Throwable throwable) {
            AppContext.showToast(R.string.request_error_hint);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }

        /**
         * @return TypeToken
         */
        Type getVoteType() {
            return new TypeToken<ResultBean<Vote>>() {
            }.getType();
        }
    }
}
