package net.oschina.app.improve.user.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.main.subscription.BlogSubAdapter;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * created by thanatosx  on 2016/8/16.
 */
public class UserBlogFragment extends BaseRecyclerViewFragment<SubBean> {

    @SuppressWarnings("unused")
    public static final String HISTORY_BLOG = "history_my_blog";
    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";
    private long userId;

    public static Fragment instantiate(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        Fragment fragment = new UserBlogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong(BUNDLE_KEY_USER_ID);
    }

    @Override
    protected void requestData() {
        super.requestData();
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getSomeoneBlogs(token, userId, null, mHandler);
    }

    @Override
    public void initData() {
        super.initData();
        if(AccountHelper.isLogin() && AccountHelper.getUserId() == userId){
            mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
                @Override
                public void onLongClick(final int position, long itemId) {
                    final SubBean bean = mAdapter.getItem(position);
                    if (bean == null) return;
                    DialogHelper.getConfirmDialog(mContext,
                            "温馨提示",
                            "确定删除该博客？",
                            "删除", "取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteBlog(bean.getId(), position);
                                }
                            }).show();
                }
            });
        }
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        return new BlogSubAdapter(getContext(), BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        SubBean blog = mAdapter.getItem(position);
        if (blog == null) return;
        BlogDetailActivity.show(getActivity(), blog.getId());
    }

    private void deleteBlog(long id, final int position) {
        OSChinaApi.deleteBlog(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if(mContext == null)return;
                SimplexToast.show(mContext,"网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SubBean>>() {
                    }.getType();
                    ResultBean<SubBean> bean = new Gson().fromJson(responseString, type);
                    if (bean.getCode() == ResultBean.RESULT_SUCCESS) {
                        mAdapter.removeItem(position);
                        SimplexToast.show(mContext,"删除成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}