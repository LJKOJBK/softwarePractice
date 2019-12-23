package net.oschina.app.improve.main.synthesize;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.base.fragments.BasePagerFragment;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.sub.SubFragment;
import net.oschina.app.improve.main.synthesize.article.ArticleFragment;
import net.oschina.app.improve.main.synthesize.english.EnglishArticleFragment;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.search.v2.SearchActivity;
import net.oschina.app.interf.OnTabReselectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 新版综合界面
 * Created by huanghaibin on 2017/10/23.
 */

public class SynthesizeFragment extends BasePagerFragment implements
        NoticeManager.NoticeNotify,
        OnTabReselectListener,
        View.OnClickListener {

    private int mCurrentItem;
    private TextView mTextCount;

    @Bind(R.id.viewStatusBar)
    View mStatusBar;

    public static SynthesizeFragment newInstance() {
        return new SynthesizeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_synthesize;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //setStatusBarPadding();
        if (BaseActivity.hasSetStatusBarColor) {
            mStatusBar.setBackgroundColor(getResources().getColor(R.color.status_bar_color));
        }
        NoticeManager.bindNotify(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleText(false, mCurrentItem);
                setTitleText(true, position);
                mCurrentItem = position;
                SubFragment.SAVE_ID = position == 2;
                if (SubFragment.SAVE_ID) {
                    OSCSharedPreference.getInstance().putLastNewsId(OSCSharedPreference.getInstance().getTheNewsId());
                    if (mRoot != null && mAdapter != null && hasCount()) {
                        mRoot.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Fragment fragment = mAdapter.getCurFragment();
                                if (fragment != null && fragment instanceof SubFragment) {
                                    ((SubFragment) fragment).onRefreshing();
                                }
                            }
                        }, 1000);
                    }
                }
                ApiHttpClient.setHeaderNewsId();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(1);
    }

    private boolean hasCount() {
        return mTextCount != null && !"0".equals(mTextCount.getText().toString());
    }

    private void setTitleText(boolean isSelected, int position) {
        TabLayout.Tab tab = mTabLayout.getTabAt(position);
        if (tab == null) return;
        View view = tab.getCustomView();
        if (view == null) return;
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setTextColor(isSelected ? 0xff24cf5f : 0xff6A6A6A);
    }

    @Override
    protected void setupTabView() {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
                if (tab.getCustomView() != null) {
                    View tabView = (View) tab.getCustomView().getParent();
                    tabView.setTag(i);
                    tabView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
        if (mTextCount == null || bean == null)
            return;
        if (bean.getNewsCount() == 0) {
            mTextCount.setVisibility(View.GONE);
        } else {
            mTextCount.setVisibility(View.VISIBLE);
            mTextCount.setText(String.valueOf(bean.getNewsCount()));
        }
    }


    @SuppressLint("InflateParams")
    private View getTabView(int i) {
        View view = mInflater.inflate(R.layout.tab_synthesize, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_count = (TextView) view.findViewById(R.id.tv_count);
        if (i == 0) {
            tv_title.setTextColor(0xff24cf5f);
        }
        if (i == 2) {
            mTextCount = tv_count;
        }
        tv_title.setText(mAdapter.getPageTitle(i));
        return view;
    }

    @OnClick({R.id.iv_search})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                //SearchActivity.show(mContext);
                SearchActivity.show(getContext());
                break;
        }
    }

    @Override
    public void onTabReselect() {
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            BasePagerFragment.Adapter pagerAdapter = (BasePagerFragment.Adapter) mViewPager.getAdapter();
            Fragment fragment = pagerAdapter.getCurFragment();
            if (fragment != null) {
                if (fragment instanceof BaseGeneralListFragment)
                    ((BaseGeneralListFragment) fragment).onTabReselect();
                else if (fragment instanceof BaseGeneralRecyclerFragment)
                    ((BaseGeneralRecyclerFragment) fragment).onTabReselect();
                else if (fragment instanceof ArticleFragment)
                    ((ArticleFragment) fragment).onTabReselect();
                else if (fragment instanceof EnglishArticleFragment)
                    ((EnglishArticleFragment) fragment).onTabReselect();
                else if (fragment instanceof SubFragment) {
                    ((SubFragment) fragment).onTabReselect();
                }
            }
        }
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(EnglishArticleFragment.newInstance());
        fragments.add(ArticleFragment.newInstance());
        fragments.add(getSubFragment(6,
                "开源资讯",
                "https://www.oschina.net/action/apiv2/sub_list?token=e6142fa662bc4bf21083870a957fbd20",
                1,
                "e6142fa662bc4bf21083870a957fbd20"));
        fragments.add(getSubFragment(2,
                "技术问答",
                "https://www.oschina.net/action/apiv2/sub_list?token=98d04eb58a1d12b75d254deecbc83790",
                3,
                "98d04eb58a1d12b75d254deecbc83790"));
        fragments.add(getSubFragment(3,
                "推荐博客",
                "https://www.oschina.net/action/apiv2/sub_list?token=df985be3c5d5449f8dfb47e06e098ef9",
                4,
                "df985be3c5d5449f8dfb47e06e098ef9"));

        return fragments;
    }

    @Override
    protected String[] getTitles() {
        return getResources().getStringArray(R.array.synthesize_titles);
    }

    private SubFragment getSubFragment(int type, String title, String url, int subType, String token) {
        SubTab tab = new SubTab();
        if (type == 3) {
            SubTab.Banner banner = tab.new Banner();
            banner.setCatalog(4);
            banner.setHref("https://www.oschina.net/action/apiv2/banner?catalog=4");
            tab.setBanner(banner);
        }
        tab.setType(type);
        tab.setFixed(false);
        tab.setName(title);
        tab.setNeedLogin(false);
        tab.setHref(url);
        tab.setSubtype(subType);
        tab.setToken(token);

        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", tab);
        return SubFragment.newInstance(tab);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.bindNotify(this);
    }
}
