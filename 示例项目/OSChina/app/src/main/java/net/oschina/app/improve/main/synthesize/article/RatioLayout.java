package net.oschina.app.improve.main.synthesize.article;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.oschina.app.R;

/**
 * 按缩放比显示的布局
 * Created by huanghaibin on 2017/11/14.
 */

public class RatioLayout extends FrameLayout {
    private float mRatio = 1.0f;
    private int mFlag;
    private static final int FLAG_WIDTH = 1;
    private static final int FLAG_HEIGHT = 2;
    private int mRatioHeight;

    public RatioLayout(@NonNull Context context) {
        this(context, null);
    }

    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mRatio = array.getFloat(R.styleable.RatioLayout_layout_ratio, 1.0f);
        mFlag = array.getInt(R.styleable.RatioLayout_ratio_flag, FLAG_WIDTH);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        if (mFlag == FLAG_HEIGHT) {
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = (int) ((float) height * mRatio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int) ((float) width / mRatio);
            mRatioHeight = height;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getRatioHeight() {
        return mRatioHeight;
    }
}
