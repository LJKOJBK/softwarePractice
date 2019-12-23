package net.oschina.app.improve.main.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import net.oschina.app.BuildConfig;
import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.TDevice;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 新版在线更新界面
 * Created by huanghaibin on 2018/4/23.
 */

public class UpdateActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_update_info)
    TextView mTextUpdateInfo;
    private Version mVersion;
    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    public static void show(Activity activity, Version version) {
        Intent intent = new Intent(activity, UpdateActivity.class);
        intent.putExtra("version", version);
        activity.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_update;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        super.initData();
        setTitle("");
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mVersion = (Version) getIntent().getSerializableExtra("version");
        mTextUpdateInfo.setText(Html.fromHtml(mVersion.getMessage()));
    }

    @OnClick({R.id.btn_update, R.id.btn_close, R.id.btn_not_show})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (!TDevice.isWifiOpen()) {
                    DialogHelper.getConfirmDialog(this, "当前非wifi环境，是否升级？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkPermission();
                        }
                    }).show();
                } else {
                    checkPermission();
                }
                break;
            case R.id.btn_not_show:
                OSCSharedPreference.getInstance().putShowUpdate(false);
                OSCSharedPreference.getInstance().putUpdateVersion(BuildConfig.VERSION_CODE);
                finish();
                break;
            case R.id.btn_close:
                finish();
                break;
        }

    }

    @SuppressLint("InlinedApi")
    private void checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_EXTERNAL_STORAGE);
        } else {
            DownloadService.startService(this, mVersion.getDownloadUrl());
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DownloadService.startService(this, mVersion.getDownloadUrl());
                finish();
            } else {
                DialogHelper.getConfirmDialog(this, "温馨提示",
                        "需要开启开源中国对您手机的存储权限才能下载安装，是否现在开启",
                        "去开启", "取消",
                        true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        }
    }
}