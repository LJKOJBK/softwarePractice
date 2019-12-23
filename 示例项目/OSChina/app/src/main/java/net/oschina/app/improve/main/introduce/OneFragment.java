package net.oschina.app.improve.main.introduce;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.main.synthesize.article.RatioLayout;
import net.oschina.app.improve.media.Util;

import butterknife.Bind;

/**
 * 介绍页1
 * Created by huanghaibin on 2017/11/24.
 */

public class OneFragment extends BaseFragment {

    @Bind(R.id.ll_logo)
    LinearLayout mLinearLogo;
    @Bind(R.id.ratioLayout)
    RatioLayout mRatioLayout;

    static OneFragment newInstance() {
        return new OneFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mLinearLogo.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLinearLogo.getLayoutParams();
                params.setMargins(0, (Util.getScreenHeight(mContext) - mRatioLayout.getRatioHeight()) / 4, 0, 0);
                mLinearLogo.setLayoutParams(params);
            }
        });
    }
}
