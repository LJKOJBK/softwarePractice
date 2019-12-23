package net.oschina.app.improve.main.introduce;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import net.oschina.app.improve.media.Util;

/**
 * 贝塞尔曲线布局
 * Created by huanghaibin on 2017/11/24.
 */

public class BezierLayout extends View{
    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    public BezierLayout(Context context) {
        this(context,null);
    }

    public BezierLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(0xff24cf5f);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.lineTo(0, Util.dipTopx(getContext(),200));
        mPath.quadTo(Util.dipTopx(getContext(),70),
                Util.dipTopx(getContext(),400),
                getWidth(),
                Util.dipTopx(getContext(),160));
        mPath.lineTo(getWidth(),0);
        canvas.drawPath(mPath, mPaint);
    }
}
