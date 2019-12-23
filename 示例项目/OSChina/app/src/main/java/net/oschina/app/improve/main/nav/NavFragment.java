package net.oschina.app.improve.main.nav;


import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.main.ExploreFragment;
import net.oschina.app.improve.main.synthesize.SynthesizeFragment;
import net.oschina.app.improve.main.tweet.TweetPagerFragment;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.user.activities.UserFansActivity;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.fragments.UserInfoFragment;
import net.oschina.common.widget.drawable.shape.BorderShape;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavFragment extends BaseFragment implements View.OnClickListener, NoticeManager.NoticeNotify, View.OnLongClickListener {
    @Bind(R.id.nav_item_news)
    NavigationButton mNavNews;
    @Bind(R.id.nav_item_tweet)
    NavigationButton mNavTweet;
    @Bind(R.id.nav_item_explore)
    NavigationButton mNavExplore;
    @Bind(R.id.nav_item_me)
    NavigationButton mNavMe;
    @Bind(R.id.nav_item_tweet_pub)
    ImageView mNavPub;
    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnNavigationReselectListener mOnNavigationReselectListener;

    public NavFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        ShapeDrawable lineDrawable = new ShapeDrawable(new BorderShape(new RectF(0, 1, 0, 0)));
        lineDrawable.getPaint().setColor(getResources().getColor(R.color.list_divider_color));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(R.color.white)),
                lineDrawable
        });
        root.setBackgroundDrawable(layerDrawable);

        mNavNews.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_news,
                SynthesizeFragment.class);

        mNavTweet.init(R.drawable.tab_icon_tweet,
                R.string.main_tab_name_tweet,
                TweetPagerFragment.class);

        mNavExplore.init(R.drawable.tab_icon_explore,
                R.string.main_tab_name_explore,
                ExploreFragment.class);

        mNavMe.init(R.drawable.tab_icon_me,
                R.string.main_tab_name_my,
                UserInfoFragment.class);


    }

    @OnClick({R.id.nav_item_news, R.id.nav_item_tweet,
            R.id.nav_item_explore, R.id.nav_item_me,
            R.id.nav_item_tweet_pub})
    @Override
    public void onClick(View v) {
        if (v instanceof NavigationButton) {
            NavigationButton nav = (NavigationButton) v;
            doSelect(nav);
        } else if (v.getId() == R.id.nav_item_tweet_pub) {
            PubActivity.show(getContext());
            //TweetPublishActivity.show(getContext(), mRoot.findViewById(R.id.nav_item_tweet_pub));
        }
    }

    @OnLongClick({R.id.nav_item_tweet_pub})
    @Override
    public boolean onLongClick(View v) {
        TweetPublishActivity.show(getContext(), mRoot.findViewById(R.id.nav_item_tweet_pub));
        return false;
    }

    public void setup(Context context, FragmentManager fragmentManager, int contentId, OnNavigationReselectListener listener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContainerId = contentId;
        mOnNavigationReselectListener = listener;

        // do clear
        clearOldFragment();
        // do select first
        doSelect(mNavNews);
    }

    public void select(int index) {
        if (mNavMe != null)
            doSelect(mNavMe);
    }

    @SuppressWarnings("RestrictedApi")
    private void clearOldFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (transaction == null || fragments == null || fragments.size() == 0)
            return;
        boolean doCommit = false;
        for (Fragment fragment : fragments) {
            if (fragment != this && fragment != null) {
                transaction.remove(fragment);
                doCommit = true;
            }
        }
        if (doCommit)
            transaction.commitNow();
    }

    private void doSelect(NavigationButton newNavButton) {
        // If the new navigation is me info fragment, we intercept it
        /*
        if (newNavButton == mNavMe) {
            if (interceptMessageSkip())
                return;
        }
        */

        NavigationButton oldNavButton = null;
        if (mCurrentNavButton != null) {
            oldNavButton = mCurrentNavButton;
            if (oldNavButton == newNavButton) {
                onReselect(oldNavButton);
                return;
            }
            oldNavButton.setSelected(false);
        }
        newNavButton.setSelected(true);
        doTabChanged(oldNavButton, newNavButton);
        mCurrentNavButton = newNavButton;
    }

    private void doTabChanged(NavigationButton oldNavButton, NavigationButton newNavButton) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldNavButton != null) {
            if (oldNavButton.getFragment() != null) {
                ft.detach(oldNavButton.getFragment());
            }
        }
        if (newNavButton != null) {
            if (newNavButton.getFragment() == null) {
                Fragment fragment = Fragment.instantiate(mContext,
                        newNavButton.getClx().getName(), null);
                ft.add(mContainerId, fragment, newNavButton.getTag());
                newNavButton.setFragment(fragment);
            } else {
                ft.attach(newNavButton.getFragment());
            }
        }
        ft.commit();
    }

    /**
     * 拦截底部点击，当点击个人按钮时进行消息跳转
     */
    private boolean interceptMessageSkip() {
        NoticeBean bean = NoticeManager.getNotice();
        if (bean.getAllCount() > 0) {
            if (bean.getLetter() + bean.getMention() + bean.getReview() > 0)
                UserMessageActivity.show(getActivity());
            else
                UserFansActivity.show(getActivity(), AccountHelper.getUserId());
            return true;
        }
        return false;
    }

    private void onReselect(NavigationButton navigationButton) {
        OnNavigationReselectListener listener = mOnNavigationReselectListener;
        if (listener != null) {
            listener.onReselect(navigationButton);
        }
    }

    @Override
    public void onNoticeArrived(NoticeBean bean) {
        mNavMe.showRedDot(bean.getUserCount());
    }

    public interface OnNavigationReselectListener {
        void onReselect(NavigationButton navigationButton);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NoticeManager.unBindNotify(this);
    }

    @Override
    protected void initData() {
        super.initData();
        NoticeManager.bindNotify(this);
    }
}
