package net.oschina.app.improve.detail.v2;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.Tag;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.detail.pay.alipay.Alipay;
import net.oschina.app.improve.detail.pay.wx.WeChatPay;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.article.ArticleAdapter;
import net.oschina.app.improve.main.synthesize.detail.ArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.improve.utils.ReadedIndexCacheManager;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.ScreenView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.UIHelper;
import net.oschina.common.widget.FlowLayout;

import java.util.List;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailFragment extends BaseFragment implements
        DetailContract.View,
        View.OnClickListener,
        BaseRecyclerAdapter.OnItemClickListener {
    private OSCApplication.ReadState mReadState;
    protected DetailContract.Presenter mPresenter;
    protected OWebView mWebView;
    protected SubBean mBean;
    protected CommentView mCommentView;

    protected int CACHE_CATALOG;
    protected NestedScrollView mViewScroller;
    protected ScreenView mScreenView;
    private ArticleAdapter mAdapter;
    private RecyclerRefreshLayout mRefreshLayout;

    protected RecyclerView mRecyclerView;
    protected View mHeaderView;
    protected FlowLayout mFlowLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mReadState = OSCApplication.getReadState("sub_list");
        mHeaderView = getHeaderView();
        if (mHeaderView != null) {
            mFlowLayout = (FlowLayout) mHeaderView.findViewById(R.id.flowLayout);
            mWebView = (OWebView) mHeaderView.findViewById(R.id.webView);
            mCommentView = (CommentView) mHeaderView.findViewById(R.id.cv_comment);
            mViewScroller = (NestedScrollView) mHeaderView.findViewById(R.id.lay_nsv);
            mScreenView = (ScreenView) mHeaderView.findViewById(R.id.screenView);
        } else {
            mFlowLayout = (FlowLayout) mRoot.findViewById(R.id.flowLayout);
            mWebView = (OWebView) mRoot.findViewById(R.id.webView);
            mCommentView = (CommentView) mRoot.findViewById(R.id.cv_comment);
            mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
            mScreenView = (ScreenView) mRoot.findViewById(R.id.screenView);
        }
        mRefreshLayout = (RecyclerRefreshLayout) mRoot.findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.recyclerView);
        if (mRecyclerView != null && mRefreshLayout != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            if (mHeaderView != null) {
                mAdapter = new ArticleAdapter(mContext, BaseRecyclerAdapter.BOTH_HEADER_FOOTER);
                mAdapter.setHeaderView(mHeaderView);
            } else {
                mAdapter = new ArticleAdapter(mContext, BaseRecyclerAdapter.ONLY_FOOTER);
            }

            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setNestedScrollingEnabled(false);
            mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
                @Override
                public void onRefreshing() {
                    mRefreshLayout.setRefreshing(true);
                    mRefreshLayout.setOnLoading(true);
                    if (mPresenter != null) {
                        mPresenter.onRefresh();
                    }
                }

                @Override
                public void onLoadMore() {
                    if (mAdapter != null) {
                        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
                    }
                    if (mPresenter != null) {
                        mPresenter.onLoadMore();
                    }
                }

                @Override
                public void onScrollToBottom() {
                    if (mAdapter != null) {
                        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
                    }
                    if (mPresenter != null) {
                        mPresenter.onLoadMore();
                    }
                }
            });
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (mPresenter != null) {
                        mPresenter.onRefresh();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position, long itemId) {
        Article top = mAdapter.getItem(position);
        if (top == null)
            return;
        if (top.getType() == 0) {
            if (TypeFormat.isGit(top)) {
                WebActivity.show(mContext, TypeFormat.formatUrl(top));
            } else {
                ArticleDetailActivity.show(mContext, top);
            }
        } else {
            try {
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
                    default:
                        UIHelper.showUrlRedirect(mContext, top.getUrl());
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
                ArticleDetailActivity.show(mContext, top);
            }
        }
        mReadState.put(top.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void showGetDetailSuccess(SubBean bean) {
        this.mBean = bean;
        if (mContext == null) return;
        //码云挂件替换
        mBean.setBody(bean.getBody().replaceAll("(|<pre>)<code>&lt;script src='(//gitee.com/[^>]+)'&gt;&lt;/script&gt;\\s*</code>(|</pre>)",
                "<code><script src='https:$2'></script></code>"));
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);


        if (mFlowLayout != null) {
            mFlowLayout.removeAllViews();
            if (bean.getiTags() == null || bean.getiTags().length == 0) {
                mFlowLayout.setVisibility(View.GONE);
            } else {
                mFlowLayout.setVisibility(View.VISIBLE);
                for (final Tag tag : bean.getiTags()) {
                    TextView tvTag = (TextView) getActivity().getLayoutInflater().inflate(R.layout.flowlayout_item, mFlowLayout, false);
                    if (!TextUtils.isEmpty(tag.getName()))
                        tvTag.setText(tag.getName());
                    mFlowLayout.addView(tvTag);
                    if (tag.getOscId() != 0) {
                        tvTag.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SoftwareDetailActivity.show(mContext, tag.getOscId());
                            }
                        });
                    }
                }
            }
        }

        if (mCommentView == null || mBean.getType() == News.TYPE_TRANSLATE) {
            if (mCommentView != null) {
                mCommentView.setVisibility(View.GONE);
            }
            return;
        }
        SubBean.Statistics statistics = bean.getStatistics();
        if (statistics == null)
            return;
        mCommentView.setShareTitle(mBean.getTitle());
        mCommentView.setTitle(String.format("%s (%d)", getResources().getString(R.string.answer_hint), bean.getStatistics().getComment()));
        mCommentView.init(bean.getId(),
                bean.getType(),
                2,
                bean.getStatistics().getComment(),
                getImgLoader(), (CommentView.OnCommentClickListener) mContext);


    }

    public void onPageFinished() {

        if (mBean == null || mBean.getId() <= 0) return;
        final int index = ReadedIndexCacheManager.getIndex(getContext(), mBean.getId(),
                CACHE_CATALOG);
        if (index != 0) {
            if (mViewScroller == null)
                return;
            mViewScroller.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mViewScroller == null)
                        return;
                    mViewScroller.smoothScrollTo(0, index);
                }
            }, 250);
        }
    }

    @Override
    public void onRefreshSuccess(List<Article> data) {
        if (mAdapter == null)
            return;
        mRefreshLayout.setCanLoadMore(true);
        mAdapter.resetItem(data);
    }

    @Override
    public void onLoadMoreSuccess(List<Article> data) {
        if (mAdapter == null)
            return;
        mAdapter.addAll(data);
        if (data != null && data.size() > 0) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
        } else {
            mRefreshLayout.setCanLoadMore(false);
        }
    }

    @Override
    public void showMoreMore() {
        if (mAdapter == null)
            return;
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }

    @Override
    public void onComplete() {
        if (mAdapter == null || mRefreshLayout == null || mHeaderView == null)
            return;
        mRefreshLayout.onComplete();
        hideOrShowTitle(mAdapter.getItems().size() != 0);
    }

    private void hideOrShowTitle(boolean isShow) {
        if (isShow) {
            mHeaderView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.line2).setVisibility(View.VISIBLE);
            mHeaderView.findViewById(R.id.tv_recommend).setVisibility(View.VISIBLE);
            //mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
        } else {
            mHeaderView.findViewById(R.id.line1).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.line2).setVisibility(View.GONE);
            mHeaderView.findViewById(R.id.tv_recommend).setVisibility(View.GONE);
            mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);
        }
    }

    @Override
    public void showPayDonateError() {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, "获取支付信息失败");
    }

    @Override
    public void showPayDonateSuccess(int type, String sign, WeChatPay.PayResult result) {
        if (mContext == null)
            return;
        if (type == 1) {
            new Alipay(getActivity()).payV2(sign);
        } else {
            WeChatPay pay = new WeChatPay(getActivity());
            if (!pay.isWxAppInstalled()) {
                SimplexToast.show(mContext, "请安装微信");
                return;
            }
            pay.pay(result);
        }
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int favCount, int strId) {
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    @Override
    public void showFavError() {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, "收藏失败");
    }

    @Override
    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void showCommentSuccess(Comment comment) {
        if (mCommentView == null)
            return;
        mBean.getStatistics().setComment(mBean.getStatistics().getComment() + 1);
        mCommentView.setTitle(String.format("%s (%d)", getResources().getString(R.string.answer_hint), mBean.getStatistics().getComment()));
        mCommentView.init(mBean.getId(),
                mBean.getType(),
                2,
                mBean.getStatistics().getComment(),
                getImgLoader(), (CommentView.OnCommentClickListener) mContext);
    }

    @Override
    public void showCommentError(String message) {

    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {

    }

    @Override
    public void showAddRelationError() {
        SimplexToast.show(mContext, "关注失败");
    }

    protected String getExtraString(Object object) {
        return object == null ? "" : object.toString();
    }

    @SuppressWarnings("unused")
    protected abstract int getCommentOrder();

    @Override
    public void onDestroy() {
        if (mBean != null && mBean.getId() > 0 && mViewScroller != null) {
            ReadedIndexCacheManager.saveIndex(getContext(), mBean.getId(), CACHE_CATALOG,
                    (mScreenView != null && mScreenView.isViewInScreen()) ? 0 : mViewScroller.getScrollY());
        }
        mWebView.destroy();
        super.onDestroy();
    }

    protected void showCopyTitle() {
        if (mBean == null)
            return;
        final String text = mBean.getTitle();
        if (TextUtils.isEmpty(text))
            return;
        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(text))
                .show();
    }

    @Override
    public void showScrollToTop() {
        if (mViewScroller != null)
            mViewScroller.scrollTo(0, 0);
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    protected View getHeaderView() {
        return null;
    }
}
