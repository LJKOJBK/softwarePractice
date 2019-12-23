package net.oschina.app.improve.user.tags;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.Tags;

/**
 * 用户标签界面
 * Created by haibin on 2018/05/22.
 */
interface UserTagsContract {

    interface View extends BaseListView<Presenter, Tags> {
        void showDeleteSuccess(Tags tags, int position);

        void showDeleteFailure(int strId);

        void showDeleteFailure(String strId);
    }

    interface Presenter extends BaseListPresenter {

        void delete(Tags tags, int position);
    }
}
