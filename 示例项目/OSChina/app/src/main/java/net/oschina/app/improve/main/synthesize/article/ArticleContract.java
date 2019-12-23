package net.oschina.app.improve.main.synthesize.article;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.Article;

/**
 *  头条界面
 * Created by huanghaibin on 2017/10/23.
 */

 interface ArticleContract {

     interface View extends BaseListView<Presenter,Article>{
         /**
          * 版本过期
          */
         void versionPast();
     }

     interface Presenter extends BaseListPresenter{
         void loadCache();
     }
}
