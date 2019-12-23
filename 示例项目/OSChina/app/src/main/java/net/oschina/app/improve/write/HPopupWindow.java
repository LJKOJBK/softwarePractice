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

class HPopupWindow extends PopupWindow implements View.OnClickListener {
    private OnHeaderChangeListener mListener;
    private ImageView mImageH1, mImageH2, mImageH3;

    @SuppressLint("InflateParams")
    HPopupWindow(Context context, OnHeaderChangeListener listener) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_h, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_anim_style_alpha);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(false);
        this.mListener = listener;
        View content = getContentView();

        mImageH1 = (ImageView) content.findViewById(R.id.iv_h1);
        mImageH2 = (ImageView) content.findViewById(R.id.iv_h2);
        mImageH3 = (ImageView) content.findViewById(R.id.iv_h3);

        mImageH1.setOnClickListener(this);
        mImageH2.setOnClickListener(this);
        mImageH3.setOnClickListener(this);
    }

    void setStyle(TextSection section) {
        DrawableCompat.setTint(mImageH1.getDrawable(), 0xFFFFFFFF);
        DrawableCompat.setTint(mImageH2.getDrawable(), 0xFFFFFFFF);
        DrawableCompat.setTint(mImageH3.getDrawable(), 0xFFFFFFFF);
        switch (section.getTextSize()) {
            case 28:
                DrawableCompat.setTint(mImageH1.getDrawable(), 0xff24cf5f);
                break;
            case 24:
                DrawableCompat.setTint(mImageH2.getDrawable(), 0xff24cf5f);
                break;
            case 20:
                DrawableCompat.setTint(mImageH3.getDrawable(), 0xff24cf5f);
                break;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_h1:
                mImageH1.setSelected(!mImageH1.isSelected());
                mListener.onTitleChange(mImageH1.isSelected() ? 28 : 18);
                DrawableCompat.setTint(mImageH1.getDrawable(), mImageH1.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);

                break;
            case R.id.iv_h2:
                mImageH2.setSelected(!mImageH2.isSelected());
                mListener.onTitleChange(mImageH2.isSelected() ? 24 : 18);
                DrawableCompat.setTint(mImageH2.getDrawable(), mImageH2.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);

                break;
            case R.id.iv_h3:
                mImageH3.setSelected(!mImageH3.isSelected());
                mListener.onTitleChange(mImageH3.isSelected() ? 20 : 18);
                DrawableCompat.setTint(mImageH3.getDrawable(), mImageH3.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
        }
    }

    void show(View v) {
        showAsDropDown(v, 0, -2 * v.getMeasuredHeight() + 10);
    }

    interface OnHeaderChangeListener {
        void onTitleChange(int size);
    }
}
