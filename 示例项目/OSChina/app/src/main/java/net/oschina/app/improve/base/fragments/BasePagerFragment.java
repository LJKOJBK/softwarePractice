package net.oschina.app.improve.base.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab Fragment
 * Created by huanghaibin on 2017/10/23.
 */

public abstract class BasePagerFragment extends BaseFragment {
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected Adapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_view_pager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTabLayout = (TabLayout) root.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) root.findViewById(R.id.viewPager);
        mAdapter = new Adapter(getChildFragmentManager());
        mAdapter.reset(getFragments());
        mAdapter.reset(getTitles());
        mViewPager.setAdapter(mAdapter);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            setupTabView();
        }

    }

    protected void setupTabView() {

    }

    @Override
    protected void initData() {

    }

    public static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> mFragment = new ArrayList<>();
        private Fragment mCurFragment;
        private String[] mTitles;

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void reset(List<Fragment> fragments) {
            mFragment.clear();
            mFragment.addAll(fragments);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                mCurFragment = (Fragment) object;
            }
        }

        public Fragment getCurFragment() {
            return mCurFragment;
        }

        void reset(String[] titles) {
            this.mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    protected abstract List<Fragment> getFragments();

    protected abstract String[] getTitles();
}
