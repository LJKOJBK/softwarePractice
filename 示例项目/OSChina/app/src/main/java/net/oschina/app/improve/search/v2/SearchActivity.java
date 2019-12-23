package net.oschina.app.improve.search.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
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
import net.oschina.app.improve.search.adapters.SearchHistoryAdapter;
import net.oschina.app.improve.search.software.SearchSoftwareActivity;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.Keyboard;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 新版搜索界面
 * Created by huanghaibin on 2018/1/4.
 */

public class SearchActivity extends BackActivity implements
        SearchContract.View, View.OnClickListener,
        BaseRecyclerAdapter.OnItemClickListener {

    private SearchPresenter mPresenter;
    @Bind(R.id.view_searcher)
    SearchView mViewSearch;
    @Bind(R.id.search_src_text)
    EditText mViewSearchEditor;
    @Bind(R.id.refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;
    private SearchHeaderView mHeaderView;
    @Bind(R.id.recyclerViewHistory)
    RecyclerView mRecyclerViewHistory;

    private SearchHistoryAdapter mSearchAdapter;
    private static final String CACHE_NAME = "search_history";

    public static void show(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mHeaderView = new SearchHeaderView(this);
        mAdapter = new SearchAdapter(this);
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mPresenter.search(SearchPresenter.TYPE_DEFAULT, mViewSearch.getQuery().toString());
            }

            @Override
            public void onLoadMore() {
                mPresenter.searchMore(SearchPresenter.TYPE_DEFAULT, mViewSearch.getQuery().toString());
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
            }

            @Override
            public void onScrollToBottom() {
                // TODO: 2018/1/5
            }
        });

        mHeaderView.setOrderChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_relate_order:
                        mPresenter.mOrder = SearchPresenter.ORDER_DEFAULT;
                        break;
                    case R.id.rb_hot_order:
                        mPresenter.mOrder = SearchPresenter.ORDER_HOT;
                        break;
                    case R.id.rb_time_order:
                        mPresenter.mOrder = SearchPresenter.ORDER_TIME;
                        break;
                }
                mRefreshLayout.setRefreshing(true);
                mPresenter.search(SearchPresenter.TYPE_DEFAULT, mViewSearchEditor.getText().toString());
            }
        });
        mHeaderView.setSearchSoftwareListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchSoftwareActivity.show(SearchActivity.this, mPresenter.mKeyword);
            }
        });
        mViewSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // 阻止点击关闭按钮 collapse icon
                return true;
            }
        });
        mViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mViewSearch.clearFocus();
                TDevice.closeKeyboard(mViewSearchEditor);
                mPresenter.search(SearchPresenter.TYPE_DEFAULT, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mRecyclerViewHistory.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);


        mSearchAdapter = new SearchHistoryAdapter(this);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewHistory.setAnimation(null);
        mRecyclerViewHistory.setAdapter(mSearchAdapter);
        List<SearchHistoryAdapter.SearchItem> items = CacheManager.readListJson(this, CACHE_NAME, SearchHistoryAdapter.SearchItem.class);
        mSearchAdapter.addAll(items);
        if (mSearchAdapter.getItems().size() != 0) {
            mSearchAdapter.addItem(new SearchHistoryAdapter.SearchItem("清空搜索历史", 1));
        }
        mSearchAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                SearchHistoryAdapter.SearchItem item = mSearchAdapter.getItem(position);
                if (item != null && item.getType() == 0) {
                    String query = item.getSearchText();
                    mViewSearchEditor.setText(query);
                    mViewSearchEditor.setSelection(query.length());
                    mPresenter.search(SearchPresenter.TYPE_DEFAULT, query);
                    TDevice.closeKeyboard(mViewSearchEditor);
                } else {
                    DialogHelper.getConfirmDialog(SearchActivity.this, "提示", "确认清空搜索历史记录吗？", "确认", "取消", true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSearchAdapter.clear();
                        }
                    }).show();
                }
            }
        });
        mViewSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || mViewSearchEditor == null)
                    return;
                Keyboard.openKeyboard(mViewSearchEditor);
            }
        }, 200);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new SearchPresenter(this);
    }

    @OnClick({R.id.tv_search})
    @Override
    public void onClick(View view) {
        if (mPresenter == null)
            return;
        TDevice.closeKeyboard(mViewSearchEditor);
        mPresenter.search(SearchPresenter.TYPE_DEFAULT, mViewSearchEditor.getText().toString().trim());
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Article top = mAdapter.getItem(position);
        if (top == null) {
            return;
        }
        if (!TDevice.hasWebView(this))
            return;
        if (top.getType() == 0) {
            if (TypeFormat.isGit(top)) {
                WebActivity.show(this, TypeFormat.formatUrl(top));
            } else {
                ArticleDetailActivity.show(this, top);
            }
        } else {
            int type = top.getType();
            long id = top.getOscId();
            switch (type) {
                case News.TYPE_SOFTWARE:
                    SoftwareDetailActivity.show(this, id);
                    break;
                case News.TYPE_QUESTION:
                    QuestionDetailActivity.show(this, id);
                    break;
                case News.TYPE_BLOG:
                    BlogDetailActivity.show(this, id);
                    break;
                case News.TYPE_TRANSLATE:
                    NewsDetailActivity.show(this, id, News.TYPE_TRANSLATE);
                    break;
                case News.TYPE_EVENT:
                    EventDetailActivity.show(this, id);
                    break;
                case News.TYPE_NEWS:
                    NewsDetailActivity.show(this, id);
                    break;
                case Article.TYPE_ENGLISH:
                    EnglishArticleDetailActivity.show(this, top);
                    break;
                default:
                    UIHelper.showUrlRedirect(this, top.getUrl());
                    break;
            }
        }
    }

    @Override
    public void showViewStatus(int status) {
        if (isDestroyed())
            return;
        mRecyclerViewHistory.setVisibility(status);
    }

    @Override
    public void showAddHistory(String keyword) {
        if (isDestroyed())
            return;
        SearchHistoryAdapter.SearchItem item = new SearchHistoryAdapter.SearchItem(keyword);
        if (mSearchAdapter.getItems().contains(item)) {
            mSearchAdapter.removeItem(item);
        }
        mSearchAdapter.addItem(0, item);
        mRecyclerViewHistory.scrollToPosition(0);
        SearchHistoryAdapter.SearchItem last = mSearchAdapter.getItem(mSearchAdapter.getItems().size() - 1);
        if (last != null && last.getType() == 0) {
            mSearchAdapter.addItem(new SearchHistoryAdapter.SearchItem("清空搜索历史", 1));
        }
    }

    @Override
    public void showNetworkError(int strId) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, strId);
    }

    @Override
    public void showSearchSuccess(SearchBean searchBean) {
        if (isDestroyed())
            return;
        mAdapter.mKeyword = mPresenter.mKeyword;
        mHeaderView.mKeyword = mPresenter.mKeyword;
        mHeaderView.setData(searchBean);
        mAdapter.resetItem(searchBean.getArticles());
    }

    @Override
    public void showSearchFailure(int strId) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, strId);
    }

    @Override
    public void showNotMore() {
        if (isDestroyed()) {
            return;
        }
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }

    @Override
    public void onComplete() {
        if (isDestroyed()) {
            return;
        }
        mRefreshLayout.onComplete();
    }

    @Override
    public void showLoadMoreSuccess(List<Article> articles) {
        if (isDestroyed()) {
            return;
        }
        mAdapter.addAll(articles);
    }

    @Override
    public void showSearchFailure(String str) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, str);
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        // TODO: 2018/1/4
    }

    @Override
    protected void onDestroy() {
        SearchHistoryAdapter.SearchItem last = mSearchAdapter.getItem(mSearchAdapter.getItems().size() - 1);
        if (last != null && last.getType() != 0) {
            mSearchAdapter.removeItem(last);
        }
        CacheManager.saveToJson(this, CACHE_NAME, mSearchAdapter.getItems());
        super.onDestroy();
    }
}
