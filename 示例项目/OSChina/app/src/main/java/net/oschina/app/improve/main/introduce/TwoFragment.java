package net.oschina.app.improve.main.introduce;

import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.main.MainActivity;

import butterknife.OnClick;

/**
 * 介绍页2
 * Created by huanghaibin on 2017/11/24.
 */

public class TwoFragment extends BaseFragment {

    static TwoFragment newInstance() {
        return new TwoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_two;
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.btn_introduce})
    public void onClick(View view) {
        MainActivity.show(mContext);
        getActivity().finish();
    }
}
