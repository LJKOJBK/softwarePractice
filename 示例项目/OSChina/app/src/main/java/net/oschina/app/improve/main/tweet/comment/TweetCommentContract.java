package net.oschina.app.improve.main.tweet.comment;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.simple.TweetComment;

/**
 * 动弹评论列表
 * Created by huanghaibin on 2017/12/18.
 */

 interface TweetCommentContract {

     interface View extends BaseListView<Presenter,TweetComment>{
         void onRequestSuccess();

         void showDeleteSuccess(int position);

         void showDeleteFailure();
     }

     interface Presenter extends BaseListPresenter{
         void deleteTweetComment(long id,TweetComment comment,int position);
     }
}
