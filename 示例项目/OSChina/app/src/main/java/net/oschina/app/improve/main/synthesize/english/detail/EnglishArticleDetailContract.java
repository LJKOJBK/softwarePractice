package net.oschina.app.improve.main.synthesize.english.detail;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.comment.Comment;

/**
 * 英文详情界面
 * Created by huanghaibin on 2018/1/15.
 */

interface EnglishArticleDetailContract {

    interface EmptyView {

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showFavReverseSuccess(boolean isFav);

        void showFavError();

        void showGetDetailSuccess(Article article);

        void hideEmptyLayout();

        void showErrorLayout(int errorType);

        void showReport();

        void showTranslateChange(boolean isEnglish);

        void showTranslateFailure(String message);
    }

    interface View extends BaseListView<Presenter, Article> {
        void showScrollToTop();

        void showCommentSuccess(Comment comment);

        void showCommentError(String message);

        void showGetDetailSuccess(Article article);

        void showTranslateSuccess(Article article,String content);

        void showTranslateFailure(String message);
    }

    interface Presenter extends BaseListPresenter {
        void getArticleDetail();

        void putArticleComment(String content, long referId, long reAuthorId);

        void addClickCount();

        void fav();

        void scrollToTop();

        String formatTextCount(int count);

        String formatTime(long time);

        String formatTimeUnit(long time);

        void translate();

        void report();

        boolean hasGetDetail();
    }
}
