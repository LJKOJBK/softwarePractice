package net.oschina.app.improve.main;

import android.view.View;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.detail.SubActivity;
import net.oschina.app.improve.git.feature.FeatureActivity;
import net.oschina.app.improve.git.gist.GistActivity;
import net.oschina.app.improve.main.discover.ShakePresentActivity;
import net.oschina.app.improve.main.synthesize.web.ZBWebActivity;
import net.oschina.app.improve.nearby.NearbyActivity;
import net.oschina.app.improve.search.v2.SearchActivity;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发现界面
 * Created by huanghaibin on 2017/10/23.
 */

public class ExploreFragment extends BaseFragment implements View.OnClickListener, OnTabReselectListener {

    @Bind(R.id.iv_has_location)
    ImageView mIvLocated;
    @Bind(R.id.viewStatusBar)
    View mStatusBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explore_v2;
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
    public void onResume() {
        super.onResume();
        hasLocation();
    }

    @OnClick({R.id.rl_git, R.id.rl_gits,
            R.id.btn_search,R.id.rl_zb,
            R.id.rl_soft, R.id.rl_scan,
            R.id.rl_shake, R.id.layout_events,
            R.id.layout_nearby})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_search:
                //SearchActivity.show(getActivity());
                SearchActivity.show(mContext);
                break;
            case R.id.rl_zb:
                ZBWebActivity.show(mContext,"https://zb.oschina.net/projects/list.html");
                break;
            case R.id.rl_git:
                FeatureActivity.show(getActivity());
                break;
            case R.id.rl_gits:
                GistActivity.show(mContext);
                break;
            case R.id.rl_soft: //开源软件
                UIHelper.showSimpleBack(getActivity(),
                        SimpleBackPage.OPEN_SOURCE_SOFTWARE);
                break;
            case R.id.rl_scan: //扫一扫
                UIHelper.showScanActivity(getActivity());
                break;
            case R.id.rl_shake: //摇一摇
                showShake();
                break;
            case R.id.layout_events: //线下活动
                SubTab tab = new SubTab();

                SubTab.Banner banner = tab.new Banner();
                banner.setCatalog(3);
                banner.setHref("https://www.oschina.net/action/apiv2/banner?catalog=3");
                tab.setBanner(banner);

                tab.setName("线下活动");
                tab.setFixed(false);
                tab.setHref("https://www.oschina.net/action/apiv2/sub_list?token=727d77c15b2ca641fff392b779658512");
                tab.setNeedLogin(false);
                tab.setSubtype(1);
                tab.setOrder(74);
                tab.setToken("727d77c15b2ca641fff392b779658512");
                tab.setType(5);

                SubActivity.show(mContext, tab);
                break;
            case R.id.layout_nearby:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(getContext());
                    break;
                }
                //NearbyActivity.show(getContext());
                NearbyActivity.show(mContext);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabReselect() {
        hasLocation();
    }

    private void showShake() {
        ShakePresentActivity.show(getActivity());
    }

    private void hasLocation() {
        boolean hasLocation = Setting.hasLocation(getContext());
        if (hasLocation) {
            mIvLocated.setVisibility(View.VISIBLE);
        } else {
            mIvLocated.setVisibility(View.GONE);
        }
    }
}
