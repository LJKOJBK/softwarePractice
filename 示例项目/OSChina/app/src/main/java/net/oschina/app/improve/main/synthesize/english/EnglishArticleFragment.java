package net.oschina.app.improve.main.synthesize.english;

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
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.detail.ArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.english.detail.EnglishArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * 英文推荐界面
 * Created by huanghaibin on 2017/10/23.
 */

public class EnglishArticleFragment extends BaseRecyclerFragment<EnglishArticleContract.Presenter, Article> implements EnglishArticleContract.View, OnTabReselectListener {

    private OSCApplication.ReadState mReadState;

    public static EnglishArticleFragment newInstance() {
        return new EnglishArticleFragment();
    }

    @Override
    protected void initWidget(View root) {
        new EnglishArticlePresenter(this);
        super.initWidget(root);
    }

    @Override
    protected void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        if (mPresenter != null) {
            mPresenter.loadCache();
        }
        super.initData();
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
    public void onScrollToBottom() {
        if (mPresenter != null) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
            mPresenter.onLoadMore();
        }
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
        return new EnglishArticleAdapter(mContext, BaseRecyclerAdapter.ONLY_FOOTER);
    }
}
