package net.oschina.app.improve.main.splash;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.Launcher;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.util.UIHelper;

import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 首页启动广告
 * Created by huanghaibin on 2017/11/25.
 */

public class AdFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.countDownView)
    CountDownView mCountDownView;
    private Launcher mLauncher;

    @Bind(R.id.iv_ad)
    ImageView mImageAd;

    private static boolean isClickAd;

    public static AdFragment newInstance(Launcher launcher) {

        AdFragment fragment = new AdFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("launcher", launcher);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ad;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mLauncher = (Launcher) bundle.getSerializable("launcher");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        isClickAd = false;
        Glide.with(mContext)
                .load(OSCApplication.getInstance().getCacheDir() + "/launcher")
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .fitCenter()
                .into(mImageAd);
        mCountDownView.setListener(new CountDownView.OnProgressListener() {
            @Override
            public void onFinish() {
                if (isClickAd || mContext == null)
                    return;
                MainActivity.show(mContext);
                mCountDownView.cancel();
                getActivity().finish();
            }
        });
        mCountDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickAd || mContext == null)
                    return;
                mCountDownView.cancel();
                MainActivity.show(mContext);
                getActivity().finish();
            }
        });
        mCountDownView.start();
    }

    @OnClick({R.id.iv_ad, R.id.iv_logo})
    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(mLauncher.getHref())) {
            return;
        }
        isClickAd = true;
        mCountDownView.cancel();
        UIHelper.showUrlRedirect(mContext, mLauncher.getHref());
        getActivity().finish();
    }
}
