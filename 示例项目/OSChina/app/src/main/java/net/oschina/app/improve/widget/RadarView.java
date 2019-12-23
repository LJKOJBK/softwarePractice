package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 技能雷达图
 * Created by huanghaibin on 2018/4/9.
 */

public class RadarView extends View {
    private Paint mPathPaint = new Paint();

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
