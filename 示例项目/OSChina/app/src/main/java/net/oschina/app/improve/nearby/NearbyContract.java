package net.oschina.app.improve.nearby;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;

import net.oschina.app.improve.base.BaseListPresenter;
import net.oschina.app.improve.base.BaseListView;
import net.oschina.app.improve.bean.NearbyResult;

/**
 * 附近的程序员
 * Created by huanghaibin on 2018/3/15.
 */

interface NearbyContract {

    interface View extends BaseListView<Presenter, NearbyResult> {
        void showLocationError(int strId);

        void showUploadError(int strId);
    }

    interface Presenter extends BaseListPresenter {
        void onReceiveLocation(Context context, BDLocation location);

        void updateNearbyInfoList(RadarNearbyResult result);

        void onGetUploadState(RadarSearchError error);

        void onRelease();
    }
}
