package net.oschina.app.improve.main.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.BuildConfig;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.utils.DialogHelper;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/19.
 */
public class CheckUpdateManager {


    private ProgressDialog mWaitDialog;
    private Context mContext;
    private boolean mIsShowDialog;

    public CheckUpdateManager(Context context, boolean showWaitingDialog) {
        this.mContext = context;
        mIsShowDialog = showWaitingDialog;
        if (mIsShowDialog) {
            mWaitDialog = DialogHelper.getProgressDialog(mContext);
            mWaitDialog.setMessage("正在检查中...");
            mWaitDialog.setCancelable(true);
            mWaitDialog.setCanceledOnTouchOutside(true);
        }
    }


    public void checkUpdate(final boolean isHasShow) {
        if (mIsShowDialog) {
            mWaitDialog.show();
        }
        OSChinaApi.checkUpdate(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    if (mIsShowDialog) {
                        DialogHelper.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
                    }
                    if (mWaitDialog != null) {
                        mWaitDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<List<Version>> bean = AppOperator.createGson()
                            .fromJson(responseString, new TypeToken<ResultBean<List<Version>>>() {
                            }.getType());
                    if (bean != null && bean.isSuccess()) {
                        List<Version> versions = bean.getResult();
                        if (versions.size() > 0) {
                            final Version version = versions.get(0);
                            int curVersionCode = BuildConfig.VERSION_CODE;
                            int code = Integer.parseInt(version.getCode());
                            if (curVersionCode < code) {
                                //是否弹出更新
                                if (OSCSharedPreference.getInstance().isShowUpdate() || isHasShow) {
                                    UpdateActivity.show((Activity) mContext, version);
                                }
                            } else {
                                if (mIsShowDialog) {
                                    DialogHelper.getMessageDialog(mContext, "已经是新版本了").show();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mWaitDialog != null) {
                    mWaitDialog.dismiss();
                }
            }
        });
    }

    @SuppressWarnings("all")
    public void setCaller(RequestPermissions caller) {
        RequestPermissions mCaller = caller;
    }

    public interface RequestPermissions {
        void call(Version version);
    }
}
