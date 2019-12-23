package net.oschina.app.improve.nearby;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarNearbySearchSortType;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;

import net.oschina.app.OSCApplication;
import net.oschina.app.Setting;

/**
 * 百度雷达管理器
 * Created by huanghaibin on 2018/3/15.
 */
@SuppressWarnings("unused")
final class BDRadarManager {
    private RadarSearchManager mRadarManager;
    private RadarSearchListener mListener;

    BDRadarManager(RadarSearchListener listener) {
        mRadarManager = RadarSearchManager.getInstance();
        mListener = listener;
        mRadarManager.addNearbyInfoListener(listener);
    }

    /**
     * 查询附近的人
     *
     * @param latLng    用户定位信息
     * @param pageIndex 页码
     */
    void requestNearby(LatLng latLng, int pageIndex) {
        if (mRadarManager == null)
            return;
        //构造请求参数，其中centerPt是自己的位置坐标
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(latLng).pageNum(pageIndex).radius(38000).pageCapacity(50).
                        sortType(RadarNearbySearchSortType.distance_from_far_to_near);
        //发起查询请求
        mRadarManager.nearbyInfoRequest(option);
    }

    void setUserId(String userId) {
        mRadarManager.setUserID(userId);
    }

    void uploadInfoRequest(RadarUploadInfo info) {
        mRadarManager.uploadInfoRequest(info);
    }

    /**
     * 关闭释放资源
     */
    void release() {
        if (mRadarManager != null && mListener != null) {
            mRadarManager.removeNearbyInfoListener(mListener);
            mRadarManager.destroy();
            mRadarManager = null;
        }
    }

    void clear() {
        if (mRadarManager == null) return;
        mRadarManager.clearUserInfo();
        Setting.updateLocationInfo(OSCApplication.getInstance(), false);
    }
}
