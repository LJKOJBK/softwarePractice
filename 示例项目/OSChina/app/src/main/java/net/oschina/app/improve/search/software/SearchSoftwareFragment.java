package net.oschina.app.improve.search.software;

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
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * 软件搜索
 * Created by huanghaibin on 2018/1/5.
 */

public class SearchSoftwareFragment extends BaseRecyclerFragment<SearchSoftwareContract.Presenter, Article>
        implements SearchSoftwareContract.View {

    static SearchSoftwareFragment newInstance() {
        return new SearchSoftwareFragment();
    }

    @Override
    protected void onItemClick(Article top, int position) {
        if (!TDevice.hasWebView(getContext()))
            return;
        if (top.getType() == 0) {
            if (TypeFormat.isGit(top)) {
                WebActivity.show(getContext(), TypeFormat.formatUrl(top));
            } else {
                ArticleDetailActivity.show(getContext(), top);
            }
        } else {
            int type = top.getType();
            long id = top.getOscId();
            switch (type) {
                case News.TYPE_SOFTWARE:
                    SoftwareDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_QUESTION:
                    QuestionDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_BLOG:
                    BlogDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_TRANSLATE:
                    NewsDetailActivity.show(getContext(), id, News.TYPE_TRANSLATE);
                    break;
                case News.TYPE_EVENT:
                    EventDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_NEWS:
                    NewsDetailActivity.show(getContext(), id);
                    break;
                case Article.TYPE_ENGLISH:
                    EnglishArticleDetailActivity.show(mContext, top);
                    break;
                default:
                    UIHelper.showUrlRedirect(getContext(), top.getUrl());
                    break;
            }
        }
    }

    @Override
    public void onRefreshSuccess(List<Article> data) {
        if (mAdapter == null || mPresenter == null)
            return;
        ((SoftwareAdapter) mAdapter).mKeyword = ((SearchSoftwarePresenter) mPresenter).mKeyword;
        super.onRefreshSuccess(data);
    }

    @Override
    protected BaseRecyclerAdapter<Article> getAdapter() {
        return new SoftwareAdapter(mContext);
    }

}
