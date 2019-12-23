package net.oschina.app.improve.main.synthesize.comment;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.comment.Comment;

/**
 * 头条评论列表
 * Created by huanghaibin on 2017/10/28.
 */

interface ArticleCommentContract {

    interface Action {
        void showAddCommentSuccess(Comment comment, int strId);

        void showAddCommentFailure(int strId);
    }

    interface View extends BaseListView<Presenter, Comment> {
        void showAddCommentSuccess(Comment comment, int strId);

        void showAddCommentFailure(int strId);
    }

    interface Presenter extends BaseListPresenter {
        void putArticleComment(String content, long referId, long reAuthorId);
    }
}
