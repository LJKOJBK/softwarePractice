package net.oschina.app.improve.main.tweet.comment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.util.HTMLUtil;

import java.util.List;

/**
 * 动弹评论列表
 * Created by huanghaibin on 2017/12/18.
 */

public class TweetCommentFragment extends BaseRecyclerFragment<TweetCommentContract.Presenter, TweetComment>
        implements TweetCommentContract.View, TweetDetailContract.ICmnView, BaseRecyclerAdapter.OnItemLongClickListener {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;

    private ProgressDialog dialog = null;
    public static TweetCommentFragment newInstance(Tweet tweet) {
        TweetCommentFragment fragment = new TweetCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tweet", tweet);
        fragment.setArguments(bundle);
        return fragment;
    }


    public static TweetCommentFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        TweetCommentFragment fragment = new TweetCommentFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOperator = (TweetDetailContract.Operator) context;
    }


    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
    }

    @Override
    protected void initData() {
        new TweetCommentPresenter(this, mOperator.getTweetDetail());
        super.initData();
        dialog = DialogHelper.getProgressDialog(getContext(), "正在删除……", false);
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void onItemClick(TweetComment tweetComment, int position) {

        mOperator.toReply(tweetComment);
    }

    @Override
    public void onLongClick(final int position, long itemId) {
        final TweetComment comment = mAdapter.getItem(position);
        if (comment == null) return;

        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(comment.getContent()))
                .addOther(comment.getAuthor().getId() == AccountHelper.getUserId(),
                        R.string.delete, new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.deleteTweetComment(mOperator.getTweetDetail().getId(), comment, position);
                            }
                        }).show();
    }

    @Override
    public void showDeleteSuccess(int position) {
        if (mContext == null)
            return;
        dialog.dismiss();
        mAdapter.removeItem(position);
        int count = mOperator.getTweetDetail().getCommentCount() - 1;
        mOperator.getTweetDetail().setCommentCount(count);
        mAgencyView.resetCmnCount(count);
        AppContext.showToastShort("删除成功");
    }

    @Override
    public void showDeleteFailure() {
        if (mContext == null)
            return;
        dialog.dismiss();
        AppContext.showToastShort("删除失败");
    }

    @Override
    public void onCommentSuccess(TweetComment comment) {
        if (mContext == null || mPresenter == null) {
            return;
        }
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnLoading(true);
        onRefreshing();
    }


    @Override
    public void onRequestSuccess() {
        if (mAdapter.getCount() < 20 && mAgencyView != null)
            mAgencyView.resetCmnCount(mAdapter.getCount());
    }

    @Override
    public List<TweetComment> getComments() {
        return mAdapter.getItems();
    }

    @Override
    protected BaseRecyclerAdapter<TweetComment> getAdapter() {
        return new TweetCommentAdapter(mContext);
    }
}
