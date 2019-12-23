package net.oschina.app.improve.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import net.oschina.app.improve.media.Util;

/**
 * 自适应的NestedScrollView
 * Created by huanghaibin on 2017/11/16.
 */

public class AutoScrollView extends NestedScrollView {
    public AutoScrollView(Context context) {
        this(context,null);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Util.getScreenWidth(getContext()), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
