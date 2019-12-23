package net.oschina.app.improve.search.v2;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.Article;

import java.util.List;

/**
 * 新版搜索界面
 * Created by huanghaibin on 2018/1/4.
 */
interface SearchContract {

    interface View extends BaseView<Presenter> {
        void showSearchSuccess(SearchBean searchBean);

        void showNotMore();

        void showLoadMoreSuccess(List<Article> articles);

        void onComplete();

        void showSearchFailure(int strId);

        void showSearchFailure(String str);

        void showViewStatus(int status);

        void showAddHistory(String keyword);
    }

    interface Presenter extends BasePresenter {
        void search(int type, String keyword);


        void searchMore(int type,String keyword);
    }
}
