package net.oschina.app.improve.git.gist.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * 代码片段详情滚动
 * Created by huanghaibin on 2017/7/17.
 */

public class GistWebView extends WebView {
    private float mDownX;
    private boolean isRequsest;

    public GistWebView(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int count = event.getPointerCount();
        if(count >= 2 && !isRequsest){
            requestDisallowInterceptTouchEvent(true);
            isRequsest = true;
            return super.onTouchEvent(event);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                isRequsest = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownX;
                if (Math.abs(dx) >= 20 && !isRequsest) {
                    requestDisallowInterceptTouchEvent(true);
                    isRequsest = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isRequsest = false;
                break;
        }
        return super.onTouchEvent(event);
    }
}
