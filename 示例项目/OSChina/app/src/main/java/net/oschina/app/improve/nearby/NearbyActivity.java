package net.oschina.app.improve.nearby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;

import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.NearbyResult;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.BottomDialog;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;

import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 寻找附近的程序员
 * Created by huanghaibin on 2018/3/15.
 */

public class NearbyActivity extends BackActivity implements
        View.OnClickListener,
        RadarSearchListener,
        BDLocationListener,
        NearbyContract.View,
        EasyPermissions.PermissionCallbacks,
        RecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseRecyclerAdapter.OnItemClickListener {

    private static final int LC = 0x3;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    private BottomDialog mSelectorDialog;

    private NearbyAdapter mAdapter;

    private NearbyPresenter mPresenter;

    public static void show(Context context) {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(context);
            return;
        }
        Intent intent = new Intent(context, NearbyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_nearby_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NearbyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setOnItemClickListener(this);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter = new NearbyPresenter(this, AccountHelper.getUser());
        mPresenter.mLocationManager = new BaiDuLocation(this, this);
        mPresenter.mRadarManager = new BDRadarManager(this);
        tryLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_opt:
                mPresenter.mRadarManager.clear();
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                break;
            case R.id.tv_cancel_opt:
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                break;
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        NearbyResult result = mAdapter.getItem(position);
        if (result == null) return;
        OtherUserHomeActivity.show(this, result.getUser());
    }

    @Override
    public void onRefreshing() {
        if (mPresenter == null)
            return;
        mPresenter.onRefreshing();
    }

    @Override
    public void onLoadMore() {
        if (mPresenter == null)
            return;
        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true);
        mPresenter.onLoadMore();
    }

    @Override
    public void onScrollToBottom() {
        // TODO: 2018/3/15
    }

    /**
     * 定位
     */
    @SuppressLint("ObsoleteSdkInt")
    @AfterPermissionGranted(LC)
    private void tryLocation() {
        String[] perms;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        } else {
            perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }
        if (EasyPermissions.hasPermissions(this, perms)) {
            mPresenter.mLocationManager.start();
        } else {
            EasyPermissions.requestPermissions(this, "使用地理位置需要获取权限，请提供权限",
                    LC, perms);
        }
    }

    /**
     * 接收了定位信息，处理
     *
     * @param bdLocation bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        mPresenter.onReceiveLocation(this, bdLocation);
    }

    /**
     * 雷达定位结果
     *
     * @param radarNearbyResult radarNearbyResult
     * @param radarSearchError  radarSearchError
     */
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
        mPresenter.updateNearbyInfoList(radarNearbyResult);
    }

    /**
     * 雷达上传结果
     *
     * @param radarSearchError radarSearchError
     */
    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        mPresenter.onGetUploadState(radarSearchError);
    }

    /**
     * 清除雷达定位
     *
     * @param radarSearchError radarSearchError
     */
    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {
        if (isDestroy())
            return;
        switch (radarSearchError) {
            case RADAR_NO_RESULT://未上传有雷达信息
                Setting.updateLocationInfo(getApplicationContext(), false);
                break;
            case RADAR_NO_ERROR://清除雷达信息成功
                Setting.updateLocationInfo(getApplicationContext(), false);
                supportFinishAfterTransition();
                break;
            default:
                SimplexToast.show(this, getString(R.string.clear_bodies_failed_hint));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nearby_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_more:
                getSelectorDialog().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog getSelectorDialog() {
        if (mSelectorDialog == null) {
            mSelectorDialog = new BottomDialog(this, true);
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.view_nearby_operator, null, false);
            view.findViewById(R.id.tv_clear_opt).setOnClickListener(this);
            view.findViewById(R.id.tv_cancel_opt).setOnClickListener(this);
            mSelectorDialog.setContentView(view);
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.setBackgroundResource(R.color.transparent);
            }
        }
        return mSelectorDialog;
    }

    @Override
    public void showLocationError(int strId) {
        if (isDestroy())
            return;
        SimplexToast.show(this, strId);
    }

    @Override
    public void showUploadError(int strId) {
        if (isDestroy())
            return;
        SimplexToast.show(this, strId);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this,
                "去设置权限",
                "使用地理位置需要获取权限, 你需要去[设置-应用-开源中国]中设置权限.否则无法使用附近程序员功能",
                "去设置", "取消", false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void onRefreshSuccess(List<NearbyResult> data) {
        if (isDestroy())
            return;
        mAdapter.resetItem(data);
    }

    @Override
    public void onLoadMoreSuccess(List<NearbyResult> data) {
        if (isDestroy())
            return;
        mAdapter.addAll(data);
    }

    @Override
    public void showNotMore() {
        if (isDestroy())
            return;
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }

    @Override
    public void onComplete() {
        mRefreshLayout.onComplete();
    }

    @Override
    public void setPresenter(NearbyContract.Presenter presenter) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onRelease();
    }

}
