package net.oschina.app.improve.search.software;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 只搜索软件
 * Created by huanghaibin on 2018/1/5.
 */
public class SearchSoftwareActivity extends BackActivity implements
        View.OnClickListener,
        SearchSoftwareContract.ActionView {
    @Bind(R.id.view_searcher)
    SearchView mViewSearch;
    @Bind(R.id.search_src_text)
    EditText mViewSearchEditor;
    private SearchSoftwarePresenter mPresenter;

    public static void show(Context context, String keyword) {
        Intent intent = new Intent(context, SearchSoftwareActivity.class);
        intent.putExtra("keyword", keyword);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_software;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();

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
                TDevice.closeKeyboard(mViewSearchEditor);
                mPresenter.mKeyword = query;
                mPresenter.onRefreshing();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mViewSearchEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        String keyword = getIntent().getStringExtra("keyword");
        mViewSearchEditor.setText(keyword);

        SearchSoftwareFragment fragment = SearchSoftwareFragment.newInstance();
        addFragment(R.id.fl_content, fragment);
        mPresenter = new SearchSoftwarePresenter(fragment, this);
        mPresenter.mKeyword = keyword;
    }

    @OnClick({R.id.tv_search})
    @Override
    public void onClick(View view) {
        if (mPresenter == null)
            return;
        TDevice.closeKeyboard(mViewSearchEditor);
        mPresenter.mKeyword = mViewSearchEditor.getText().toString().trim();
        mPresenter.onRefreshing();
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
}
