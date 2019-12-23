package net.oschina.app.improve.main.tweet.praise;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.simple.TweetLike;

/**
 * 动弹点赞列表
 * Created by huanghaibin on 2017/12/18.
 */

 interface TweetPraiseContract {

     interface View extends BaseListView<Presenter,TweetLike>{

     }

     interface Presenter extends BaseListPresenter{

     }
}
