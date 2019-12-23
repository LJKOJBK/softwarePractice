package net.oschina.app.improve.widget.rich;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;

/**
 * 图片输入面板
 * Created by huanghaibin on 2017/8/3.
 */

@SuppressWarnings("unused")
public class ImagePanel extends FrameLayout implements
        View.OnClickListener, View.OnLongClickListener {
    private ImageView mImageView;
    private RichLinearLayout mParent;
    public boolean isDeleteMode;
    String mImagePath;
    public ImagePanel(@NonNull Context context) {
        this(context, null);
    }
    public ImagePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_image_panel, this, true);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }
    void setImagePath(String imagePath) {
        mImagePath = imagePath;
        Glide.with(getContext())
                .load(imagePath)
                .asBitmap()
                .fitCenter()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(mImageView);
    }
    /**
     * 显示编辑删除模式
     *
     * @param index View下标
     */
    void showDeleteMode(int index) {
        isDeleteMode = true;
        showMode(true);
    }
    void showMode(boolean isDelete) {
        if (isDelete) {
            setBackgroundResource(R.drawable.bg_image_panel);
            int p = UI.dipToPx(getContext(), 1);
            setPadding(p, p, p, p);
        } else {
            setBackgroundColor(Color.WHITE);
            isDeleteMode = false;
            setPadding(0, 0, 0, 0);
        }
    }
    void clearMode() {
        setFocusable(false);
        setFocusableInTouchMode(false);
        clearFocus();
    }
    void setFocusMode() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }
    @Override
    public void onClick(View v) {
    }
    @Override
    public boolean onLongClick(final View v) {
        //mParent.mFocusView.setFocusable(false);
        //mParent.mFocusView.setFocusableInTouchMode(false);
        //mParent.mFocusView.clearFocus();
        mParent.mParent.mParent.mContentPanel.setVisibility(GONE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        if (mParent != null &&
                mParent.mParent != null &&
                !mParent.mParent.isKeyboardOpen()) {
            openKeyboard(v);
            mParent.mFocusPanel = this;
        }
        return true;
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        isDeleteMode = false;
        if (gainFocus) {
            showMode(true);
        } else {
            showMode(false);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("onKeyDown", "onKeyDown");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                mParent.adjustLayout(this, true);
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (mParent.mFocusPanel != null) {
                    mParent.mFocusPanel.clearMode();
                }
                clearMode();
                mParent.adjustLayout(this, false);
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mParent == null) {
            mParent = (RichLinearLayout) getParent();
        }
    }
    String getFileName() {
        return mImagePath.substring(mImagePath.lastIndexOf("/") + 1);
    }
    private void openKeyboard(View view) {
        //view.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private boolean isOpenKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        return imm.isActive();
    }
}