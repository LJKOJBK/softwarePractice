package net.oschina.app.improve.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * UI操作相关
 * Created by huanghaibin on 2018/1/18.
 */
@SuppressWarnings("unused")
public final class UI {
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void runDelayed(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }
}
