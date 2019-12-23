package net.oschina.app.improve.main.synthesize.comment;

import android.view.View;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * 头条评论
 * Created by huanghaibin on 2017/10/28.
 */

public class ArticleCommentFragment extends BaseRecyclerFragment<ArticleCommentContract.Presenter, Comment>
        implements ArticleCommentContract.View {

    private OnCommentClickListener mListener;

    public static ArticleCommentFragment newInstance() {
        return new ArticleCommentFragment();
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mListener = (OnCommentClickListener) mContext;
    }

    @Override
    protected void onItemClick(Comment comment, int position) {
        if (mListener != null) {
            mListener.onClick(comment);
        }
    }


    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        if (mContext == null)
            return;
        mAdapter.addItem(comment);
    }

    @Override
    public void showAddCommentFailure(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected BaseRecyclerAdapter<Comment> getAdapter() {
        return new CommentAdapter(mContext);
    }

    public interface OnCommentClickListener {
        void onClick(Comment comment);
    }
}
