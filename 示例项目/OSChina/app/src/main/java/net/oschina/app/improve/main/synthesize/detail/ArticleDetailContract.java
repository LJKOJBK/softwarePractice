package net.oschina.app.improve.main.synthesize.detail;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.detail.db.Behavior;

import java.util.List;

/**
 * 头条详情
 * Created by huanghaibin on 2017/10/23.
 */

interface ArticleDetailContract {

    interface EmptyView {

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showFavReverseSuccess(boolean isFav);

        void showFavError();

        void showErrorLayout(int errorType);

        void showGetDetailSuccess(Article article);
    }

    interface View extends BaseListView<Presenter, Article> {
        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showGetDetailSuccess(Article article);

        void showScrollToTop();
    }

    interface Presenter extends BaseListPresenter {

        void getArticleDetail();

        void putArticleComment(String content, long referId, long reAuthorId);

        void uploadBehaviors(List<Behavior> behaviors);

        void addClickCount();

        void fav();

        void scrollToTop();

        String formatTextCount(int count);

        String formatTime(long time);

        String formatTimeUnit(long time);
    }
}
