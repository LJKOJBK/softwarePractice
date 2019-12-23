package net.oschina.app.improve.main.update;

import android.content.Context;
import android.text.TextUtils;

/**
 * 应用存储
 * Created by huanghaibin on 2017/6/27.
 */
@SuppressWarnings("unused")
public final class OSCSharedPreference extends SharedPreferenceUtil {

    private static OSCSharedPreference mInstance;

    public static void init(Context context, String name) {
        if (mInstance == null) {
            mInstance = new OSCSharedPreference(context, name);
        }
    }

    public static OSCSharedPreference getInstance() {
        return mInstance;
    }

    private OSCSharedPreference(Context context, String name) {
        super(context, name);
    }

    /**
     * 点击更新过的版本
     */
    void putUpdateVersion(int code) {
        put("osc_update_code", code);
    }

    /**
     * 设置更新过的版本
     */
    public int getUpdateVersion() {
        return getInt("osc_update_code", 0);
    }

    /**
     * 设置不弹出更新
     */
    public void putShowUpdate(boolean isShow) {
        put("osc_update_show", isShow);
    }

    /**
     * 是否弹出更新
     * 或者是新版本重新更新 259200000
     */
    boolean isShowUpdate() {
        return getBoolean("osc_update_show", true);
    }

    /**
     * 是否已经弹出更新
     *
     * @return 不弹出更新代表已经更新
     */
    public boolean hasShowUpdate() {
        return !getBoolean("osc_update_show", true);
    }

    /**
     * 设备唯一标示
     *
     * @param id id
     */
    public void putDeviceUUID(String id) {
        put("osc_device_uuid", id);
    }

    /**
     * 设备唯一标示
     */
    public String getDeviceUUID() {
        return getString("osc_device_uuid", "");
    }


    /**
     * 第一次安装
     */
    public void putFirstInstall() {
        put("osc_first_install", false);
    }

    /**
     * 第一次安装
     */
    public boolean isFirstInstall() {
        return getBoolean("osc_first_install", true);
    }

    /**
     * 第一次使用
     */
    public void putFirstUsing() {
        put("osc_first_using_v2", false);
    }

    /**
     * 第一次使用
     */
    public boolean isFirstUsing() {
        return getBoolean("osc_first_using_v2", true);
    }


    /**
     * putLastNewsId
     *
     * @param id id
     */
    public void putLastNewsId(long id) {
        put("last_news_id", id);
    }

    /**
     * 获取最新的id
     *
     * @return return
     */
    public long getLastNewsId() {
        return getLong("last_news_id", 0);
    }


    /**
     * 返回新闻有多少
     */
    public long getTheNewsId() {
        return getLong("the_last_news_id", 0);
    }

    /**
     * 存储最新新闻数量
     */
    public void putTheNewsId(long count) {
        put("the_last_news_id", count);
    }


    /**
     * 关联剪切版
     *
     * @param isRelate isRelate
     */
    public void putRelateClip(boolean isRelate) {
        put("osc_is_relate_clip", isRelate);
    }

    /**
     * 是否关联剪切版
     *
     * @return 是否关联剪切版
     */
    public boolean isRelateClip() {
        return getBoolean("osc_is_relate_clip", true);
    }

    /**
     * 最后一次分享的url
     *
     * @param url 最后一次分享的url
     */
    public void putLastShareUrl(String url) {
        if (TextUtils.isEmpty(url))
            return;
        put("osc_last_share_url", url);
    }

    /**
     * 最后一次分享的url
     *
     * @return 最后一次分享的url
     */
    public String getLastShareUrl() {
        return getString("osc_last_share_url", "");
    }


    public boolean isFirstOpenUrl() {
        return getBoolean("osc_is_first_open_url", true);
    }

    public void putFirstOpenUrl() {
        put("osc_is_first_open_url", false);
    }
}
