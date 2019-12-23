package net.oschina.app.improve.nearby;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarUploadInfo;

import net.oschina.app.AppContext;
import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.NearbyResult;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.util.TDevice;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 寻找附近的程序员
 * Created by huanghaibin on 2018/3/15.
 */

class NearbyPresenter implements NearbyContract.Presenter {
    private final NearbyContract.View mView;
    private final User mUser;
    private static final String CHARSET = "UTF-8";
    private boolean mIsFirstLocation = true;
    BaiDuLocation mLocationManager;
    BDRadarManager mRadarManager;

    private int mNextPageIndex = 0;

    private LatLng mUserLatLng;

    NearbyPresenter(NearbyContract.View mView, User mUser) {
        this.mView = mView;
        this.mUser = mUser;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {

        if (mUserLatLng == null || (mUserLatLng.latitude == 4.9E-324 && mUserLatLng.longitude == 4.9E-324)) {
            mLocationManager.start();
            mView.onComplete();
            return;
        }
        mRadarManager.requestNearby(mUserLatLng, 0);
        mIsFirstLocation = false;
    }

    @Override
    public void onLoadMore() {
        if (mUserLatLng == null || (mUserLatLng.latitude == 4.9E-324 && mUserLatLng.longitude == 4.9E-324)) {
            mLocationManager.start();
            mView.onComplete();
            return;
        }
        mRadarManager.requestNearby(mUserLatLng, mNextPageIndex);
        mIsFirstLocation = false;
    }


    @Override
    public void updateNearbyInfoList(RadarNearbyResult result) {
        if (result == null) {
            mView.showNotMore();
            mView.onComplete();
            return;
        }
        Log.e("updateNearbyInfoList", " --- ");
        //pageNum==0，表示初始化数据，有可能是刷新，也有可能是第一次加载
        List<RadarNearbyInfo> infoList = result.infoList;
        if (infoList == null || infoList.size() == 0) {
            mView.showNotMore();
            return;
        }
        List<NearbyResult> items = new ArrayList<>();
        int pageIndex = result.pageIndex;
        if (pageIndex == 0) {//第一次，第一页

            Log.e("updateNearbyInfoList", " ---  0  ----  ");
            //没有缓存数据，直接添加
            for (RadarNearbyInfo info : infoList) {
                User user = null;
                try {
                    String comments = URLDecoder.decode(info.comments, CHARSET);
                    user = AppOperator.createGson().fromJson(comments, User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (user == null || (user.getId() == 0 && TextUtils.isEmpty(user.getName())))
                    continue;

                NearbyResult.Nearby nearby = new NearbyResult.Nearby();
                nearby.setDistance(info.distance);
                nearby.setMobileName(info.mobileName);
                nearby.setMobileOS(info.mobileOS);
                items.add(new NearbyResult(user, nearby));
            }
            mNextPageIndex = 1;
            mView.onRefreshSuccess(items);
        } else {
            Log.e("updateNearbyInfoList", " ---  index  ----  " + pageIndex);
            for (RadarNearbyInfo info : infoList) {
                User user = null;
                try {
                    String comments = URLDecoder.decode(info.comments, CHARSET);
                    user = AppOperator.createGson().fromJson(comments, User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (user == null || (user.getId() == 0 && TextUtils.isEmpty(user.getName())))
                    continue;

                int index = containsFriend(user, items);

                if (index == -1) {
                    NearbyResult.Nearby nearby = new NearbyResult.Nearby();
                    nearby.setDistance(info.distance);
                    nearby.setMobileName(info.mobileName);
                    nearby.setMobileOS(info.mobileOS);
                    items.add(new NearbyResult(user, nearby));
                }
            }
            mNextPageIndex += 1;
            mView.onLoadMoreSuccess(items);
        }
        mView.onComplete();
    }

    /**
     * check is cache
     *
     * @param user load_user
     * @return isCache?index:-1
     */
    private int containsFriend(User user, List<NearbyResult> items) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUser().getId() == user.getId()) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onReceiveLocation(Context context, BDLocation location) {
        final int code = location.getLocType();
        switch (code) {
            case BDLocation.TypeCriteriaException://62
                mLocationManager.start();
                mView.showLocationError(R.string.no_location_hint);
                mView.onComplete();
                return;
            case BDLocation.TypeNetWorkException://63
                mView.showLocationError(R.string.network_exception_hint);
                mView.onComplete();
                return;
            case BDLocation.TypeServerError://167
                mView.showLocationError(R.string.server_no_have_permission_hint);
                mView.onComplete();
                return;
            case BDLocation.TypeNetWorkLocation://161
                //mView.showLocationError(R.string.tip_network_error);
                break;
            case BDLocation.TypeOffLineLocation://66  离线模式
                mView.showLocationError(R.string.tip_network_error);
                break;
        }

        if (code >= 501) {
            mView.showLocationError(R.string.key_is_invalid_hint);
            return;
        }

        if (TDevice.hasInternet() && location.getLatitude() != 4.9E-324 && location.getLongitude() != 4.9E-324) {

            boolean started = mLocationManager.isStart();

            if (started) {
                mLocationManager.stop();
            }

            mUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            Setting.updateLocationPermission(context, true);

            //周边雷达设置用户身份标识，id为空默认是设备标识
            String userId;

            //上传位置
            RadarUploadInfo info = new RadarUploadInfo();

            userId = String.valueOf(mUser.getId());

            try {
                SampleAuthor author = new SampleAuthor(mUser);
                String authorJson = AppOperator.getGson().toJson(author);
                info.comments = URLEncoder.encode(authorJson, CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                mView.showLocationError(R.string.upload_lbs_info_hint);
                return;
            }
            mRadarManager.setUserId(userId);
            info.pt = mUserLatLng;
            Log.e("定位成功", "  --  " + mUserLatLng.toString());
            mRadarManager.uploadInfoRequest(info);
            mRadarManager.requestNearby(mUserLatLng, 0);
        } else {
            mView.showLocationError(R.string.tip_network_error);
        }
    }


    @Override
    public void onGetUploadState(RadarSearchError error) {
        switch (error) {
            case RADAR_NETWORK_ERROR:
            case RADAR_NETWORK_TIMEOUT:
                if (mNextPageIndex == 0) {
                    mView.showUploadError(R.string.network_timeout_hint);
                } else {
                    AppContext.showToastShort(R.string.request_error_hint);
                }
                mView.showUploadError(R.string.upload_lbs_info_hint);
                break;
            case RADAR_NO_ERROR:
                if (mIsFirstLocation) {
                    Setting.updateLocationInfo(OSCApplication.getInstance(), true);
                    onRefreshing();
                }
                break;
            case RADAR_PERMISSION_UNFINISHED:
                //ShowSettingDialog();
                break;
        }
    }

    @Override
    public void onRelease() {
        mRadarManager.release();
        mLocationManager.release();
    }

    /**
     * 定位信息是否有效
     *
     * @return 定位信息是否有效
     */
    private boolean isLatlngEnable() {
        return mUserLatLng != null && (mUserLatLng.latitude == 4.9E-324 && mUserLatLng.longitude == 4.9E-324);
    }

    private static class SampleAuthor {
        public long id;
        public String name;
        public String portrait;
        public int gender;
        Author.Identity identity;
        public SampleAuthorMore more;

        private SampleAuthor(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.portrait = user.getPortrait();
            this.gender = user.getGender();
            this.identity = user.getIdentity();
            this.more = new SampleAuthorMore(user.getMore() != null ? user.getMore().getCompany() : "");
            if (this.identity == null)
                this.identity = new Author.Identity();
        }
    }


    private static class SampleAuthorMore {
        String company;

        private SampleAuthorMore(String company) {
            company = company == null ? "" : company;
            this.company = company;
        }
    }
}
