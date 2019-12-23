package net.oschina.app.improve.main.tweet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.base.fragments.BasePagerFragment;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.subscription.SubFragment;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.interf.OnTabReselectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 新版动弹界面
 * Created by huanghaibin on 2017/10/23.
 */

public class TweetPagerFragment extends BasePagerFragment implements OnTabReselectListener {


    @Bind(R.id.viewStatusBar)
    View mStatusBar;

    public static TweetPagerFragment newInstance() {
        return new TweetPagerFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_pager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //setStatusBarPadding();
        if (BaseActivity.hasSetStatusBarColor) {
            mStatusBar.setBackgroundColor(getResources().getColor(R.color.status_bar_color));
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
            }
        }
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TweetFragment.newInstance(TweetFragment.CATALOG_NEW));
        fragments.add(TweetFragment.newInstance(TweetFragment.CATALOG_HOT));
        fragments.add(getSubFragment());
        fragments.add(TweetFragment.newInstance(TweetFragment.CATALOG_MYSELF));
        return fragments;
    }

    @Override
    protected String[] getTitles() {
        return getResources().getStringArray(R.array.tweet_titles);
    }

    private SubFragment getSubFragment() {
        SubTab tab = new SubTab();
        tab.setType(3);
        tab.setFixed(false);
        tab.setName("每日乱弹");
        tab.setNeedLogin(false);
        tab.setHref("https://www.oschina.net/action/apiv2/sub_list?token=263ee86f538884e70ee1ee50aed759b6");
        tab.setSubtype(5);
        tab.setToken("263ee86f538884e70ee1ee50aed759b6");

        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", tab);
        return SubFragment.newInstance(tab);
    }
}
