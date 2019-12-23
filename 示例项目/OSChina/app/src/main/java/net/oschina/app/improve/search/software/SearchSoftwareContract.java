package net.oschina.app.improve.search.software;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.Article;

/**
 * 软件搜索
 * Created by huanghaibin on 2018/1/5.
 */

interface SearchSoftwareContract {

    interface ActionView {
        void showSearchFailure(int strId);

        void showSearchFailure(String str);
    }

    interface View extends BaseListView<Presenter, Article> {

    }

    interface Presenter extends BaseListPresenter {

    }
}
