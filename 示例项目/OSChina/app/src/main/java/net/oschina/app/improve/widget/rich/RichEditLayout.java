package net.oschina.app.improve.widget.rich;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 富文本编辑器入口,包含底部工具栏
 * Created by huanghaibin on 2017/8/3.
 */

@SuppressWarnings("unused")
public class RichEditLayout extends LinearLayout {
    private RichScrollView mScrollView;
    RichBar mRichBar;
    View mContentPanel;//底部内容布局
    boolean isKeyboardOpen = true;
    public static int KEYBOARD_HEIGHT = 0;

    public RichEditLayout(Context context) {
        this(context, null);
    }

    public RichEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mScrollView = new RichScrollView(context);
        LinearLayout.LayoutParams params =
                new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        mScrollView.setLayoutParams(params);
        addView(mScrollView);
        mRichBar = new RichBar(getContext());
        LinearLayout.LayoutParams richParams =
                new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mRichBar.setLayoutParams(richParams);
        addView(mRichBar);

//        if (KEYBOARD_HEIGHT != 0) {
//            mContentPanel.getLayoutParams().height = Keyboard.KEYBOARD_HEIGHT;
//        }
        final Activity activity = (Activity) context;
        final View rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                int statusBarHeight = 0;
                if (heightDiff > 200) {
                    try {
                        @SuppressLint("PrivateApi")
                        Class<?> c = Class.forName("com.android.internal.R$dimen");
                        Object obj = c.newInstance();
                        Field field = c.getField("status_bar_height");
                        int x = Integer.parseInt(field.get(obj).toString());
                        statusBarHeight = getResources().getDimensionPixelSize(x);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int h = heightDiff - statusBarHeight;
                    if (KEYBOARD_HEIGHT < h) {
                        KEYBOARD_HEIGHT = h;
                        if (mContentPanel != null)
                            mContentPanel.getLayoutParams().height = KEYBOARD_HEIGHT;
                    }
                }
            }
        });
    }

    public void insertImagePanel(String image) {
        mScrollView.addImagePanel(image);
    }

    public void setContentPanel(View view) {
        this.mContentPanel = view;
    }

    public void setBold(boolean isBold) {
        mScrollView.mRichLinearLayout.setBold(isBold);
    }

    public void setItalic(boolean isItalic) {
        mScrollView.mRichLinearLayout.setItalic(isItalic);
    }

    public void setMidLine(boolean isMidLine) {
        mScrollView.mRichLinearLayout.setMidLine(isMidLine);
    }

    public void setAlignStyle(int align) {
        mScrollView.mRichLinearLayout.setAlignStyle(align);
    }

    public void init(List<Section> sections) {
        mScrollView.mRichLinearLayout.init(sections);
    }

    public void setColorSpan(String color) {
        mScrollView.mRichLinearLayout.setColorSpan(color.replace("#", ""));
    }

    public void setTextSizeSpan(boolean isIncrease) {
        mScrollView.mRichLinearLayout.setTextSizeSpan(isIncrease);
    }

    public void setTextSize(int size) {
        mScrollView.mRichLinearLayout.setTextSize(size);
    }

    public boolean isKeyboardOpen() {
        return (mRichBar.getBottom() < UI.getScreenHeight(getContext()) - UI.dipToPx(getContext(), 80)
                && mContentPanel.getVisibility() == GONE) || isKeyboardOpen;
    }

    public void setKeyboardOpen(boolean isOpen) {
        this.isKeyboardOpen = isOpen;
    }

    public List<TextSection> createSectionList() {
        return mScrollView.createSectionList();
    }

    public int getImageCount() {
        return mScrollView.mRichLinearLayout.getImageCount();
    }

    public void openKeyboard() {
        isKeyboardOpen = true;
        DrawableCompat.setTint(mRichBar.mBtnKeyboard.getDrawable(), 0xff24cf5f);
        mScrollView.mRichLinearLayout.mFocusView.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void closeKeyboard() {
        isKeyboardOpen = false;
        DrawableCompat.setTint(mRichBar.mBtnKeyboard.getDrawable(), 0xff111111);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mScrollView.mRichLinearLayout.mFocusView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void setOnSectionChangeListener(RichEditText.OnSectionChangeListener listener) {
        mScrollView.mRichLinearLayout.mListener = listener;
        mScrollView.mRichLinearLayout.mFocusView.mListener = listener;
    }

    public void setAdjustResize() {
        Window window = ((Activity) getContext()).getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setAdjustNothing() {
        Window window = ((Activity) getContext()).getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    public void setFontTint(int color) {
        DrawableCompat.setTint(mRichBar.mBtnFont.getDrawable(), color);
    }

    public void setCategoryTint(int color) {
        DrawableCompat.setTint(mRichBar.mBtnCategory.getDrawable(), color);
    }


    public void setKeyboardTint(int color) {
        DrawableCompat.setTint(mRichBar.mBtnKeyboard.getDrawable(), color);
    }

    public String getTitle() {
        return mScrollView.mRichLinearLayout.getTitle();
    }

    public String getSummary() {
        return mScrollView.mRichLinearLayout.getSummary();
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(mScrollView.mRichLinearLayout.mFocusView.getText().toString().trim());
    }
}