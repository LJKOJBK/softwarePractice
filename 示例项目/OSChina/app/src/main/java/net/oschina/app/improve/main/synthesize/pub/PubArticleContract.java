package net.oschina.app.improve.main.synthesize.pub;

import net.oschina.app.improve.base.BasePresenter;
import net.oschina.app.improve.base.BaseView;

/**
 * 发布分享文章
 * Created by huanghaibin on 2017/12/1.
 */

interface PubArticleContract {

    interface View extends BaseView<Presenter> {
        void showPubSuccess(int strId);

        void showPubFailure(String message);

        void showGetTitleSuccess(String title);

        void showGetTitleFailure(String message);
    }

    interface Presenter extends BasePresenter {
        void putArticle(String url, String title);

        void getTitle(String url);

        /**
         * 判断是不是微信公众号文章
         * @param url
         */
        boolean isWechatUrl(String url);
    }
}
