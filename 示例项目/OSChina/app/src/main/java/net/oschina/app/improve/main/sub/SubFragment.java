package net.oschina.app.improve.main.sub;

import android.os.Bundle;
import android.view.View;

import net.oschina.app.AppConfig;
import net.oschina.app.OSCApplication;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.main.banner.EventHeaderView;
import net.oschina.app.improve.main.header.BlogHeaderView;
import net.oschina.app.improve.main.header.HeaderView;
import net.oschina.app.improve.main.subscription.BlogSubAdapter;
import net.oschina.app.improve.main.subscription.EventSubAdapter;
import net.oschina.app.improve.main.subscription.NewsSubAdapter;
import net.oschina.app.improve.main.subscription.QuestionSubAdapter;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

/**
 * 订阅栏目
 * Created by huanghaibin on 2017/12/18.
 */

public class SubFragment extends BaseRecyclerFragment<SubContract.Presenter, SubBean> implements SubContract.View ,OnTabReselectListener{
    private SubTab mTab;
    private HeaderView mHeaderView;
    private BlogHeaderView mBlogHeaderView;
    private EventHeaderView mEventHeaderView;
    private OSCApplication.ReadState mReadState;
    public static boolean SAVE_ID = false;//是否保存新闻id

    public static SubFragment newInstance(SubTab subTab) {
        SubFragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
        assert mTab != null;
        //CACHE_NAME = mTab.getToken();
    }

    @Override
    protected void initWidget(View root) {
        new SubPresenter(this, mTab);
        super.initWidget(root);
    }

    @Override
    protected void initHeader() {
        if (mTab.getBanner() != null) {
            if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS) {
                mHeaderView = new net.oschina.app.improve.main.header.NewsHeaderView(mContext, mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
            } else if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_EVENT) {
                mEventHeaderView = new EventHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
            } else if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_BLOG) {
                mBlogHeaderView = new BlogHeaderView(mContext, mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
            }
        }
    }

    @Override
    protected void hokeSetHeaderView() {
        if (mTab.getBanner() != null) {
            if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS) {
                mAdapter.setHeaderView(mHeaderView);
            } else if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_EVENT) {
                mAdapter.setHeaderView(mEventHeaderView);
            } else if (mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_BLOG) {
                mAdapter.setHeaderView(mBlogHeaderView);
            }
        }

        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
        if (mAdapter instanceof NewsSubAdapter) {
            ((NewsSubAdapter) mAdapter).setTab(mTab);
        }
        mRefreshLayout.setBottomCount(2);
        if (mPresenter != null) {
            mPresenter.loadCache();
        }
    }

    @Override
    public void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        super.initData();

    }

    @Override
    protected void onItemClick(SubBean sub, int position) {
        if (!TDevice.hasWebView(mContext)) {
            return;
        }
        switch (sub.getType()) {
            case News.TYPE_SOFTWARE:
                SoftwareDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_QUESTION:
                QuestionDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_BLOG:
                BlogDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_TRANSLATE:
                NewsDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_EVENT:
                EventDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(mContext, sub);
                break;
            default:
                UIHelper.showUrlRedirect(mContext, sub.getHref());
                break;
        }

        mReadState.put(sub.getKey());
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
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
        }
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
        if (mEventHeaderView != null) {
            mEventHeaderView.requestBanner();
        }
    }

    @Override
    public void updateKey() {
        if( mTab!= null &&
                mTab.getType() == 6 &&
                mAdapter.getItems().size() != 0){
            SubBean bean = mAdapter.getItem(0);
            if(bean == null)
                return;
            OSCSharedPreference.getInstance().putTheNewsId(bean.getNewsId());
            if(SAVE_ID){
                OSCSharedPreference.getInstance().putLastNewsId(bean.getNewsId());
                ApiHttpClient.setHeaderNewsId();
            }
        }
    }

    @Override
    public void onUpdateTime(String time) {
        if (mAdapter == null)
            return;
        mAdapter.setSystemTime(time);
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getAdapter() {
        int mode = (mHeaderView != null || mEventHeaderView != null || mBlogHeaderView != null)
                ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;
        if (mTab.getType() == News.TYPE_BLOG)
            return new BlogSubAdapter(getActivity(), mode);
        else if (mTab.getType() == News.TYPE_EVENT)
            return new EventSubAdapter(this, mode);
        else if (mTab.getType() == News.TYPE_QUESTION)
            return new QuestionSubAdapter(this, mode);
        return new NewsSubAdapter(getActivity(), mode);
    }

}
