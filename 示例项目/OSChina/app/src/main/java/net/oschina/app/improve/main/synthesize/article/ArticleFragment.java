package net.oschina.app.improve.main.synthesize.article;

import android.view.View;

import net.oschina.app.OSCApplication;
import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.main.banner.HeaderView;
import net.oschina.app.improve.main.banner.NewsHeaderView;
import net.oschina.app.improve.main.introduce.ArticleIntroduceActivity;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.detail.ArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.english.detail.EnglishArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * 头条界面
 * Created by huanghaibin on 2017/10/23.
 */

public class ArticleFragment extends BaseRecyclerFragment<ArticleContract.Presenter, Article> implements ArticleContract.View, OnTabReselectListener {

    private HeaderView mHeaderView;
    private OSCApplication.ReadState mReadState;

    public static ArticleFragment newInstance() {
        return new ArticleFragment();
    }

    @Override
    protected void initWidget(View root) {
        new ArticlePresenter(this);
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        ArticleIntroduceActivity.show(mContext);
        mReadState = OSCApplication.getReadState("sub_list");
        if (mPresenter != null) {
            mPresenter.loadCache();
        }
        mHeaderView = new NewsHeaderView(mContext, getImgLoader(),
                "https://www.oschina.net/action/apiv2/banner?catalog=1",
                "d6112fa662bc4bf21084670a857fbd20banner1");
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mRefreshLayout.setBottomCount(2);
    }

    @Override
    protected void onItemClick(Article top, int position) {
        if (!TDevice.hasWebView(mContext))
            return;
        if (top.getType() == 0) {
            if (TypeFormat.isGit(top)) {
                WebActivity.show(mContext, TypeFormat.formatUrl(top));
            } else {
                ArticleDetailActivity.show(mContext, top);
            }
        } else {
            int type = top.getType();
            long id = top.getOscId();
            switch (type) {
                case News.TYPE_SOFTWARE:
                    SoftwareDetailActivity.show(mContext, id);
                    break;
                case News.TYPE_QUESTION:
                    QuestionDetailActivity.show(mContext, id);
                    break;
                case News.TYPE_BLOG:
                    BlogDetailActivity.show(mContext, id);
                    break;
                case News.TYPE_TRANSLATE:
                    NewsDetailActivity.show(mContext, id, News.TYPE_TRANSLATE);
                    break;
                case News.TYPE_EVENT:
                    EventDetailActivity.show(mContext, id);
                    break;
                case News.TYPE_NEWS:
                    NewsDetailActivity.show(mContext, id);
                    break;
                case Article.TYPE_ENGLISH:
                    EnglishArticleDetailActivity.show(mContext, top);
                    break;
                default:
                    UIHelper.showUrlRedirect(mContext, top.getUrl());
                    break;
            }
        }
        mReadState.put(top.getKey());
        mAdapter.updateItem(position);
    }


    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null) {
            mHeaderView.requestBanner();
        }
    }

    @Override
    public void onScrollToBottom() {
        if (mPresenter != null) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
            mPresenter.onLoadMore();
        }
    }

    @Override
    public void versionPast() {
        if(mContext == null)
            return;
        SimplexToast.show(mContext,"当前版本已经过期，请升级最新版本");
    }

    @Override
    public void onTabReselect() {
        if (mRecyclerView != null && mPresenter != null) {
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
        }
    }

    @Override
    public void onRefreshSuccess(List<Article> data) {
        super.onRefreshSuccess(data);
        if (data.size() < 8) {
            mRefreshLayout.setOnLoading(true);
            onLoadMore();
        }
    }

    @Override
    protected BaseRecyclerAdapter<Article> getAdapter() {
        return new ArticleAdapter(mContext, BaseRecyclerAdapter.BOTH_HEADER_FOOTER);
    }
}
