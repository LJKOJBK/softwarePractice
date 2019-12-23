package net.oschina.app.improve.write;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.widget.rich.TextSection;

import java.util.List;

/**
 * 写博客
 * Created by huanghaibin on 2017/8/3.
 */

interface WriteContract {

    interface View extends BaseView<Presenter> {
        void showPubBlogSuccess(int strId, SubBean bean);

        void showPubBlogFailure(int strId);
    }

    interface Presenter extends BasePresenter {
        /**
         * 发布博客
         *
         * @param blog 标题
         */
        void pubBlog(Blog blog, List<TextSection> sections);
    }
}
