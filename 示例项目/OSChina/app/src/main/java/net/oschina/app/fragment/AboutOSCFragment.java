package net.oschina.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.BuildConfig;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.UIHelper;
import net.oschina.common.admin.Boss;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutOSCFragment extends BaseFragment {

    @Bind(R.id.tv_version_name)
    TextView mTvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.tv_grade).setOnClickListener(this);
        view.findViewById(R.id.tv_oscsite).setOnClickListener(this);
        view.findViewById(R.id.tv_knowmore).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mTvVersionName.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    @OnClick(R.id.img_portrait)
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.tv_grade:
                openMarket();
                break;
            case R.id.tv_oscsite:
                UIHelper.openInternalBrowser(getActivity(), "https://www.oschina.net");
                break;
            case R.id.tv_knowmore:
                UIHelper.openInternalBrowser(getActivity(),
                        "https://www.oschina.net/home/aboutosc");
                break;
            case R.id.img_portrait:
                Boss.verifyApp(getContext());
                Setting.updateSystemConfigTimeStamp(getContext());
                break;
            default:
                break;
        }
    }

    private void openMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            SimplexToast.show(getActivity(), "请至少安装一个应用商店");
        }
    }
}
