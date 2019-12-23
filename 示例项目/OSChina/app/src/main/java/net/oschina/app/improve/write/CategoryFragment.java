package net.oschina.app.improve.write;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.widget.rich.RichEditLayout;
import net.oschina.app.improve.widget.rich.RichEditText;
import net.oschina.app.improve.widget.rich.TextSection;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 列表界面的布局、系统分类、博客分类
 * Created by huanghaibin on 2017/8/31.
 */

public class CategoryFragment extends BaseFragment {

    @Bind(R.id.recyclerBlog)
    RecyclerView mRecyclerBlog;

    @Bind(R.id.recyclerSystem)
    RecyclerView mRecyclerSystem;

    private BlogCategoryAdapter mBlogAdapter;
    private SystemCategoryAdapter mSystemAdapter;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerBlog.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerSystem.setLayoutManager(new LinearLayoutManager(mContext));
        mBlogAdapter = new BlogCategoryAdapter(mContext);
        mSystemAdapter = new SystemCategoryAdapter(mContext);
        mRecyclerSystem.setAdapter(mSystemAdapter);
        mRecyclerBlog.setAdapter(mBlogAdapter);
        mBlogAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mBlogAdapter.setSelectedPosition(position);
            }
        });
        mSystemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mSystemAdapter.setSelectedPosition(position);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        getCategories();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mRoot != null) {
            if (RichEditLayout.KEYBOARD_HEIGHT != 0) {
                mRoot.getLayoutParams().height = RichEditLayout.KEYBOARD_HEIGHT;
            } else {
                mRoot.getLayoutParams().height = Util.dipTopx(mContext, 270);
            }
        }
    }

    long getCategoryId() {
        if (mBlogAdapter != null) {
            BlogCategory category = mBlogAdapter.getSelectedItem();
            if (category != null)
                return category.getId();
        }
        return -1;
    }

    int getSystemId() {
        if (mSystemAdapter != null) {
            SystemCategoryAdapter.SystemCategory category = mSystemAdapter.getSelectedItem();
            if (category != null)
                return category.getId();
        }
        return -1;
    }

    private void getCategories() {
        OSChinaApi.getBlogCategories(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("onSuccess", "" + responseString);
                try {
                    Type type = new TypeToken<ResultBean<List<BlogCategory>>>() {
                    }.getType();
                    ResultBean<List<BlogCategory>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mBlogAdapter.resetItem(bean.getResult());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
