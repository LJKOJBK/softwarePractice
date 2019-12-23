package net.oschina.app.improve.main;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 双击
 * Created by huanghaibin on 2018/1/17.
 */

public abstract class OnDoubleTouchListener implements View.OnTouchListener {
    private long lastTouchTime = 0;
    private AtomicInteger touchCount = new AtomicInteger(0);
    private Runnable mRun = null;
    private Handler mHandler;

    public OnDoubleTouchListener() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void removeCallback() {
        if (mRun != null) {
            mHandler.removeCallbacks(mRun);
            mRun = null;
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            final long now = System.currentTimeMillis();
            lastTouchTime = now;

            touchCount.incrementAndGet();
            removeCallback();

            mRun = new Runnable() {
                @Override
                public void run() {
                    if (now == lastTouchTime) {
                        onMultiTouch(v, event, touchCount.get());
                        touchCount.set(0);
                    }
                }
            };

            mHandler.postDelayed(mRun, getMultiTouchInterval());
        }
        return true;
    }


    private int getMultiTouchInterval() {
        return 400;
    }


    public abstract void onMultiTouch(View v, MotionEvent event, int touchCount);
}
