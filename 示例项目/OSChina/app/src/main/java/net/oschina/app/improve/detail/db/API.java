package net.oschina.app.improve.detail.db;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.BuildConfig;

/**
 * 阅读习惯接口
 * Created by haibin on 2017/5/23.
 */
public final class API {
    private static final String URL = "http://192.168.2.64/action/apiv2/push_read_record";

    public static void addBehaviors(String json, TextHttpResponseHandler handler) {
        if(!TextUtils.isEmpty(json)){
            // TODO: 2017/11/6 目前不上传数据
            return;
        }
        RequestParams params = new RequestParams("json", json);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("passcode", BuildConfig.VIOLET_PASSCODE);
        client.addHeader("AppToken", "123456");
        client.post(URL, params, handler);
    }
}
