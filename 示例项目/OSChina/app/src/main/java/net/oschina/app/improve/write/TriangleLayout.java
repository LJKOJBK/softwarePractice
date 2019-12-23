package net.oschina.app.improve.write;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 例文评论布局,三角形下标
 * Created by huanghaibin on 2017/7/12.
 */

public class TriangleLayout extends View {

    protected Paint mTextPaint;

    public TriangleLayout(Context context) {
        this(context, null);
    }

    public TriangleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFF000000);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") Path path = new Path();
        path.lineTo(0, 0);
        path.lineTo(getMeasuredWidth(), 0);
        path.lineTo(getMeasuredWidth() / 2, getHeight());
        path.close();
        canvas.drawPath(path, mTextPaint);
    }
}
