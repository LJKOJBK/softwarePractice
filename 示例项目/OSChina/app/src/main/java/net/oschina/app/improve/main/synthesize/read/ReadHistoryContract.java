package net.oschina.app.improve.main.synthesize.read;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.Article;

/**
 * 阅读记录
 * Created by huanghaibin on 2017/12/4.
 */

interface ReadHistoryContract {

    interface View extends BaseListView<Presenter, Article> {

    }

    interface Presenter extends BaseListPresenter {

    }
}
