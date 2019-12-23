package net.oschina.app.improve.user.tags.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tags;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 用户搜索标签界面
 * Created by haibin on 2018/05/28.
 */
public class SearchTagsActivity extends BackActivity implements
        View.OnClickListener,
        SearchTagsContract.View,
        BaseRecyclerAdapter.OnItemClickListener {

    @Bind(R.id.view_searcher)
    SearchView mViewSearch;
    @Bind(R.id.search_src_text)
    EditText mViewSearchEditor;
    @Bind(R.id.refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private SearchTagAdapter mAdapter;
    private SearchTagsPresenter mPresenter;

    public static void show(Context context) {
        if (!AccountHelper.isLogin()) {
            return;
        }
        context.startActivity(new Intent(context, SearchTagsActivity.class));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_tags_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter = new SearchTagAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                mPresenter.search(mViewSearch.getQuery().toString());
            }

            @Override
            public void onLoadMore() {
                mPresenter.searchMore(mViewSearch.getQuery().toString());
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
            }

            @Override
            public void onScrollToBottom() {
                // TODO: 2018/1/5
            }
        });

        mViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mViewSearch.clearFocus();
                TDevice.closeKeyboard(mViewSearchEditor);
                mPresenter.search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        mAdapter.setRelateListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tags tags = mAdapter.getItem(position);
                if (tags == null)
                    return;
                mPresenter.putTags(tags, position);
            }
        });

        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new SearchTagsPresenter(this);
    }

    @OnClick({R.id.tv_search})
    @Override
    public void onClick(View view) {
        if (mPresenter == null)
            return;
        TDevice.closeKeyboard(mViewSearchEditor);
        mPresenter.search(mViewSearchEditor.getText().toString().trim());
    }

    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void showSearchSuccess(List<Tags> list) {
        if (isDestroyed()) {
            return;
        }
        mAdapter.resetItem(list);
    }

    @Override
    public void showLoadMoreSuccess(List<Tags> list) {
        if (isDestroyed()) {
            return;
        }
        mAdapter.addAll(list);
    }

    @Override
    public void showSearchFailure(int strId) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, strId);
    }

    @Override
    public void showSearchFailure(String str) {
        if (isDestroyed()) {
            return;
        }
        SimplexToast.show(this, str);
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
    public void showNetworkError(int strId) {
        if (isDestroyed()) {
            return;
        }
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }


    @Override
    public void showPutSuccess(Tags tags, int position) {
        if (isDestroyed())
            return;
        Tags item = mAdapter.getItem(position);
        if (item != null && tags.equals(item)) {
            mAdapter.updateItem(position);
        }
    }

    @Override
    public void showPutFailure(int strId) {
        if (isDestroyed())
            return;
        SimplexToast.show(this, strId);
    }

    @Override
    public void showPutFailure(String strId) {
        if (isDestroyed())
            return;
        SimplexToast.show(this, strId);
    }

    @Override
    public void setPresenter(SearchTagsContract.Presenter presenter) {

    }

}
