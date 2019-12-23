package net.oschina.app.improve.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * 平滑弹出键盘
 * Created by huanghaibin on 2017/12/18.
 */

public class SmoothLayout extends FrameLayout {

    public static int TYPE_FONT = 1;
    public static int TYPE_LETTER_PAGER = 2;
    public static int TYPE_PHOTO = 3;
    public static int TYPE_LIST = 4;
    public static int TYPE_TIME = 5;

    private int mType;

    private EditText mEditText;
    private boolean isKeyboardOpen = false;//键盘是否打开

    public SmoothLayout(@NonNull Context context) {
        this(context, null);
    }

    public SmoothLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void bindEditText(EditText editText) {
        this.mEditText = editText;
    }

    private void init() {
        final Activity activity = (Activity) getContext();
        final Window window = activity.getWindow();
        final View rootView = window.getDecorView();
        window.getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                int statusBarHeight = 0;
                if (heightDiff > 200) {
                    try {
                        @SuppressLint("PrivateApi") Class<?> c = Class.forName("com.android.internal.R$dimen");
                        Object obj = c.newInstance();
                        Field field = c.getField("status_bar_height");
                        int x = Integer.parseInt(field.get(obj).toString());
                        statusBarHeight = getResources().getDimensionPixelSize(x);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int h = heightDiff - statusBarHeight;
                    if (Keyboard.KEYBOARD_HEIGHT < h) {
                        Keyboard.KEYBOARD_HEIGHT = h;
                        getLayoutParams().height = Keyboard.KEYBOARD_HEIGHT;
                    }
                }
            }
        });
    }


    /**
     * 显示或者隐藏面板
     */
    public void showOrClosePanel(int type) {
        setAdjustNothing();
        if (getVisibility() == View.VISIBLE) {
            if (isKeyboardOpen) {
                closeKeyboard();
            } else {
                if (this.mType != type && mType != 0) {
                    this.mType = type;
                    return;
                }
                this.mType = type;
                setVisibility(View.GONE);
                closeKeyboard();
            }
        } else {
            this.mType = type;
            closeKeyboard();
            setVisibility(View.VISIBLE);
        }
    }

    public void openKeyboard() {
        isKeyboardOpen = true;
        Keyboard.openKeyboard(mEditText);
    }

    public void closeKeyboard() {
        isKeyboardOpen = false;
        Keyboard.closeKeyboard(mEditText);
    }

    public void setAdjustResize() {

        if (getVisibility() == View.VISIBLE) {
            setAdjustNothing();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    isKeyboardOpen = true;
                    setVisibility(GONE);
                    setResize();
                }
            }, 500);
            return;
        }
        isKeyboardOpen = true;
        setResize();
    }

    private void setResize() {
        Window window = ((Activity) getContext()).getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setAdjustNothing() {
        Window window = ((Activity) getContext()).getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

}
