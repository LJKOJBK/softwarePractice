package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import net.oschina.app.R;

/**
 * 自定义字体
 * Created by huanghaibin on 2017/11/27.
 */

public class OSCTextView extends AppCompatTextView {
    private static final int FONT_TYPE_SYSTEM = 0;

    private static final int FONT_TYPE_LIGHT = 1;

    private static final int FONT_TYPE_MEDIUM = 2;

    private static final int FONT_TYPE_REGULAR = 3;

    private int mFontType = 0;

    public OSCTextView(Context context) {
        this(context, null);
    }

    public OSCTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OSCTextView);
        mFontType = array.getInt(R.styleable.OSCTextView_font_type, FONT_TYPE_SYSTEM);
        array.recycle();
        init(context);
    }

    private void init(Context context) {
        String path = getFont();
        if (TextUtils.isEmpty(path))
            return;
        Typeface type = Typeface.createFromAsset(context.getAssets(), path);
        setTypeface(type);
    }

    private String getFont() {
        String typeface = null;
        switch (mFontType) {
            case FONT_TYPE_SYSTEM:
                break;
            case FONT_TYPE_LIGHT:
                typeface = "fonts/NotoSansSC-Light.otf";
                break;
            case FONT_TYPE_MEDIUM:
                typeface = "fonts/NotoSansSC-Medium.otf";
                break;
            case FONT_TYPE_REGULAR:
                typeface = "fonts/NotoSansSC-Regular.otf";
                break;
            default:
                break;
        }
        return typeface;
    }
}
