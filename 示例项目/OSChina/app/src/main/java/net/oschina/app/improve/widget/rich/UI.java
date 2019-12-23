package net.oschina.app.improve.widget.rich;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * 辅助类
 * Created by huanghaibin on 2017/8/3.
 */
@SuppressWarnings("unused")
final class UI {

    /**
     * 获得屏幕的宽度
     *
     * @param context context
     * @return width
     */
     static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
         assert manager != null;
         Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    /**
     * 获得屏幕的高度
     *
     * @param context context
     * @return height
     */
     static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
         assert manager != null;
         Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context context
     * @param pxValue px
     * @return dp
     */
    static float pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    static float getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
