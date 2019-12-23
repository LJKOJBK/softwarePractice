package net.oschina.app.improve.user.tags.search;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.Tags;

import java.util.List;

/**
 * 用户搜索标签界面
 * Created by haibin on 2018/05/28.
 */
interface SearchTagsContract {

    interface View extends BaseView<Presenter> {
        void showSearchSuccess(List<Tags> list);

        void showNotMore();

        void showLoadMoreSuccess(List<Tags> articles);

        void onComplete();

        void showSearchFailure(int strId);

        void showSearchFailure(String str);


        void showPutSuccess(Tags tags, int position);

        void showPutFailure(int strId);

        void showPutFailure(String strId);
    }

    interface Presenter extends BasePresenter {
        void search(String keyword);


        void searchMore(String keyword);

        void putTags(Tags tags, int position);
    }
}
