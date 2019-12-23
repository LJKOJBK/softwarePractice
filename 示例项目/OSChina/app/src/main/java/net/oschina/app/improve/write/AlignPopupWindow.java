package net.oschina.app.improve.write;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import net.oschina.app.R;
import net.oschina.app.improve.widget.rich.TextSection;

/**
 * 弹出H对话框
 * Created by huanghaibin on 2017/8/31.
 */

class AlignPopupWindow extends PopupWindow implements View.OnClickListener {

    private ImageView mImageAlignLeft, mImageAlignCenter, mImageAlignRight;
    private OnAlignChangeListener mListener;

    @SuppressLint("InflateParams")
    AlignPopupWindow(Context context, OnAlignChangeListener listener) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_align, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_anim_style_alpha);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //setFocusable(true);
        this.mListener = listener;
        View content = getContentView();
        mImageAlignLeft = (ImageView) content.findViewById(R.id.iv_align_left);
        mImageAlignCenter = (ImageView) content.findViewById(R.id.iv_align_center);
        mImageAlignRight = (ImageView) content.findViewById(R.id.iv_align_right);
        mImageAlignLeft.setOnClickListener(this);
        mImageAlignCenter.setOnClickListener(this);
        mImageAlignRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_align_left:
                mImageAlignLeft.setSelected(!mImageAlignLeft.isSelected());
                mListener.onAlignChange(TextSection.LEFT);
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case R.id.iv_align_center:
                mImageAlignCenter.setSelected(!mImageAlignCenter.isSelected());
                mListener.onAlignChange(TextSection.CENTER);
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case R.id.iv_align_right:
                mImageAlignRight.setSelected(!mImageAlignRight.isSelected());
                mListener.onAlignChange(TextSection.RIGHT);
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xff24cf5f);
                break;
        }
    }

    void setStyle(TextSection section) {
        switch (section.getAlignment()) {
            case TextSection.LEFT:
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case TextSection.CENTER:
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case TextSection.RIGHT:
                DrawableCompat.setTint(mImageAlignLeft.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignCenter.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mImageAlignRight.getDrawable(), 0xff24cf5f);
                break;
        }
    }

    void show(View v) {
        showAsDropDown(v, 0, -2 * v.getMeasuredHeight() + 10);
    }

    interface OnAlignChangeListener {
        void onAlignChange(int align);
    }
}
