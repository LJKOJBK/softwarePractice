package net.oschina.app.improve.main.synthesize.web;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.comment.Comment;

/**
 * 头条浏览器
 * Created by huanghaibin on 2017/10/30.
 */

interface ArticleWebContract {

    interface View extends BaseView<Presenter> {
        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showFavReverseSuccess(boolean isFav);

        void showFavError();
    }

    interface Presenter extends BasePresenter {
        void putArticleComment(String content, long referId, long reAuthorId);

        void fav();
    }
}
