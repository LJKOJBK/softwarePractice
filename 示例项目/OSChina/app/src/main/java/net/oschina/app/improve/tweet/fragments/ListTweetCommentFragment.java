package net.oschina.app.improve.tweet.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos
 * on 16/6/13.
 */
public class ListTweetCommentFragment extends BaseRecyclerViewFragment<TweetComment>
        implements TweetDetailContract.ICmnView, BaseRecyclerAdapter.OnItemLongClickListener {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private int mDeleteIndex = 0;

    public static ListTweetCommentFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetCommentFragment fragment = new ListTweetCommentFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOperator = (TweetDetailContract.Operator) context;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mOperator.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<TweetComment> getRecyclerAdapter() {
        TweetCommentAdapter adapter = new TweetCommentAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return adapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetComment>>>() {
        }.getType();
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mAdapter.getCount() < 20 && mAgencyView != null)
            mAgencyView.resetCmnCount(mAdapter.getCount());
    }

    @Override
    public void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getTweetCommentList(mOperator.getTweetDetail().getId(), token, mHandler);
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
        super.onItemClick(position, itemId);
        TweetComment item = mAdapter.getItem(position);
        if (item != null)
            mOperator.toReply(item);
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final TweetComment comment = mAdapter.getItem(position);
        if (comment == null) return;
        mDeleteIndex = position;

        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(comment.getContent()))
                .addOther(comment.getAuthor().getId() == AccountHelper.getUserId(),
                        R.string.delete, new Runnable() {
                            @Override
                            public void run() {
                                handleDeleteComment(comment);
                            }
                        }).show();

    }

    private void handleDeleteComment(TweetComment comment) {
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        OSChinaApi.deleteTweetComment(mOperator.getTweetDetail().getId(), comment.getId(), new TextHttpResponseHandler() {
            private ProgressDialog dialog = DialogHelper.getProgressDialog(getContext(), "正在删除……", false);

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort("删除失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean>() {
                        }.getType());
                if (result.isSuccess()) {
                    mAdapter.removeItem(mDeleteIndex);
                    int count = mOperator.getTweetDetail().getCommentCount() - 1;
                    mOperator.getTweetDetail().setCommentCount(count);
                    mAgencyView.resetCmnCount(count);
                    AppContext.showToastShort("删除成功");
                } else {
                    AppContext.showToastShort("删除失败");
                }
            }
        });
    }

    @Override
    public void onCommentSuccess(TweetComment comment) {
        if (mContext == null || mRefreshLayout == null) {
            return;
        }
        if (mRefreshLayout.isLoding()) {
            ApiHttpClient.cancelALL();
        }
        isRefreshing = true;
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        mRefreshLayout.setOnLoading(true);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);
        OSChinaApi.getTweetCommentList(mOperator.getTweetDetail().getId(), null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mContext == null)
                    return;
                onRequestError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (mContext == null)
                    return;
                try {
                    ResultBean<PageBean<TweetComment>> resultBean = AppOperator.createGson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        showRefreshSuccess(resultBean);
                        onRequestSuccess(resultBean.getCode());
                    } else {
                        if (resultBean != null && resultBean.getCode() == ResultBean.RESULT_TOKEN_ERROR) {
                            SimplexToast.show(getActivity(), resultBean.getMessage());
                        }
                        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mContext == null)
                    return;
                onRequestFinish();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                if (mContext == null)
                    return;
                onRequestFinish();
            }
        });
    }

    private void showRefreshSuccess(ResultBean<PageBean<TweetComment>> resultBean) {
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        AppConfig.getAppConfig(getActivity()).set("system_time", resultBean.getTime());
        mBean.setItems(resultBean.getResult().getItems());
        mAdapter.clear();
        mAdapter.addAll(mBean.getItems());
        mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
        mRefreshLayout.setCanLoadMore(true);
        if (resultBean.getResult().getItems() == null
                || resultBean.getResult().getItems().size() < 20)
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(
                    isNeedEmptyView()
                            ? EmptyLayout.NODATA
                            : EmptyLayout.HIDE_LAYOUT);
        }
    }

    @Override
    public List<TweetComment> getComments() {
        return mAdapter.getItems();
    }
}
