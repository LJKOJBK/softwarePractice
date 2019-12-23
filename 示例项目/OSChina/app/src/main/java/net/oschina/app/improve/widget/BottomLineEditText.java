package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;

import net.oschina.app.R;
import net.oschina.app.improve.media.Util;

/**
 * 自定义下划线
 * Created by huanghaibin on 2017/8/22.
 */
@SuppressWarnings("unused")
public class BottomLineEditText extends AppCompatEditText implements TextWatcher {
    private Paint mPaint = new Paint();
    private int mMaxCount;
    private boolean isShowCount;
    private Paint mTextPaint = new Paint();

    public BottomLineEditText(Context context) {
        this(context, null);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public BottomLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomLineEditText);
        setBackgroundColor(Color.TRANSPARENT);
        int mLineColor = array.getColor(R.styleable.BottomLineEditText_line_color, 0xFF24cf5f);
        float mLineHeight = array.getDimension(R.styleable.BottomLineEditText_line_height, 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mLineHeight);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mLineColor);

        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(mLineHeight);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(Util.dipTopx(context, 12));
        mTextPaint.setColor(mLineColor);

        mMaxCount = array.getInt(R.styleable.BottomLineEditText_max_count, 16);
        isShowCount = array.getBoolean(R.styleable.BottomLineEditText_is_show_count, true);
        array.recycle();
        addTextChangedListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float h = getHeight();
        int w = getWidth();
        int paddingRight = getPaddingRight();
        int length = getText().toString().length();
        canvas.drawLine(0, h - 1, w, h - 1, mPaint);
        canvas.drawText(String.valueOf(mMaxCount - length), w - paddingRight, h - 30, mTextPaint);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void setMaxCount(int mMaxCount) {
        this.mMaxCount = mMaxCount;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxCount)});
    }

    public void setShowCount(boolean showCount) {
        isShowCount = showCount;
    }
}
