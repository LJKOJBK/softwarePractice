package net.oschina.app.improve.nearby;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 百度定位
 * Created by huanghaibin on 2018/3/15.
 */
@SuppressWarnings("unused")
final class BaiDuLocation {
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private static final String coorType = "bd09ll";
    //coorType - 取值有3个： 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll
    private static final int scanSpan = 3600000 * 3;//定位间隔

    private LocationClient mLocationClient;

    BaiDuLocation(Context context, BDLocationListener listener) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        initConfig();
        //mLocationClient.registerLocationListener(listener);
        mLocationClient.registerNotifyLocationListener(listener);
    }

    void start() {
        if (!isStart())
            mLocationClient.start();
    }

    boolean isStart() {
        return mLocationClient.isStarted();
    }

    void stop() {
        if (isStart())
            mLocationClient.stop();
    }

    void release() {
        mLocationClient.stop();
    }

    private void initConfig() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(coorType);//返回的定位结果是百度经纬度，默认值bd09ll 设置返回值的坐标类型
        option.setScanSpan(scanSpan);//定位间隔,默认3小时
        option.setIsNeedAddress(true); //是否需要地址
        mLocationClient.setLocOption(option);
    }
}
