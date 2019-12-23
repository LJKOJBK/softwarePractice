package net.oschina.app.improve.main.sub;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.SubBean;

/**
 * 订阅接口
 * Created by huanghaibin on 2017/12/18.
 */

public interface SubContract {

    interface View extends BaseListView<Presenter, SubBean> {
        void onUpdateTime(String time);

        void updateKey();
    }

    interface Presenter extends BaseListPresenter {
        void loadCache();
    }
}
